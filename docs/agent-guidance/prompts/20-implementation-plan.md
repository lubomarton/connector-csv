# Prompt 20: implementation plan

```text
Prepare an implementation plan for the task below. Do not modify files yet.

Task:
{{TASK}}

Accepted decisions and constraints:
{{DECISIONS}}

Use the completed repository discovery, security/performance preflight, and
documentation-grounding results. If any prerequisite is missing, say so rather
than inventing it.

The plan must include:

1. objective and explicit non-goals;
2. current behavior and evidence;
3. target behavior and acceptance criteria;
4. exact files, classes, methods, configuration, schemas, or objects to change;
5. implementation order and why it is safe;
6. public API, configuration, schema, runtime, protocol, and data compatibility;
7. migration, rollout, rollback, and partial-failure behavior;
8. tests to add or update, separated into unit, integration, acceptance,
   performance, security, and deployment categories as applicable;
9. exact validation commands and whether each is local, networked,
   tenant-backed, state-changing, or production-facing;
10. risks, assumptions, unresolved questions, and stop conditions.

Security impact:
- trust boundaries;
- sensitive inputs and outputs;
- authentication and authorization;
- logging and error messages;
- secret handling;
- abuse and failure scenarios.

Performance impact:
- affected critical path;
- remote/database operations before and after;
- paging, batching, caching, retries, concurrency, allocations, and memory;
- expected workload;
- regression measurement strategy;
- behavior at scale and under partial failure.

Separate required work from optional refactoring. Do not expand the task to fix
unrelated findings. End with a proposed execution-approval scope and wait for
approval.
```
