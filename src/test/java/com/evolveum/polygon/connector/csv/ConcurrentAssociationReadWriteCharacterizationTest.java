package com.evolveum.polygon.connector.csv;

import com.evolveum.polygon.connector.csv.util.AssociationCharacter;
import com.evolveum.polygon.connector.csv.util.AssociationHolder;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectReference;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Characterizes the Windows file-handle conflict between a managed-association search
 * that is still publishing results and an overlapping attribute-value update.
 *
 * This intentionally records the current behavior before a production fix is applied.
 */
public class ConcurrentAssociationReadWriteCharacterizationTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void managedAssociationSearchCanKeepSourceFileOpenWhileRemoveStarts() throws Exception {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            throw new SkipException("Windows-specific file replacement characterization");
        }

        Path path = Path.of("target", "concurrent-association-read-write.csv");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "id;memberOf\r\n1;\r\n2;1\r\n3;\r\n");

        ObjectClassHandler handler = handler(path);
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch releaseCallback = new CountDownLatch(1);
        AtomicReference<Throwable> searchFailure = new AtomicReference<>();

        Thread searchThread = new Thread(() -> {
            try {
                handler.executeQuery(GROUP, null, object -> {
                    callbackEntered.countDown();
                    try {
                        if (!releaseCallback.await(10, TimeUnit.SECONDS)) {
                            throw new AssertionError("Timed out waiting to release search callback");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    }
                    return true;
                }, null);
            } catch (Throwable t) {
                searchFailure.set(t);
            }
        }, "csv-characterization-search");

        searchThread.start();
        Assert.assertTrue(callbackEntered.await(10, TimeUnit.SECONDS),
                "Search did not reach the result callback");

        ConnectorObjectBuilder referencedBuilder = new ConnectorObjectBuilder();
        referencedBuilder.setObjectClass(GROUP);
        referencedBuilder.setUid("1");
        referencedBuilder.setName(new Name("1"));
        ConnectorObjectReference reference = new ConnectorObjectReference(referencedBuilder.build());
        Set<Attribute> attributes = Set.of(AttributeBuilder.build("group", reference));

        try {
            Assert.expectThrows(ConnectorIOException.class,
                    () -> handler.removeAttributeValues(GROUP, new Uid("2"), attributes, null));
        } finally {
            releaseCallback.countDown();
            searchThread.join(10_000);
        }

        Assert.assertFalse(searchThread.isAlive(), "Search thread did not terminate");
        Assert.assertNull(searchFailure.get(), "Search failed unexpectedly: " + searchFailure.get());
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
