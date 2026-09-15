# Prompt 11: evidence-driven debugging and root-cause isolation

Use after repository discovery when the task is to explain a defect, regression,
unexpected behavior, or performance anomaly. This prompt is investigative: do
not change production code until the causal boundary is supported by evidence.

```text
Investigate the reported problem using an evidence-driven root-cause workflow.
Do not modify source files until the investigation identifies a bounded cause or
an explicitly testable hypothesis and the user approves implementation.

Problem statement:
{{PROBLEM}}

Observed evidence already available:
{{EVIDENCE}}

Work from observation to cause without silently skipping layers:

1. Reconstruct the symptom.
   - state the exact observed failure or degradation;
   - identify workload, inputs, scale, runtime, version, and environment;
   - separate reproduced facts from reports, assumptions, and interpretation.

2. Establish a controlled baseline.
   - identify the smallest representative reproducer;
   - hold unrelated variables constant;
   - record the expected behavior and the measured or observable baseline;
   - preserve negative, null, and contradictory results.

3. Trace the execution path across architectural boundaries.
   - identify caller, shared framework/library, connector/adapter, transport,
     persistence/database, and external-service boundaries as applicable;
   - locate transformations, callbacks, retries, handlers, caches, loops,
     query generation, serialization, and allocation-heavy paths;
   - do not assign responsibility to the highest visible layer when the same
     behavior can originate in a shared dependency.

4. Form competing hypotheses.
   For each hypothesis record:
   - mechanism;
   - repository/source location or external component involved;
   - evidence that would support it;
   - evidence that would falsify it;
   - cheapest discriminating experiment.

5. Discriminate rather than accumulate anecdotes.
   - prefer one experiment that distinguishes hypotheses over many similar
     reproductions;
   - compare alternative paths only when they test a causal boundary;
   - if multiple products share the same framework path, use that fact to test
     shared-framework attribution rather than assuming independent defects;
   - stop expanding the matrix when new cases cannot materially change the
     causal conclusion.

6. Attribute the cause at the narrowest defensible layer.
   Classify the final state as one of:
   - confirmed root cause;
   - supported causal mechanism with unresolved lower-level detail;
   - bounded hypothesis requiring another experiment;
   - correlation only;
   - not reproduced / insufficient evidence.

For every material claim cite exact files, methods, tests, traces, profiles,
queries, logs, or measurements. Mark evidence as repository evidence, runtime
measurement, external documentation, inference, assumption, or unknown.

Conclude with:
1. symptom and baseline;
2. execution-path boundary map;
3. hypotheses considered and falsified;
4. strongest supported cause and confidence;
5. minimum corrective surface;
6. regression test or measurement needed before implementation;
7. unresolved evidence and risks.

Do not rename a correlation as a root cause. Do not recommend optimization
before identifying which layer actually consumes the measured cost.
```
