package com.evolveum.polygon.connector.csv;

import org.apache.commons.io.FileUtils;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectReference;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Exercises the read/write overlap through the real ConnId ConnectorFacade instead of
 * calling ObjectClassHandler directly. This is intentionally close to midPoint's
 * get-object followed by REMOVE_ATTR_VALUE execution path, including ConnId buffering.
 */
public class ConcurrentAssociationFacadeReadWriteTest extends BaseTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void selectiveFacadeReadMustNotLeaveUsersCsvLockedForAssociationRemove() throws Exception {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            throw new SkipException("Windows-specific file replacement regression");
        }

        Path users = Path.of("target", "facade-concurrent-users.csv");
        Files.createDirectories(users.getParent());

        StringBuilder csv = new StringBuilder("firstName;uid;lastName;password;memberOf\r\n");
        csv.append("john;123;doe;qwe123;1\r\n");
        for (int i = 0; i < 2000; i++) {
            csv.append("user").append(i)
                    .append(';').append("U").append(i)
                    .append(";doe;;1\r\n");
        }
        Files.writeString(users, csv.toString());

        CsvConfiguration config = createConfigurationNameEqualsUid();
        config.setFilePath(users.toFile());
        config.setTmpFolder(null);
        config.setMultivalueDelimiter(",");
        config.setMultivalueAttributes("memberOf");
        config.setManagedAssociationPairs(new String[]{
                "\"account\"+memberOf -# \"group\"+id",
                "\"group\"+memberOf -# \"group\"+id"
        });

        File groupsProperties = new File("./target/facade-concurrent-groups.properties");
        FileUtils.copyFile(new File(TEMPLATE_FOLDER_PATH + "/groups-memberOf.properties"), groupsProperties);
        config.setObjectClassDefinition(groupsProperties);

        File groupsCsv = new File("./target/facade-concurrent-groups.csv");
        FileUtils.copyFile(new File(TEMPLATE_FOLDER_PATH + "/groups-memberOf.csv"), groupsCsv);

        // The object-class definition points at the standard test groups CSV name.
        // Keep that file in sync with the isolated test copy so the secondary handler
        // resolves the same fixture data without sharing the users file.
        FileUtils.copyFile(groupsCsv, new File("./target/groups-memberOf.csv"));

        config.validate();
        ConnectorFacade connector = createNewInstance(config);

        // getObject is the important part: ConnId implements it using the search path
        // and may return to the consumer before the buffered producer has terminated.
        Assert.assertNotNull(connector.getObject(ObjectClass.ACCOUNT, new Uid("123"), null));

        ConnectorObjectBuilder referencedBuilder = new ConnectorObjectBuilder();
        referencedBuilder.setObjectClass(GROUP);
        referencedBuilder.setUid("1");
        referencedBuilder.setName(new Name("1"));
        ConnectorObjectReference reference = new ConnectorObjectReference(referencedBuilder.build());
        Set<Attribute> attributes = Set.of(AttributeBuilder.build(ASSOC_ATTR_GROUP, reference));

        Uid updated = connector.removeAttributeValues(ObjectClass.ACCOUNT, new Uid("123"), attributes,
                new OperationOptionsBuilder().build());
        Assert.assertEquals(updated, new Uid("123"));

        String after = Files.readString(users);
        Assert.assertTrue(after.contains("john;123;doe;qwe123;"),
                "Association removal was not persisted to users CSV");
    }
}
