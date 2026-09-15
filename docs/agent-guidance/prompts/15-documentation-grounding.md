# Prompt 15: authoritative documentation grounding

```text
Perform the documentation-grounding phase for this repository.
Do not modify any repository files.

Use the repository discovery results and the source index at:

docs/references/authoritative-sources.md

External-access rules:

- Use official vendor, project, runtime, and protocol documentation listed or
  approved in the source index.
- Do not use blogs, forums, Q&A pages, generated summaries, or unofficial forks
  as primary authority when an official source exists.
- Keep external access read-only.
- Do not authenticate to customer tenants or live systems, call tenant APIs,
  download tenant data, or access restricted sources without explicit approval.
- Never display credentials, keys, certificates containing private material,
  assertions, tokens, tenant URLs, personal data, restricted metadata, or
  unsanitized logs/exports.
- Do not copy complete vendor documentation into the repository or report.

For every behavior relevant to the proposed work, compare where applicable:

1. repository implementation/configuration and Git history;
2. repository tests;
3. versioned official vendor/project documentation;
4. official protocol or standards documentation;
5. tenant or environment metadata, only when already available and authorized;
6. approved architecture and decisions;
7. verified runtime observations.

If the selected profile is `midpoint-configuration`, also run the checks in
`docs/agent-guidance/prompts/17-midpoint-configuration-grounding.md`. Use the
exact target-version schemas and Java source revision; do not treat a current
or unversioned example as proof of compatibility with an older release.

For each material behavior, distinguish these states rather than collapsing
all evidence into a single "supported" claim:

- vendor-supported behavior;
- repository-implemented behavior;
- repository-configured behavior;
- repository-test-validated behavior;
- runtime-validated behavior;
- approved architecture/decision;
- recommended best practice;
- assumption requiring validation;
- future state.

For integration or architecture behavior, identify where applicable:

- system of record/source of authority;
- management plane/trust boundary;
- provisioning path;
- synchronization direction;
- object, attribute, and association ownership.

Produce a matrix with:

- feature, contract, or documented claim;
- affected object/capability;
- repository files, configuration, classes, and methods;
- current implementation/configuration;
- repository test evidence;
- verified runtime evidence, if available;
- system of record/source of authority, if applicable;
- management plane and provisioning path, if applicable;
- synchronization direction/ownership, if applicable;
- authoritative source;
- source product/API/protocol version or scope;
- source status: versioned, current/latest, deprecated, replaced, historical,
  inaccessible, or unclear;
- verification date;
- officially documented behavior;
- tenant-specific evidence, if authorized;
- evidence classification;
- status: compliant, partially compliant, defect, undocumented extension,
  tenant-dependent, unclear, conflicting evidence, or not applicable;
- security impact;
- performance/scalability impact;
- operational impact;
- migration/upgrade impact;
- missing test coverage;
- documentation gap;
- recommended next step.

Security handling:

- If credentials, private keys, secret containers, tokens, sensitive
  certificates, customer identifiers, tenant URLs, personal data, restricted
  metadata, or unsanitized production exports/logs are discovered, do not print
  their contents.
- Record only path/type, exposure impact, and required remediation.
- Classify sensitive repository artifacts as publication-blocking until they are
  remediated or explicitly dispositioned.
- If a value may have been real, recommend rotation/revocation and, when
  relevant, repository-history remediation.
- Do not assume deletion from the current working tree removes historical
  exposure.

Version handling:

- Record documentation product/API/protocol versions and verification date.
- Identify unversioned or `latest` references.
- Do not assume current documentation describes older supported versions.
- Record inaccessible, deprecated, replaced, or contradictory sources.
- Do not resolve source conflicts silently.
- Distinguish validated, supported, known-compatible, not-validated, deprecated,
  and historical claims when the evidence allows it.

Separate confirmed repository behavior, vendor-documented behavior,
repository-configured behavior, test evidence, runtime evidence, tenant-specific
facts, approved project decisions, recommendations, assumptions, future state,
and unresolved discrepancies.

Conclude with:

- confirmed facts;
- contradictions or defects;
- documentation gaps;
- security blockers;
- version/compatibility uncertainties;
- missing validation evidence;
- recommended remediation order;
- open questions requiring approval or additional evidence.

Do not implement changes. Complete the report and wait for approval.
```
