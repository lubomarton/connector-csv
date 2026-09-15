# Profile: Java ConnId connector development

## Use for

Microsoft Graph, SAP SuccessFactors, LDAP/AD, SCIM, REST, CSV, database, and
other Java ConnId connectors used by midPoint or compatible runtimes.

## Contracts

- Preserve public connector configuration names and semantics.
- Preserve ConnId SPI, connector-framework, Java, and supported midPoint runtime
  compatibility.
- Review assembled JAR dependencies and runtime classloading conflicts.
- Treat schema, filter, paging, CRUD, synchronization, and error mapping as
  connector contracts.
- Treat connector, ConnId framework, consuming application, persistence, and
  remote-system behavior as separate causal layers during defect and
  performance analysis.

## Safety and security

- Keep unit tests separate from tenant-backed integration tests.
- Never run integration tests against an unknown or production tenant.
- Do not hardcode credentials or environment-specific values.
- Review `GuardedString`, OAuth/SAML flows, token caching, TLS, trust stores,
  proxy handling, tenant isolation, query construction, request/response logs,
  and secret files packaged into the JAR.
- Never log tokens, assertions, secrets, passwords, keys, full credential
  responses, or sensitive object payloads.

## Performance and debugging

- Establish a reproducible baseline before changing performance-sensitive code.
- Trace the complete call path before attributing cost to the connector. Include
  the consuming application, ConnId framework handlers/callbacks, connector
  implementation, transport or file/database layer, and downstream system as
  applicable.
- Review server-side paging and filtering, batch behavior, collection selection,
  remote calls per object, N+1 navigation, throttling, `Retry-After`, backoff,
  token acquisition, schema generation, LiveSync polling, `$select`/`$expand`,
  handler/callback counts, object materialization, and memory use for large
  result sets.
- Inspect filter translation, result handling, paging cookies/tokens,
  synchronization deltas, retry paths, and framework-to-connector conversions
  when work amplification is suspected.
- Count causal work units in addition to elapsed time: connector invocations,
  framework callbacks, remote requests, database/repository queries, rows or
  objects materialized, retries, and allocations when measurable.
- When the same symptom appears across multiple connectors that share ConnId,
  explicitly test whether the common framework path explains it before
  duplicating connector-specific optimizations.
- Do not infer a framework defect merely from cross-connector similarity; trace
  the shared source path or capture discriminating runtime evidence.
- Preserve lazy, paged, or streaming behavior unless the approved task changes
  it.
- Use `11-evidence-driven-debugging.md` for causal isolation and
  `12-performance-investigation.md` for measurement-first optimization work.

## Discovery questions

- Where are connector entry points, configuration, schema, object processors,
  transport, authentication, exception translation, and ConnId callback or
  result-handler boundaries?
- Which operations and object classes are supported?
- What Java, Maven, ConnId, test framework, and midPoint versions are supported?
- Which ConnId framework version/source revision actually runs in the target
  environment, and is it bundled, inherited, or supplied by the runtime?
- Which tests are hermetic and which contact a tenant or external data source?
- How are retries, timeouts, throttling, pagination, cancellation, filter
  translation, and result delivery handled?
- Which parts of an observed failure are connector-owned versus framework,
  application, database, operating-system, or remote-service owned?
- Which official vendor, protocol, ConnId, and consuming-runtime sources govern
  behavior?

## Validation defaults

Run focused unit tests first, then the complete unit suite. For performance
changes, add or preserve a representative benchmark/regression measurement that
verifies both work reduction and correctness. Packaging, integration tests,
tenant access, and connector deployment require explicit approval and known
non-production targets.
