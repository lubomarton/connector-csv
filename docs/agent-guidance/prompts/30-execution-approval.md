# Prompt 30: scoped execution approval

Replace every placeholder before using this prompt.

```text
Approved. Implement only the agreed plan for:

{{TASK}}

Authorized files or components:
{{FILES_OR_COMPONENTS}}

Authorized validation:
{{VALIDATION_COMMANDS}}

You may:
- modify only the approved files and directly required supporting files;
- add or update the approved tests;
- run the approved local validation commands;
- inspect Git status and the resulting diff.

You may not:
- materially expand scope or perform optional refactoring;
- display or modify real credentials or sensitive customer data;
- contact a tenant, production system, or external service unless explicitly
  listed above;
- run integration, deployment, migration, provisioning, or destructive tests
  unless explicitly listed above;
- commit, push, force-push, switch or create branches, create a pull request,
  deploy, or modify remote state;
- discard, overwrite, stash, shelve, or reformat unrelated user changes.

Do not introduce security or performance behavior outside the approved plan.

Before completing:
- review the final diff for sensitive-data exposure;
- review new logs and error messages;
- report changes in remote calls, queries, allocations, paging, batching,
  caching, retries, concurrency, and memory;
- run only approved security and performance checks;
- do not claim improvements without evidence.

Stop and ask before materially deviating from the plan or when an unexpected
repository state, test failure, source conflict, or compatibility issue changes
the approved approach.

At completion, report modified files, behavior, security impact, performance
impact, validation results, remaining risks, Git status, and diff summary. Wait
for separate approval before commit or push.
```
