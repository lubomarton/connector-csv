package com.evolveum.polygon.connector.csv.util;

import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.SyncDeltaType;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class CandidateSetTest {

    private static final ObjectClass GROUP = new ObjectClass("group");

    @Test
    public void removeAllKeepsCandidateIdIndexConsistent() {
        ConnectorObjectCandidate first = candidate("1");
        ConnectorObjectCandidate second = candidate("2");
        CandidateSet<ConnectorObjectCandidate> candidates = new CandidateSet<>();
        candidates.addWithId(first);
        candidates.addWithId(second);

        Assert.assertEquals(candidates.getCandidateId(), Set.of(first.getId(), second.getId()));

        Assert.assertTrue(candidates.removeAll(Set.of(first)));

        Assert.assertEquals(candidates.size(), 1);
        Assert.assertFalse(candidates.getCandidateId().contains(first.getId()));
        Assert.assertTrue(candidates.getCandidateId().contains(second.getId()));
    }

    @Test
    public void removingOneRuntimeClassKeepsSharedLogicalIdIndexed() {
        ConnectorObjectId id = new ConnectorObjectId("shared", GROUP);
        ConnectorObjectCandidate ordinary = new ConnectorObjectCandidate(
                id, builder("ordinary"), Set.of(), Set.of(), Set.of(), "group");
        SyncDeltaObjectCandidate sync = new SyncDeltaObjectCandidate(
                new ConnectorObjectId("shared", GROUP), builder("sync"), Set.of(), Set.of(),
                new SyncToken("token"), SyncDeltaType.CREATE, Set.of(), "group");
        CandidateSet<ConnectorObjectCandidate> candidates = new CandidateSet<>();
        candidates.addWithId(ordinary);
        candidates.addWithId(sync);

        Assert.assertEquals(candidates.size(), 2);
        Assert.assertEquals(candidates.getCandidateId(), Set.of(id));

        Assert.assertTrue(candidates.remove(ordinary));

        Assert.assertEquals(candidates.size(), 1);
        Assert.assertTrue(candidates.getCandidateId().contains(id));
    }

    private ConnectorObjectCandidate candidate(String id) {
        return new ConnectorObjectCandidate(
                new ConnectorObjectId(id, GROUP), builder(id), Set.of(), Set.of(), Set.of(), "group");
    }

    private ConnectorObjectBuilder builder(String id) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setObjectClass(GROUP);
        builder.setUid(id);
        builder.setName(new Name(id));
        return builder;
    }
}
