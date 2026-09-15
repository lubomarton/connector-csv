package com.evolveum.polygon.connector.csv.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class CandidateSet<E extends ConnectorObjectCandidate> extends HashSet<ConnectorObjectCandidate> {

    private final Set<ConnectorObjectId> candidateId = new HashSet<>();

    @Override
    public boolean add(ConnectorObjectCandidate candidate) {
        boolean added = super.add(candidate);
        if (added) {
            candidateId.add(candidate.getId());
        }
        return added;
    }

    public boolean addWithId(E candidate) {
        return add(candidate);
    }

    @Override
    public boolean remove(Object candidate) {
        boolean removed = super.remove(candidate);
        if (removed) {
            rebuildCandidateIds();
        }
        return removed;
    }

    public boolean removeWithId(E candidate) {
        return remove(candidate);
    }

    @Override
    public boolean removeAll(Collection<?> candidates) {
        boolean changed = super.removeAll(candidates);
        if (changed) {
            rebuildCandidateIds();
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> candidates) {
        boolean changed = super.retainAll(candidates);
        if (changed) {
            rebuildCandidateIds();
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        candidateId.clear();
    }

    public Set<ConnectorObjectId> getCandidateId() {
        return candidateId;
    }

    private void rebuildCandidateIds() {
        candidateId.clear();
        for (ConnectorObjectCandidate candidate : this) {
            candidateId.add(candidate.getId());
        }
    }
}
