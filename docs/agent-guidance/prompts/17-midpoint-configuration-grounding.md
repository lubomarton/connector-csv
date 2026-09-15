# Prompt 17: midPoint configuration grounding

```text
Ground the proposed midPoint configuration work in sources that match the
target midPoint version. This is a read-only analysis. Do not modify files,
import objects, execute tasks, call live resources, or change any environment.

## Establish the baseline

Identify and report:

1. the exact target midPoint version, edition, distribution, and relevant
   connector versions;
2. the repository evidence that establishes this baseline;
3. the matching official documentation version;
4. the `Evolveum/midpoint` source tag or immutable commit;
5. the schema artifacts from that version and how they are obtained;
6. the revision of official distribution examples and
   `Evolveum/midpoint-samples` used for comparison;
7. any relevant connector repository and its tag or commit.

Stop and report a prerequisite if the target version cannot be established.
Do not silently substitute `latest`, a development branch, or model memory.

## Verify the proposed configuration

For every material construct, verify as applicable:

- XML namespaces, QNames, element and item names, types, paths,
  multiplicities, containers, references, relations, and extension items;
- object type, OID, archetype, lifecycle, assignment, inducement, and import
  dependencies;
- mapping phases, strengths, channels, expression variables, search behavior,
  and evaluation context;
- correlation, synchronization reactions, object templates, policy rules,
  marks, authorizations, delegation, certification, reports, tasks, and
  activities;
- resource schema, object classes, kinds, intents, delineation, capabilities,
  caching, fetch strategy, paging, LiveSync, and connector behavior;
- security boundaries including expressions, scripts, `runPrivileged`,
  credentials, authorizations, tenant data, and live-state operations;
- performance-sensitive searches, mappings, assignments, org closure,
  repository queries, task batching, worker threads, and repeated remote calls.

Use this evidence order when sources disagree:

1. target-version schema and verified runtime behavior;
2. matching Java implementation and repository tests;
3. matching versioned official documentation;
4. matching distribution configuration and official examples;
5. current documentation or examples from another version;
6. project-local examples and observations;
7. unofficial material.

Explain conflicts instead of resolving them silently. Java implementation can
explain runtime behavior but does not automatically make an internal detail a
supported public contract. Examples demonstrate a use, but do not by
themselves prove schema validity, completeness, or cross-version compatibility.

## Evidence report

For each important conclusion, provide:

- configuration construct or claimed behavior;
- target version and affected repository file;
- schema evidence, including definition and multiplicity;
- Java class, method, tag, or commit when runtime tracing was required;
- official documentation page and version;
- official example path and revision, if used;
- repository-local test or sanitized runtime evidence, if available;
- classification: confirmed contract, confirmed implementation behavior,
  version-dependent, tenant-dependent, conflicting, or unresolved;
- security and performance implications;
- required test or acceptance evidence.

Conclude with:

1. constructs safe to use for the target version;
2. incompatible, deprecated, or unproven constructs;
3. source conflicts and missing evidence;
4. exact implications for the implementation plan;
5. stop conditions requiring a version, source, schema, connector, or runtime
   decision.

Do not invent XML elements, extension definitions, relations, OIDs, expression
variables, connector capabilities, or runtime behavior. Do not expose tenant
URLs, credentials, tokens, certificates, private keys, personal data, or
unsanitized configuration. Complete the report and wait for approval.
```
