# Prompt 00: repository discovery

```text
Inspect this repository without modifying any files.

Start by determining the repository type, primary technology stack, runtime
model, expected deployment environment, and applicable AGENTS.md instructions.

## Repository and Git state

Report:

1. current branch, tracking branch, default/base branch, and divergence;
2. staged, unstaged, ignored, and untracked changes;
3. whether the working tree is safe for new work;
4. existing AGENTS.md files, repository instructions, contribution guides,
   architecture documents, decision records, and development documentation;
5. repository structure and important modules;
6. build, test, lint, formatting, packaging, release, migration, and deployment
   mechanisms;
7. required runtimes, tools, frameworks, protocols, and dependency versions;
8. existing CI/CD workflows and required validation checks;
9. compatibility constraints and supported runtime or platform versions.

## Security inspection

Determine:

1. how credentials, secrets, certificates, keys, tokens, environment-specific
   values, and customer configuration are loaded;
2. whether tracked, ignored, generated, or locally modified files may contain
   sensitive values, without displaying those values;
3. whether complete requests, responses, attributes, headers, personal data,
   credentials, or authentication results may be logged;
4. the authentication and authorization model, where applicable;
5. externally controlled input surfaces and their validation;
6. use of command execution, dynamic queries, deserialization, paths,
   templates, expressions, scripts, or reflection;
7. TLS, certificate validation, cryptography, secret storage, and proxy behavior;
8. dependency and supply-chain risks visible from repository configuration;
9. security-sensitive defaults, bypasses, debug modes, or test-only behavior
   that could reach production;
10. available security tests, static analysis, dependency scanning, secret
    scanning, or threat-model documentation.

Classify security findings as:

- confirmed vulnerability;
- likely security defect;
- risk requiring verification;
- hardening opportunity;
- no issue found with the inspected evidence.

Never display sensitive values.

## Performance and scalability inspection

Determine:

1. main execution paths and likely performance-critical operations;
2. database queries, remote API calls, paging, batching, filtering, caching,
   retries, synchronization, concurrency, and background work;
3. potentially repeated or nested remote or database operations;
4. unbounded collections, full scans, eager loading, repeated parsing,
   unnecessary allocation, or large in-memory transformations;
5. retry, timeout, throttling, backoff, rate-limit, and cancellation behavior;
6. cache ownership, invalidation, concurrency, bounds, and sensitive-data impact;
7. documented workload, volume, latency, throughput, and topology;
8. performance tests, benchmarks, profiler instructions, load tests, execution
   plans, or production metrics;
9. areas where correctness tests exist but performance is untested;
10. scalability risks requiring measurement rather than speculation.

Classify performance findings as:

- confirmed regression;
- measurable bottleneck supported by evidence;
- scalability risk;
- performance-sensitive code requiring regression coverage;
- optimization opportunity requiring measurement;
- insufficient evidence.

## Working profile

Select the most suitable profile:

- midPoint configuration or deployment;
- ConnId connector development;
- proof of concept or experimental development;
- infrastructure or platform configuration;
- analytics or data processing;
- documentation or architecture;
- other.

## Final report

Provide:

1. confirmed architecture and repository state;
2. security findings and evidence;
3. performance and scalability findings and evidence;
4. compatibility constraints;
5. test and validation gaps;
6. risks and prerequisites;
7. recommended next steps before implementation;
8. proposed repository-specific AGENTS.md topics;
9. authoritative external sources that must be identified or verified.

Constraints:

- Do not modify, create, restore, move, or delete files.
- Do not output credentials, tokens, private keys, passwords, certificate
  contents, tenant URLs, or sensitive customer data.
- Do not install or update dependencies.
- Do not run builds, tests, scans, migrations, provisioning, deployments,
  authentication, tenant API calls, or external network operations.
- Do not commit, push, switch or create branches, create a pull request, or
  modify remote state.
- Separate confirmed findings, assumptions, and recommendations.

Complete the report and wait for approval.
```
