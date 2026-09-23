# CLAUDE.md

## Project
Gilded Rose refactoring kata. Java 17, Maven, JUnit 5. Run tests with `mvn test`.

## Hard constraints
- Never modify `Item.java` or the `items` field of `GildedRose` (name, type or visibility).
- The original `updateQuality()` behavior is the reference. Any behavior change must be named explicitly and approved first.
- Do not edit, rename or reformat tests that are not part of the current task.
- Make only the change requested. Propose anything else instead of doing it.

## Workflow
- Before changing non-trivial code, explain the current behavior and your plan.
- Work in small steps and show each change as a diff for review.
- New behavior starts with a failing test; show it fails for the expected reason, then implement.
- A change is not done until `mvn test` passes. If you cannot run Maven, say so instead of claiming the tests pass.

## Testing
- Test through `GildedRose.updateQuality()`, not internals.
- For every threshold, test both sides (e.g. Backstage sellIn 11 and 10, 6 and 5; sellIn 1 and 0 for expiry).

## Commits
- One logical change per commit; the message explains why.
