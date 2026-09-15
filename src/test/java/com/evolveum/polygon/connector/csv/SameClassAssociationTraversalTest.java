package com.evolveum.polygon.connector.csv;

import com.evolveum.polygon.connector.csv.util.AssociationCharacter;
import com.evolveum.polygon.connector.csv.util.AssociationHolder;
import com.evolveum.polygon.connector.csv.util.ConnectorObjectCandidate;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectReference;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SameClassAssociationTraversalTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void resolvesTwoLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-2-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n", 2, "forward");
    }

    @Test
    public void resolvesTwoLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-2-reverse.csv",
                "id;memberOf\r\n2;1\r\n1;\r\n", 2, "reverse");
    }

    @Test
    public void resolvesThreeLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-3-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n3;2\r\n", 3, "forward");
    }

    @Test
    public void resolvesThreeLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-3-reverse.csv",
                "id;memberOf\r\n3;2\r\n2;1\r\n1;\r\n", 3, "reverse");
    }

    @Test
    public void resolvesFourLevelSameClassChainInForwardOrder() throws Exception {
        assertChainResolved("same-class-chain-4-forward.csv",
                "id;memberOf\r\n1;\r\n2;1\r\n3;2\r\n4;3\r\n", 4, "forward");
    }

    @Test
    public void resolvesFourLevelSameClassChainInReverseOrder() throws Exception {
        assertChainResolved("same-class-chain-4-reverse.csv",
                "id;memberOf\r\n4;3\r\n3;2\r\n2;1\r\n1;\r\n", 4, "reverse");
    }

    @Test
    public void measuresSparseSameClassReferenceAcrossUnrelatedRows() throws Exception {
        int records = 100;
        StringBuilder content = new StringBuilder("id;memberOf\r\n");
        content.append("1;\r\n");
        content.append("2;1\r\n");
        for (int id = 3; id <= records; id++) {
            content.append(id).append(";\r\n");
        }

        Path path = Path.of("target", "same-class-sparse-100.csv");
        Files.createDirectories(path.getParent());
        Files.writeString(path, content.toString());

        ObjectClassHandler handler = handler(path);
        List<ConnectorObject> results = new ArrayList<>();
        ConnectorObjectCandidate.resetConstructionCount();
        handler.executeQuery(GROUP, null, object -> {
            results.add(object);
            return true;
        }, null);

        long candidateConstructions = ConnectorObjectCandidate.getConstructionCount();
        long extraCandidateConstructions = candidateConstructions - records;
        double fullPassEquivalents = (double) candidateConstructions / records;

        System.out.printf(
                "SAME_CLASS_SPARSE_WORK records=%d associationEdges=1 candidateConstructions=%d extraCandidateConstructions=%d fullPassEquivalents=%.2f%n",
                records, candidateConstructions, extraCandidateConstructions, fullPassEquivalents);

        Assert.assertEquals(results.size(), records);
        assertNoParent(results, "1");
        assertParent(results, "2", "1");
        assertNoParent(results, "100");
        Assert.assertEquals(candidateConstructions, (long) records,
                "Same-class query must construct each CSV candidate exactly once");
    }

    private void assertChainResolved(String fileName, String content, int levels, String order) throws Exception {
        Path path = Path.of("target", fileName);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);

        ObjectClassHandler handler = handler(path);
        List<ConnectorObject> results = new ArrayList<>();
        ConnectorObjectCandidate.resetConstructionCount();
        handler.executeQuery(GROUP, null, object -> {
            results.add(object);
            return true;
        }, null);
        long candidateConstructions = ConnectorObjectCandidate.getConstructionCount();
        long extraCandidateConstructions = candidateConstructions - levels;
        double fullPassEquivalents = levels == 0 ? 0.0 : (double) candidateConstructions / levels;

        System.out.printf(
                "SAME_CLASS_WORK levels=%d order=%s records=%d candidateConstructions=%d extraCandidateConstructions=%d fullPassEquivalents=%.2f%n",
                levels, order, levels, candidateConstructions, extraCandidateConstructions, fullPassEquivalents);

        Assert.assertEquals(candidateConstructions, (long) levels,
                "Same-class query must construct each CSV candidate exactly once");
        Assert.assertEquals(results.size(), levels);
        assertNoParent(results, "1");
        for (int child = 2; child <= levels; child++) {
            assertParent(results, Integer.toString(child), Integer.toString(child - 1));
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
        Assert.assertNotNull(references);
        Assert.assertEquals(references.getValue().size(), 1);
        ConnectorObjectReference reference = (ConnectorObjectReference) references.getValue().get(0);
        Assert.assertEquals(referenceTargetId(reference), expectedParentUid);
    }

    private String referenceTargetId(ConnectorObjectReference reference) {
        Attribute uid = reference.getValue().getAttributeByName(Uid.NAME);
        if (uid != null && uid.getValue() != null && !uid.getValue().isEmpty()) {
            return String.valueOf(uid.getValue().get(0));
        }

        Attribute name = reference.getValue().getAttributeByName(Name.NAME);
        Assert.assertNotNull(name, "Reference contains neither UID nor Name identification");
        Assert.assertNotNull(name.getValue());
        Assert.assertFalse(name.getValue().isEmpty());

        Object value = name.getValue().get(0);
        if (value instanceof Collection<?> collection) {
            Assert.assertEquals(collection.size(), 1);
            return String.valueOf(collection.iterator().next());
        }
        return String.valueOf(value);
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
