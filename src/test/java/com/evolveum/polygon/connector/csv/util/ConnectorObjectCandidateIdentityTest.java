package com.evolveum.polygon.connector.csv.util;

import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.SyncDeltaType;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class ConnectorObjectCandidateIdentityTest {

    private static final ObjectClass ACCOUNT = ObjectClass.ACCOUNT;
    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void hashSetMembershipSurvivesAssociationLifecycleMutation() {
        ConnectorObjectId dependencyId = new ConnectorObjectId("dependency", GROUP);
        ConnectorObjectCandidate root = candidate(
                new ConnectorObjectId("root", ACCOUNT), "root-uid", "root",
                new HashSet<>(Set.of(dependencyId)), new HashSet<>(), Set.of("group"));
        ConnectorObjectCandidate dependency = candidate(
                dependencyId, "dependency-uid", "dependency", new HashSet<>(), new HashSet<>(), Set.of());
        Set<ConnectorObjectCandidate> candidates = new HashSet<>();
        candidates.add(root);
        int initialHash = root.hashCode();

        Assert.assertTrue(candidates.contains(root));

        root.setDepth(1);
        Assert.assertEquals(root.hashCode(), initialHash);
        Assert.assertTrue(candidates.contains(root));

        root.addCandidateUponWhichThisDepends(dependency);
        Assert.assertEquals(root.hashCode(), initialHash);
        Assert.assertTrue(candidates.contains(root));

        root.evaluateDependencies();
        Assert.assertEquals(root.hashCode(), initialHash);
        Assert.assertTrue(candidates.contains(root));

        Assert.assertTrue(root.complete());
        Assert.assertEquals(root.hashCode(), initialHash);
        Assert.assertTrue(candidates.contains(root));
        Assert.assertTrue(candidates.remove(root));
        Assert.assertTrue(candidates.isEmpty());
        Assert.assertNotNull(root.getCandidateBuilder().build().getAttributeByName("group"));
    }

    @Test
    public void sameIdentityIsEqualDespiteDifferentMutableState() {
        ConnectorObjectId id = new ConnectorObjectId("same", ACCOUNT);
        ConnectorObjectCandidate first = candidate(id, "uid-1", "first", Set.of(), Set.of(), Set.of());
        ConnectorObjectCandidate second = candidate(
                new ConnectorObjectId("same", ACCOUNT), "uid-2", "second",
                Set.of(new ConnectorObjectId("target", GROUP)), Set.of(), Set.of("group"));
        ConnectorObjectCandidate third = candidate(
                new ConnectorObjectId("same", ACCOUNT), "uid-3", "third", Set.of(), Set.of(), Set.of());

        Assert.assertTrue(first.complete());
        first.setDepth(2);

        Assert.assertEquals(first, second);
        Assert.assertEquals(second, first);
        Assert.assertEquals(second, third);
        Assert.assertEquals(first, third);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertEquals(second.hashCode(), third.hashCode());
    }

    @Test
    public void distinctConnectorObjectIdentitiesRemainDistinct() {
        ConnectorObjectCandidate firstAccount = candidate(
                new ConnectorObjectId("first", ACCOUNT), "uid-1", "first", Set.of(), Set.of(), Set.of());
        ConnectorObjectCandidate secondAccount = candidate(
                new ConnectorObjectId("second", ACCOUNT), "uid-2", "second", Set.of(), Set.of(), Set.of());
        ConnectorObjectCandidate sameIdGroup = candidate(
                new ConnectorObjectId("first", GROUP), "uid-3", "first", Set.of(), Set.of(), Set.of());
        Set<ConnectorObjectCandidate> candidates = new HashSet<>();

        candidates.add(firstAccount);
        candidates.add(secondAccount);
        candidates.add(sameIdGroup);

        Assert.assertNotEquals(firstAccount, secondAccount);
        Assert.assertNotEquals(firstAccount, sameIdGroup);
        Assert.assertEquals(candidates.size(), 3);
    }

    @Test
    public void syncCandidateRetainsConcreteClassBoundary() {
        ConnectorObjectId id = new ConnectorObjectId("same", ACCOUNT);
        ConnectorObjectCandidate queryCandidate = candidate(
                id, "uid", "same", Set.of(), Set.of(), Set.of());
        SyncDeltaObjectCandidate syncCandidate = new SyncDeltaObjectCandidate(
                id, builder(ACCOUNT, "uid", "same"), Set.of(), Set.of(), new SyncToken("token"),
                SyncDeltaType.CREATE, Set.of(), "group");

        Assert.assertNotEquals(queryCandidate, syncCandidate);
        Assert.assertNotEquals(syncCandidate, queryCandidate);
    }

    @Test
    public void dependencyDeduplicationRemainsBasedOnLogicalConnectorObjectId() {
        ConnectorObjectId dependencyId = new ConnectorObjectId("dependency", GROUP);
        ConnectorObjectCandidate root = candidate(
                new ConnectorObjectId("root", ACCOUNT), "root-uid", "root",
                new HashSet<>(Set.of(dependencyId)), new HashSet<>(), Set.of("group"));
        ConnectorObjectCandidate queryDependency = candidate(
                dependencyId, "dependency-query", "dependency", Set.of(), Set.of(), Set.of());
        SyncDeltaObjectCandidate syncDependency = new SyncDeltaObjectCandidate(
                new ConnectorObjectId("dependency", GROUP),
                builder(GROUP, "dependency-sync", "dependency"), Set.of(), Set.of(), new SyncToken("token"),
                SyncDeltaType.CREATE, Set.of(), "group");

        root.addCandidateUponWhichThisDepends(queryDependency);
        root.addCandidateUponWhichThisDepends(syncDependency);

        Assert.assertTrue(root.complete());
        Assert.assertNotNull(root.getCandidateBuilder().build().getAttributeByName("group"));
        Assert.assertEquals(root.getCandidateBuilder().build().getAttributeByName("group").getValue().size(), 1);
    }

    private ConnectorObjectCandidate candidate(ConnectorObjectId id, String uid, String name,
                                               Set<ConnectorObjectId> objectIds,
                                               Set<ConnectorObjectId> subjectIds,
                                               Set<String> referenceNames) {
        return new ConnectorObjectCandidate(
                id, builder(id.getObjectClass(), uid, name), objectIds, subjectIds, referenceNames, "group");
    }

    private ConnectorObjectBuilder builder(ObjectClass objectClass, String uid, String name) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setObjectClass(objectClass);
        builder.setUid(uid);
        builder.setName(new Name(name));
        builder.addAttribute(AttributeBuilder.build("marker", uid));
        return builder;
    }
}
