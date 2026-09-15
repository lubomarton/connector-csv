# Authoritative sources

Last reviewed: 2026-09-15  
Repository profile: connid-connector  
Supported product/runtime versions: Java 17; connector-csv 2.10-SNAPSHOT; connector-parent 1.5.3.0-M3; exact ConnId runtime dependency pending resolution

## Policy

Repository code describes the current implementation; it is not proof that the
implementation satisfies an external platform or protocol contract.

Use official vendor and standards documentation together with repository tests,
approved decisions, authorized tenant metadata, and verified runtime evidence.
Do not resolve conflicts silently.

Never store credentials, tokens, private keys, certificate contents, customer
personal data, tenant URLs, unsanitized logs, or complete restricted metadata
exports in this file.

## Source hierarchy

1. Repository implementation and Git history.
2. Repository tests and validation evidence.
3. Versioned official vendor or project documentation.
4. Official protocol or standards documentation.
5. Authorized tenant or environment metadata.
6. Approved architecture and decision records.
7. Verified runtime observations for named versions and environments.

## Source register

### connector-csv repository

- Owner/vendor: Evolveum / repository maintainer
- Authority type: project source
- URL or repository path: repository source tree
- Subject and scope: CSV connector implementation and tests
- Product, API, protocol, or runtime version: 2.10-SNAPSHOT
- Release identifier, tag, or commit: `ecd7e038cb5e18f9c642dbce3f65f3051f6333b2` baseline before agent bootstrap
- Last verified: 2026-09-15
- Access: public
- Affected repository components: all connector code and tests
- Derived constraints: implementation evidence only; does not by itself prove ConnId or midPoint contract behavior
- Tenant-specific deviations: none known
- Deprecation or replacement status: current repository baseline
- Verification status: verified

### ConnId framework source and documentation

- Owner/vendor: ConnId project / runtime provider
- Authority type: project runtime
- URL or repository path: TODO resolve from exact dependency
- Subject and scope: SPI, framework search/result handling, filters, operation options, callbacks
- Product, API, protocol, or runtime version: TODO resolve exact version inherited or runtime-supplied
- Release identifier, tag, or commit: TODO
- Last verified: 2026-09-15
- Access: public where available
- Affected repository components: ConnId SPI integration, search, synchronization, result handling
- Derived constraints: do not attribute framework behavior until exact runtime/source revision is established
- Tenant-specific deviations: none known
- Deprecation or replacement status: current | TODO
- Verification status: pending

### midPoint consuming runtime

- Owner/vendor: Evolveum
- Authority type: runtime
- URL or repository path: `Evolveum/midpoint`
- Subject and scope: consuming application behavior relevant to ConnId connector invocation and result processing
- Product, API, protocol, or runtime version: target version must be recorded per investigation
- Release identifier, tag, or commit: TODO per measured environment
- Last verified: 2026-09-15
- Access: public
- Affected repository components: external caller boundary only
- Derived constraints: repository-local connector evidence cannot establish caller invocation counts or runtime behavior
- Tenant-specific deviations: none recorded here
- Deprecation or replacement status: current per target environment
- Verification status: pending per investigation

## Restricted tenant or environment sources

Record only the existence, scope, access boundary, sanitized derived constraints,
and verification date. Do not commit complete tenant metadata or sensitive
configuration unless explicitly approved and sanitized.

## Source conflict log

| Topic | Repository behavior | Official documentation | Tenant/runtime evidence | Decision/status |
| --- | --- | --- | --- | --- |
| ConnId runtime version | inherited/runtime supplied | pending exact resolution | pending | Must resolve before framework-level compatibility or root-cause claim |

## Unresolved documentation gaps

- Exact ConnId framework dependency/version and immutable source revision.
- Exact midPoint/runtime version for any measured end-to-end performance claim.
