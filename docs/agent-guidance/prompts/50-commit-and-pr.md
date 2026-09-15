# Prompt 50: commit and pull request authorization

Use only after reviewing the final implementation report. Fill all placeholders
and remove every action that is not being authorized.

```text
The final implementation review is accepted for:

{{TASK}}

Authorized Git actions:
{{EXACT_GIT_ACTIONS}}

Target branch and remote:
{{TARGET_BRANCH_AND_REMOTE}}

Commit message:
{{COMMIT_MESSAGE}}

Pull request base and title, if PR creation is authorized:
{{PR_DETAILS_OR_NOT_AUTHORIZED}}

Before acting:
- confirm the current branch, tracking branch, status, and exact diff scope;
- confirm no credentials, local configuration, build output, IDE files,
  unrelated changes, or sensitive data are included;
- stop if repository state differs from the reviewed state.

Perform only the explicitly authorized Git actions. Do not force-push, rewrite
history, delete branches, merge, deploy, publish a release, or modify other
remote state unless separately listed above.

Afterward report the commit identifier, pushed branch, PR link if created,
checks triggered, and final repository status. Do not claim remote checks passed
until their completed result has been observed.
```
