# Gilded Rose Refactoring Kata: PTV Logistics Assignment

Submitted by Varsha Sellen for the technical assignment following the second-round interview.

## What this is

A refactor of the Gilded Rose kata's `updateQuality()` method, together with support for the new "Conjured" item type, implemented in Java with JUnit 5 and Maven.

The git history contains 14 commits. It shows the actual sequence of the work rather than presenting only the final state: characterization tests, the refactor in small steps, the new feature through TDD, self-review, a fix following a final review of the submission, follow-up changes from a critical review of the finished submission (a `CLAUDE.md` steering file, the Java version, boundary tests, a test comparing the refactored code with the original, a corrected code comment and a multi-day Conjured test), and finally this README and the transcript.

Requires JDK 17 or later. Run it with:

```bash
mvn test
```

## Approach

1. **Understand the existing behavior.**  
   Read the existing code and requirements without changing anything, to understand `updateQuality()` item type by item type and confirm the constraints before making design decisions.

2. **Establish a safety net.**  
   Had Claude generate 19 characterization tests, then ran them myself against the unmodified code to confirm they captured its current behavior before refactoring. I also traced the important Backstage Pass boundary cases by hand against the requirements rather than relying only on the tests passing.

3. **Refactor the existing logic.**  
   Replaced the nested conditionals in `updateQuality()` with a Strategy pattern: an `ItemUpdater` interface, one implementation per existing item type (`NormalItemUpdater`, `AgedBrieUpdater`, `BackstagePassUpdater`, `SulfurasUpdater`), and an `ItemUpdaterFactory` for dispatch. I chose the Strategy approach after considering the alternatives, and wrote the first versions of `NormalItemUpdater` and `AgedBrieUpdater` myself. I then used Claude to help complete the repetitive implementation work and factory/cut-over under the constraints I specified. The refactor was done in two steps, adding the new classes first, then switching `GildedRose` over, with each diff reviewed before moving on. `Item.java` and the `items` field were left untouched, as required by the assignment.

4. **Add Conjured through TDD.**  
   Started with failing Conjured tests (Claude generated the two rate tests and I wrote the two quality-floor tests), confirmed the failures were for the expected reason, and then implemented the minimal `ConjuredItemUpdater` myself, following the established pattern. I ran the tests myself and verified the result.

5. **Self-review the refactor.**  
   During my review, I found two issues: duplicated quality-clamp logic across four classes and no test covering multiple item types being processed together in the same inventory. I extracted the shared `QualityMath` helper myself and asked Claude only for the multi-item integration test, which I reviewed before it was added. I then asked Claude to review the combined result without changing code, and ran the tests myself.

6. **Review the finished submission.**  
   I then reviewed the finished submission before sending it. That review exposed a genuine gap in how Conjured items were matched by name, as well as issues in the documentation. I evaluated the findings and decided which changes to make. The Conjured matching was changed to use a name prefix, the relevant test was added, and the documentation was updated. The full reasoning and review process is documented in `TRANSCRIPT.md`.

7. **Get a critical review of the finished submission.**  
   Before sending, I asked Claude to review the complete submission critically, without rewriting it. It compared the refactored code with the original implementation over 30 simulated days from a grid of starting states and found no behavior change for any existing item type; that comparison is now a test in the repository (`OriginalBehaviorComparisonTest`). It also found that the tests covered only the lower side of each boundary, that the starter pom targeted Java 8 while the factory uses `Map.of`, and that parts of this documentation disagreed about who wrote what. I checked each finding and decided the follow-ups: a `CLAUDE.md` steering file, a Java 17 build, six boundary tests, the comparison test, a multi-day Conjured test, and corrected documentation.

## Decisions where the requirements are unclear

Two behaviors of the original code are either not stated in the requirements or sit in tension with them. Following the assignment's guidance that the existing implementation is the behavioral reference, I preserved both behaviors and documented them:

- **Aged Brie's quality increase doubles once past its sell date.** The requirements say quality *degrades* twice as fast after the sell date, but do not specify whether the same doubling applies to Aged Brie's *increase*. The original code doubles it, so I preserved that behavior.

- **Sulfuras' `sellIn` never decrements.** The requirements say the system lowers both values for every item, but also that Sulfuras "never has to be sold". The original code never decrements it, so I preserved that as the behavioral reference rather than reinterpreting the requirements. A code comment records the reasoning.

## Design choices for the Conjured feature

Conjured is new behavior, so there was no existing implementation to preserve. These are the design choices I made:

- **Matching by name prefix.** Any item whose name starts with `"Conjured"` is routed to `ConjuredItemUpdater` by `ItemUpdaterFactory`. The requirements describe Conjured as a category of items rather than a single named item, so matching only one hardcoded name would be too narrow. The tests use `"Conjured Mana Cake"` as the standard example and `generalizesToOtherConjuredItemNames` uses a different name to verify the prefix behavior.

- **Prefix matching only for Conjured.** The existing Aged Brie, Sulfuras and Backstage Pass items continue to use their original exact-name matching. Changing that existing behavior would go beyond the scope of the refactoring. The asymmetry is therefore intentional: prefix matching was introduced only for the new Conjured feature.

- **Quality degradation rate.** A Conjured item loses 2 quality per day before its sell date and 4 per day after it, twice the normal degradation rate in both phases. Quality is still clamped at 0.

## How AI was used

I used Claude Code throughout the exercise, but I drove the work by defining the constraints, checking the requirements, making the design decisions, reviewing the changes, and running the tests myself. The prompts and the relevant session history are included in `TRANSCRIPT.md`.

In particular:

- I made the design decisions and the decisions on unclear requirements, including Strategy versus subclassing, the Aged Brie and Sulfuras behaviors, and the final Conjured prefix-matching decision.
- I wrote the first versions of `NormalItemUpdater` and `AgedBrieUpdater`.
- I used Claude primarily for repetitive implementation and test-writing work once the design and constraints were established.
- I reviewed Claude's diffs and ran the test suite myself in VS Code rather than relying solely on Claude's verification.
- During self-review, I identified the duplicated quality guards and the missing multi-item test. I extracted `QualityMath` myself and asked Claude only for the test.
- Before sending the submission, I reviewed the final implementation and documentation myself and decided which changes to make.

A detailed breakdown of what I did versus what Claude did is included in `TRANSCRIPT.md`.

I did not use a `CLAUDE.md` or other steering file during the main session; all guidance was given through prompts. Afterwards I added a `CLAUDE.md` that turns the problems from that session into standing rules, and it was used for the follow-up changes.

## Who did what

**Me:**

- Defined the overall workflow and constraints.
- Checked the requirements and manually verified important boundary cases, especially Backstage Passes.
- Identified the unclear Aged Brie and Sulfuras behaviors and decided how to handle them.
- Considered the design alternatives, rejected subclassing because `Item.java` could not be modified, and chose the Strategy pattern.
- Wrote the first versions of `NormalItemUpdater` and `AgedBrieUpdater`.
- Implemented `ConjuredItemUpdater` and extracted `QualityMath` myself.
- Directed Claude's implementation with explicit constraints and small, reviewable steps.
- Reviewed Claude's diffs before approving the refactoring steps.
- Ran the test suite myself in VS Code.
- Identified the duplicated quality guards and missing multi-item inventory test during self-review.
- Investigated the unexpected third Conjured test failure rather than relying on Claude's prediction.
- Checked whether Conjured matching was coupled to one item name, had Claude review it, then changed the factory to prefix matching myself.
- Asked Claude for a critical review of the finished submission, checked its findings, and decided the follow-up changes.
- Reviewed the final submission and made the final engineering and documentation decisions.

**Claude:**

- Explained the existing `updateQuality()` behavior item by item and highlighted relevant edge cases.
- Generated the initial characterization tests and later the two Conjured rate tests.
- Helped compare the design alternatives.
- Completed the repetitive implementation work for the updater classes and factory/cut-over based on the Strategy design and constraints I specified.
- Wrote the multi-item inventory test I requested after my self-review, and reviewed the `QualityMath` extraction without changing code.
- Confirmed, in a targeted review I requested, that Conjured matching was coupled to one item name.
- Reviewed the finished submission critically, drafted `CLAUDE.md` from the session's lessons, and wrote the six boundary tests, the comparison test, the multi-day Conjured test, the Java version change and the documentation corrections I approved.

The division of work was intentional: I used Claude to accelerate repetitive implementation and test-writing work, while I retained responsibility for the requirements, architecture, decisions, validation, review, and final acceptance of the changes.

## Limitations / what I'd do differently

- **Commit 6 mixes two changes.** The `QualityMath` extraction and multi-item test are in the same commit. I could have rewritten the history to separate them, but I chose to leave the history intact because it records what actually happened during the exercise.

- **The kata's TextTest golden-master fixture wasn't run.** Instead, `OriginalBehaviorComparisonTest` runs a verbatim copy of the original `updateQuality()` side by side with the refactored code for 30 simulated days from a grid of starting states and asserts that they never diverge (Conjured items excluded, since that change is intended). It only covers the names and value ranges in its grid, and it keeps a copy of the legacy code in the test sources, which I would delete once the refactor is accepted.

- **Conjured matching is a simple prefix check.** It is case-sensitive, and it also matches names with no space after the prefix, such as "ConjuredX". That is acceptable for this kata's item names; with real supplier data I would agree the naming rule first and match on a normalized word rather than a raw prefix.

- **The design does not currently compose combined behaviors.** The current structure has five small item updaters, an `ItemUpdaterFactory`, and the shared `QualityMath` helper. For the five independent item types in this exercise, that keeps each class focused and makes adding the new Conjured type straightforward. However, a future requirement such as a Conjured item that is also a Backstage Pass would expose a limitation: the current `ItemUpdater` implementations do not compose. If such combinations were required, I would consider composable rate and floor rules rather than introducing a separate class for every combination.
