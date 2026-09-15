# Prompt 10: security and performance preflight

Use after repository discovery when a task touches authentication,
authorization, secrets, sensitive data, remote APIs, queries, concurrency,
caching, paging, large data, or other critical paths.

```text
Perform a focused security and performance preflight for the proposed task.
Do not modify files or execute builds, tests, network calls, or live operations.

Task:
{{TASK}}

Inspect only the code, configuration, tests, and documentation needed to answer:

Security impact:
- trust boundaries changed or introduced;
- sensitive inputs, outputs, storage, transport, and logs;
- authentication and authorization behavior;
- validation, injection, traversal, deserialization, and command risks;
- secret, token, key, certificate, TLS, and proxy handling;
- dependencies and classloading relevant to the change;
- abuse, failure, rollback, and partial-failure scenarios;
- security tests or controls required.

Performance impact:
- affected critical path;
- remote calls or database operations before and after;
- paging, batching, filtering, caching, retry, throttling, and concurrency;
- allocations, memory bounds, streaming, queues, and data volume;
- expected workload and topology;
- baseline or measurement evidence available;
- representative regression test, benchmark, profiler, or query-plan strategy;
- behavior at scale and under partial failure.

For each finding, cite exact repository files, methods, configuration, tests, or
documents. Classify evidence as confirmed, inferred, assumed, or unknown.

Conclude with:
1. blocking issues;
2. required plan constraints;
3. required tests and measurements;
4. unresolved questions requiring user or environment input.

Do not claim security or performance improvements without evidence. Wait for
approval before planning or implementation.
```
