package com.evolveum.polygon.connector.csv;

import com.evolveum.polygon.connector.csv.util.AssociationCharacter;
import com.evolveum.polygon.connector.csv.util.AssociationHolder;
import com.evolveum.polygon.connector.csv.util.CandidateSet;
import com.evolveum.polygon.connector.csv.util.ConnectorObjectCandidate;
import com.evolveum.polygon.connector.csv.util.ConnectorObjectId;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectReference;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectorObjectIdTraversalIdentityTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void logicallyEqualIdDoesNotTriggerReiteration() throws Exception {
        ObjectClassHandler handler = handler(Path.of("target", "logical-id-unused.csv"));
        ConnectorObjectId candidateId = new ConnectorObjectId("1", GROUP);
        ConnectorObjectId equalButDistinct = new ConnectorObjectId("1", GROUP);

        Assert.assertNotSame(candidateId, equalButDistinct);
        Assert.assertEquals(candidateId, equalButDistinct);
        Assert.assertFalse(appendDecision(handler, candidate(candidateId, Set.of(equalButDistinct))));
        Assert.assertFalse(appendDecision(handler, candidate(candidateId, Set.of(candidateId))));

        ConnectorObjectId otherId = new ConnectorObjectId("2", GROUP);
        Assert.assertTrue(appendDecision(handler, candidate(otherId, Set.of(candidateId))));
    }

    @Test
    public void reIterationSkipsLogicalSelfCandidate() throws Exception {
        Path path = writeFixture("logical-self-skip.csv", "id;memberOf\r\n1;\r\n");
        ObjectClassHandler handler = handler(path);
        CountingCandidate candidate = new CountingCandidate(
                new ConnectorObjectId("1", GROUP), builder("1"), Set.of(), Set.of(), Set.of("group"), "group");
        CandidateSet<ConnectorObjectCandidate> candidateSet = new CandidateSet<>();
        candidateSet.addWithId(candidate);
        HashMap<ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>> candidates = new HashMap<>();
        candidates.put(new ConnectorObjectId("1", GROUP), candidateSet);

        Method method = ObjectClassHandler.class.getDeclaredMethod("reIterateCandidates", HashMap.class);
        method.setAccessible(true);
        method.invoke(handler, candidates);

        Assert.assertEquals(candidate.dependencyAttempts, 0);
    }

    @Test
    public void sameTextualIdInDifferentObjectClassesRemainsDistinct() {
        Assert.assertNotEquals(
                new ConnectorObjectId("1", GROUP),
                new ConnectorObjectId("1", ObjectClass.ACCOUNT));
    }

    @Test
    public void sameClassAssociationGraphRemainsResolved() throws Exception {
        Path path = writeFixture("logical-id-association.csv", "id;memberOf\r\n1;\r\n2;1\r\n");
        ObjectClassHandler handler = handler(path);
        List<ConnectorObject> results = new ArrayList<>();

        handler.executeQuery(GROUP, null, object -> {
            results.add(object);
            return true;
        }, null);

        Assert.assertEquals(results.size(), 2);
        ConnectorObject child = results.stream()
                .filter(object -> "2".equals(object.getUid().getUidValue()))
                .findFirst()
                .orElseThrow();
        Attribute references = child.getAttributeByName("group");
        Assert.assertNotNull(references);
        Assert.assertEquals(references.getValue().size(), 1);
        ConnectorObjectReference reference = (ConnectorObjectReference) references.getValue().get(0);
        Assert.assertEquals(reference.getValue().getAttributeByName(Uid.NAME), new Uid("1"));
    }

    private boolean appendDecision(ObjectClassHandler handler, ConnectorObjectCandidate candidate) throws Exception {
        Method method = ObjectClassHandler.class.getDeclaredMethod(
                "appendToCandidateMap", ConnectorObjectCandidate.class, HashMap.class, boolean.class);
        method.setAccessible(true);
        return (boolean) method.invoke(handler, candidate,
                new HashMap<ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>>(), true);
    }

    private Path writeFixture(String name, String content) throws Exception {
        Path path = Path.of("target", name);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
        return path;
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

    private ConnectorObjectCandidate candidate(ConnectorObjectId id, Set<ConnectorObjectId> dependencies) {
        return new ConnectorObjectCandidate(
                id, builder(id.getId()), dependencies, Set.of(), Set.of("group"), "group");
    }

    private ConnectorObjectBuilder builder(String id) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setObjectClass(GROUP);
        builder.setUid(id);
        builder.setName(new Name(id));
        return builder;
    }

    private static final class CountingCandidate extends ConnectorObjectCandidate {

        private int dependencyAttempts;

        private CountingCandidate(ConnectorObjectId id, ConnectorObjectBuilder candidateBuilder,
                                  Set<ConnectorObjectId> associatedObjectIds,
                                  Set<ConnectorObjectId> subjectIdsToBeProcessed,
                                  Set<String> referenceNames, String nameAssocAttrDirect) {
            super(id, candidateBuilder, associatedObjectIds, subjectIdsToBeProcessed,
                    referenceNames, nameAssocAttrDirect);
        }

        @Override
        public void addCandidateUponWhichThisDepends(ConnectorObjectCandidate candidate) {
            dependencyAttempts++;
            super.addCandidateUponWhichThisDepends(candidate);
        }
    }
}
