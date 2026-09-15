# Repository instructions

## Repository contract

- Connector and target platform: CSV file connector for ConnId/midPoint
- Profile: Java ConnId connector development
- Java baseline: 17
- Maven/connector baseline: `connector-parent` 1.5.3.0-M3; `connector-csv` 2.10-SNAPSHOT
- ConnId runtime baseline: inherited from connector parent/runtime; resolve exact dependency before compatibility or framework claims
- Hermetic unit-test command: `mvn test`
- Tenant-backed integration tests: not applicable by default; any external/runtime-backed validation requires explicit approval

Follow:

- `docs/agent-guidance/connid-connector.md`
- `docs/references/authoritative-sources.md`
- the applicable prompt under `docs/agent-guidance/prompts/`

## Required behavior

- Start unfamiliar work with read-only repository and Git inspection.
- Preserve public connector configuration, ConnId SPI, schema, and supported runtime compatibility unless the approved task changes them.
- Keep hermetic tests separate from runtime-backed or externally coupled tests.
- Never hardcode or log credentials, tokens, assertions, passwords, keys, certificate contents, sensitive payloads, or environment-specific values.
- Review schema, search, paging, filters, CRUD, sync, authentication, retries, error mapping, packaging, classloading, file locking, and concurrency as applicable.
- Analyze security, performance, compatibility, migration, and partial failures for every behavioral change.
- For defects and performance work, separate connector-owned behavior from ConnId framework, consuming application, filesystem, JVM, and OS behavior before attributing root cause.
- Ground ConnId/runtime claims in exact dependency/source revisions and tests; record conflicts explicitly.
- Add focused tests for every behavioral change.
- Do not commit, push, switch branches, or create a PR without explicit approval.

## Performance investigation

- Establish a reproducible baseline before optimization.
- Trace the complete caller -> ConnId -> connector -> file layer -> result-handler path.
- Count work units such as connector invocations, handler callbacks, records scanned, objects materialized, retries, and repeated parsing in addition to elapsed time.
- Do not infer a connector defect from a symptom observed through a connector, and do not infer a ConnId defect solely from cross-connector similarity.
- Use `docs/agent-guidance/prompts/11-evidence-driven-debugging.md` for causal isolation and `docs/agent-guidance/prompts/12-performance-investigation.md` for measurement-first performance work.

## Validation

Run focused unit tests before the complete unit suite. Packaging, runtime integration, deployment, and production-facing validation require separate approval.
