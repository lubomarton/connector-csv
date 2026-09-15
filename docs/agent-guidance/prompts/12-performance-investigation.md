# Prompt 12: performance investigation and optimization evidence

Use when a task concerns latency, throughput, CPU, memory, database cost,
network cost, scale degradation, or a suspected performance regression. This
workflow is for measurement and causal attribution before optimization.

```text
Investigate the performance problem before changing implementation.

Performance objective or symptom:
{{PERFORMANCE_PROBLEM}}

Known workload and environment:
{{WORKLOAD_ENVIRONMENT}}

Existing measurements:
{{MEASUREMENTS}}

Follow this sequence:

1. Define the metric and acceptance boundary.
   - identify latency, throughput, CPU, allocation, heap, I/O, network,
     database work, or another explicit metric;
   - state workload size, concurrency, warm-up, repetitions, and environment;
   - distinguish absolute target, regression threshold, and exploratory goal.

2. Establish a reproducible baseline.
   - use the smallest workload that still exposes the effect;
   - preserve the production-relevant shape of data and call topology;
   - capture central tendency and variance when repeated runs are meaningful;
   - record runtime, JVM, database, dependency, connector/framework, and build
     versions that can affect the result.

3. Measure the full path before choosing the optimization layer.
   Where applicable correlate:
   - application/JVM profile or trace;
   - shared framework/library calls;
   - connector/adapter calls;
   - remote requests and retries;
   - database statements, plans, execution counts, and time;
   - allocation and memory pressure;
   - synchronization, contention, blocking, and thread state.

4. Count work, not only elapsed time.
   Record causal work units such as:
   - repository/database searches;
   - network requests;
   - callbacks/handler invocations;
   - objects materialized;
   - rows scanned/returned;
   - retries;
   - serialization/deserialization operations;
   - allocations when available.

5. Isolate scaling behavior.
   - test at more than one representative scale when the claim concerns scale;
   - identify linear, super-linear, threshold, cliff, saturation, or noisy
     behavior only when the measurements support that classification;
   - investigate discontinuities across all relevant layers before assigning
     them to the database, JVM, connector, framework, or application.

6. Form and falsify competing causes.
   - change one causal variable at a time where practical;
   - use implementation/configuration variants only when they discriminate
     between hypotheses;
   - do not treat a faster variant as proof of why it is faster;
   - avoid unnecessary cross-product benchmarking after a shared lower-layer
     mechanism is already demonstrated, unless external validity requires it.

7. Propose the smallest justified optimization.
   The proposal must state:
   - measured bottleneck;
   - responsible layer and code path;
   - intended reduction in work;
   - correctness/compatibility constraints;
   - benchmark or regression test that would validate the change;
   - rollback path and possible trade-offs.

For each conclusion classify evidence as measured, repository/source evidence,
external documentation, inference, assumption, or unknown. Preserve failed or
null optimization attempts because they constrain later hypotheses.

Conclude with:
1. baseline and workload;
2. dominant measured costs;
3. scaling evidence;
4. causal attribution and confidence;
5. hypotheses rejected;
6. proposed bounded optimization;
7. validation design and acceptance criteria;
8. remaining uncertainty.

Do not claim a performance improvement from code inspection alone. Do not claim
root cause from timing correlation alone. Correctness and compatibility remain
hard constraints even when a variant is faster.
```
