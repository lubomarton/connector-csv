package com.evolveum.polygon.connector.csv;

import com.evolveum.polygon.connector.csv.util.AssociationCharacter;
import com.evolveum.polygon.connector.csv.util.AssociationHolder;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectReference;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SameClassAssociationTraversalTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void resolvesTwoLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-two-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n", 2);
    }

    @Test
    public void resolvesTwoLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-two-reverse.csv",
                "id;memberOf\r\n2;1\r\n1;\r\n", 2);
    }

    @Test
    public void resolvesThreeLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-three-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n3;2\r\n", 3);
    }

    @Test
    public void resolvesThreeLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-three-reverse.csv",
                "id;memberOf\r\n3;2\r\n2;1\r\n1;\r\n", 3);
    }

    @Test
    public void resolvesFourLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n3;2\r\n4;3\r\n", 4);
    }

    @Test
    public void resolvesFourLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-reverse.csv",
                "id;memberOf\r\n4;3\r\n3;2\r\n2;1\r\n1;\r\n", 4);
    }

    private void assertChainResolved(String fileName, String content, int length) throws Exception {
        Path path = Path.of("target", fileName);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);

        ObjectClassHandler handler = handler(path);
        List<ConnectorObject> results = new ArrayList<>();
        handler.executeQuery(GROUP, null, object -> {
            results.add(object);
            return true;
        }, null);

        Assert.assertEquals(results.size(), length);
        assertNoParent(results, "1");
        for (int i = 2; i <= length; i++) {
            assertParent(results, Integer.toString(i), Integer.toString(i - 1));
        }
    }

    private void assertNoParent(List<ConnectorObject> results, String uid) {
        ConnectorObject object = object(results, uid);
        Attribute references = object.getAttributeByName("group");
        Assert.assertTrue(references == null || references.getValue() == null || references.getValue().isEmpty());
    }

    private void assertParent(List<ConnectorObject> results, String uid, String expectedParentUid) {
        ConnectorObject object = object(results, uid);
        Attribute references = object.getAttributeByName("group");
        Assert.assertNotNull(references, "Missing group reference for " + uid + " -> " + expectedParentUid);
        Assert.assertEquals(references.getValue().size(), 1);
        ConnectorObjectReference reference = (ConnectorObjectReference) references.getValue().get(0);
        Assert.assertEquals(reference.getValue().getAttributeByName(Uid.NAME), new Uid(expectedParentUid));
    }

    private ConnectorObject object(List<ConnectorObject> results, String uid) {
        return results.stream()
                .filter(object -> uid.equals(object.getUid().getUidValue()))
                .findFirst()
                .orElseThrow();
    }

    private ObjectClassHandler handler(Path path) {
        ObjectClassHandlerConfiguration configuration = new ObjectClassHandlerConfiguration(GROUP, null);
        configuration.setFilePath(path.toFile());
        configuration.setUniqueAttribute("id");
        configuration.setNameAttribute("id");
        configuration.setPasswordAttribute(null);
        configuration.setMultivalueDelimiter(",");
        configuration.setMultivalueAttributes("memberOf");
        configuration.setManagedAssociationPairs(new String[]{"group memberOf group"});
        configuration.recompute();

        ObjectClassHandler handler = new ObjectClassHandler(configuration);
        AssociationHolder association = new AssociationHolder();
        association.setReferenceName("group");
        association.setAssociationAttributeName("memberOf");
        association.setValueAttributeName("id");
        association.setSubjectObjectClassName("group");
        association.setObjectObjectClassName("group");
        association.setCharacter(AssociationCharacter.OBTAINS);
        handler.associationHolders = Map.of("group", new HashSet<>(Set.of(association)));
        handler.setHandlers(Map.of(GROUP, handler));
        return handler;
    }
}
