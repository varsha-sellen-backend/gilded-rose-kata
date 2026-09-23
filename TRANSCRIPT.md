# AI-Assisted Development Transcript

This is a curated summary of the session, not a raw export. Where I kept the exact prompt, it is quoted verbatim (typos included; punctuation lightly normalized), followed by a short note on what happened. I ran all tests myself in VS Code. Claude was used mainly for explanation, test generation, repetitive implementation and targeted reviews; I remained responsible for understanding the existing behavior, making the design decisions, reviewing the changes and validating the result.

## Step 1: Understanding the current code

> I'm working through the Gilded Rose Refactoring Kata in Java, in this project folder. Read GildedRose.java and Item.java. Don't suggest any refactoring yet. Walk through the updateQuality logic item-type by item-type and explain exactly what it does today, including edge cases: quality bounds at 0/50, sell_in going negative, and Sulfuras. I want to confirm I understand current behavior before changing anything.

Claude walked through `updateQuality()` item by item. I used that as a starting point, then checked the assignment constraints and the behavior against the requirements before deciding what to preserve.

## Step 2: Characterization tests

> Based on that explanation, write comprehensive JUnit characterization tests for the CURRENT behavior of GildedRose.updateQuality(): normal items, Aged Brie, Backstage passes (sell_in > 10, ==10, 6-10, 5-1, ==0, negative), Sulfuras, and quality boundary cases at 0 and 50. Don't change any production code, only add tests. Flag anything that looks like it could be unintended or ambiguous behavior so I can check it against the requirements doc.

Claude wrote the initial 19 characterization tests. I ran them myself against the unmodified code and confirmed they passed.

I separately traced the Backstage Pass boundary values (`sellIn` 10, 6, 5, 0) against the requirements, including whether the code's `sellIn < 11` correctly represents "10 days or less":

> sellIn <= 10 this is also right

I also identified two behaviors the requirements do not state explicitly: Aged Brie gaining 2 quality per day after its sell date, and Sulfuras' `sellIn` never changing. For Sulfuras, Claude had pointed to "never has to be sold" as the justification, and I checked the wording myself:

> for sulfuras Just for clarification, an item can never have its Quality increase above 50, however "Sulfuras" is a legendary item and as such its Quality is 80 and it never alters. this says Quality never alters . so keeping the Quality frozen seems ok for me

I decided to preserve both behaviors because the assignment says the existing implementation is the behavioral reference.

## Step 3: Choosing the design

Claude laid out two approaches: subclassing `Item` per item type, or an `ItemUpdater` interface with one implementation per type (Strategy).

I rejected subclassing. Subclasses would not require editing `Item.java`, but callers construct plain `Item` instances, so the subclasses would only be used if every construction site changed, and dispatching with `instanceof` would bring back the conditional logic I was trying to remove. I chose the Strategy approach.

## Step 4: Refactor

Before asking Claude to implement the refactor, I wrote the first versions of `NormalItemUpdater` and `AgedBrieUpdater` myself in VS Code. I then sent this prompt:

> Now refactor GildedRose.java to use the ItemUpdater/Strategy pattern we discussed: an ItemUpdater interface with update(Item item), one implementation per item type (NormalItemUpdater, AgedBrieUpdater, BackstagePassUpdater, SulfurasUpdater), and a factory/map that dispatches by item.name. Do NOT modify Item.java or change the type/visibility of the items field on GildedRose. Preserve current behavior exactly, including: Aged Brie's quality increasing by 2/day once past its sell date (not just 1/day), and Sulfuras' sellIn never decrementing. Add a short comment on the Sulfuras updater explaining why sellIn is frozen, citing the requirement. Do this in the smallest safe steps possible - don't rewrite everything in one shot. After each step, tell me which existing tests should still be passing unchanged. Don't touch GildedRoseTest.java. Show me each step as a diff, not the whole file dumped at once.

Claude kept my two updater implementations and completed the remaining updater classes, the factory, and the cut-over to the Strategy-based design.

Claude implemented this in two commits: the new classes first, then the cut-over. I reviewed each diff before approving the next step. The test file was not touched.

Claude could not run Maven in its environment, so it checked its work with a standalone script and reported that limitation. I ran the real tests myself in VS Code: all 19 characterization tests passed.

## Step 5: Conjured items via TDD

> Write only a failing JUnit test for a "Conjured" item that degrades in quality twice as fast as a normal item, per the requirements. Cover both before and after its sell date. Don't write any production code yet - I want to see this fail first.

Claude initially rewrote the whole test file instead of adding only the requested tests, and renamed an unrelated test display name. I did not ask for either change and flagged it immediately. The unrelated changes were reverted before committing. Separately, I removed the "FLAG:" marker from the Aged Brie test's display name myself, once I had decided to keep that behavior.

Claude used `"Conjured Mana Cake"` as the item name in every generated test. That name comes from the kata's starter fixture (`TexttestFixture.java`); the requirements themselves describe Conjured as a category of items, not a single named item.

All four Conjured tests were in place before any implementation: Claude's two rate tests and my two quality-floor tests. Claude predicted 2 failures. I ran the tests myself and got 3. I investigated the third rather than assuming Claude's prediction was complete: the chosen starting quality stayed above zero under normal degradation, so the zero floor did not hide the difference. All three failures had the same cause: there was no Conjured updater yet.

I then implemented `ConjuredItemUpdater` following the existing updater pattern. At this stage the factory matched the exact name `"Conjured Mana Cake"`. I ran the tests myself: 23 passing.

The red and green parts were kept together in commit `5f66b61`.

## Step 6: Self-review

I reviewed the refactor myself and found two issues: the quality-boundary logic was duplicated across the updater classes, and no test covered several item types in the same inventory. I decided to do the extraction myself and asked Claude only for the test:

> I reviewed the refactor and found duplicated quality-boundary logic across the updater classes.
> I want to remove that duplication without changing behavior.
> I will handle the extraction of the shared quality-boundary logic myself. Please do not implement the QualityMath extraction.
> Instead, write a JUnit test that covers multiple different item types in the same inventory and verifies that each item is updated according to its own existing rules.
> Do not modify production code.
> Do not modify existing tests except where necessary to add the new test.
> Do not rename unrelated tests or make unrelated changes.
> Show me only the proposed test change first and explain what behavior it protects.

I reviewed the proposed test before it was added, then extracted the `QualityMath` helper myself. I then asked Claude to review the combined result without changing anything:

> Review the current changes after my QualityMath extraction and the new multi-item inventory test.
> Do not make any code changes.
> Check whether:
>
> 1. the QualityMath extraction preserves the existing behavior;
> 2. the multi-item test genuinely exercises different item types;
> 3. the test is independent and meaningful;
> 4. there are any unintended changes;
> 5. the existing characterization and Conjured tests should still pass.
>
> Do not assume the result is correct because the test count increased. Identify any specific issue you find and explain why it matters.

The review found no behavioral issues with the `QualityMath` extraction or the multi-item test. Claude's separate verification copy was out of date and showed four false failures; Claude identified the stale state and corrected it. I then ran the tests myself in VS Code: 24 passing.

## Step 7: Final review before sending

After drafting the documentation, I went back to the new requirement. Every Conjured test used the same name, and I wanted to know whether the implementation was coupled to it. I asked Claude to review that specifically, without changing code:

> Review the completed refactoring against the original Gilded Rose requirements.
> Focus specifically on the new Conjured requirement:
> "Conjured" items degrade in Quality twice as fast as normal items.
> Check whether the current implementation treats Conjured as a category of items or whether it is accidentally coupled to one specific item name.
> Do not change the code yet.
> Inspect:
>
> * the Conjured tests;
> * ItemUpdaterFactory;
> * ConjuredItemUpdater;
> * the original requirements;
> * the starter code.
>
> Tell me whether matching only "Conjured Mana Cake" would fully satisfy the requirement.
> If you think the implementation is too narrow, explain what behavior should be tested to demonstrate that Conjured is a category rather than a single named item.
> Do not assume that the item name used in an existing fixture automatically defines the complete requirement.

Claude confirmed that the factory matched only the exact fixture name, so another Conjured item name would not receive the Conjured behavior.

I decided that any item whose name starts with `"Conjured"` should use `ConjuredItemUpdater`, while Aged Brie, Sulfuras and Backstage Pass matching stays exact, because changing those would alter existing behavior. I added `generalizesToOtherConjuredItemNames` using `"Conjured Bread"` and changed the factory to `startsWith("Conjured")`. This became commit `811fc5d`.

I also corrected `qualityNeverNegativeAfterSellDate`, which was asserting two things at once.

Final total: 25 tests: 19 characterization, 5 Conjured, 1 multi-item.

## Step 8: Critical review of the finished submission

Before sending, I asked Claude to review the whole submission critically, and not to improve it. From my prompt:

> Do NOT rewrite [my] solution. Do NOT make the solution artificially better. Review what is actually present.

Claude compared the refactored code with the original implementation over 30 simulated days from a grid of starting states and found no behavior change for any existing item type. It also found three problems:

- The tests covered only the lower side of each boundary. Six off-by-one changes (for example Backstage `sellIn < 11` to `< 12`, or any expiry check `< 0` to `<= 0`) left all 25 tests passing.
- The starter pom targeted Java 8, but `ItemUpdaterFactory` uses `Map.of`, which needs Java 9 or later.
- The README and this transcript disagreed about who wrote `ConjuredItemUpdater` and `QualityMath`.

I checked each finding, decided which follow-ups were appropriate, and reviewed the resulting changes. From the lessons in this session, Claude drafted a `CLAUDE.md`, which I reviewed and approved before using it for the remaining work (see Steering files below).

Claude then raised the build to Java 17 and added six boundary tests: Backstage Passes at `sellIn` 11, 6, and 1, plus `sellIn` 1 for Normal items, Aged Brie, and Conjured items. I reviewed these cases as targeted checks for the off-by-one boundaries; each test fails when the corresponding boundary is changed, confirming that the tests protect those rules.

I also had Claude turn the original-versus-refactored behavior check into a repeatable test, `OriginalBehaviorComparisonTest`. It runs a verbatim copy of the original `updateQuality()` alongside the refactored implementation and reports the first behavioral difference. Since this comparison does not cover the newly introduced Conjured behavior, I had Claude add a separate multi-day Conjured test that follows an item across its sell date and down to the quality floor.

The review also flagged that the Sulfuras code comment contradicted the reasoning documented in the README. I decided that the comment should be made consistent, and Claude applied the correction.

Finally, I reviewed the resulting changes and ran `mvn clean test` myself. The final test suite completed with 33 tests passing.

## Who did what

**Me:**

- Set the constraints and order of work: understand the existing code, characterize it with tests, choose the design, then refactor in small steps.
- Checked the requirements and manually traced the Backstage Pass boundaries.
- Identified the unclear Aged Brie and Sulfuras behaviors and decided how to handle them.
- Rejected subclassing and chose the Strategy pattern.
- Wrote the first versions of `NormalItemUpdater` and `AgedBrieUpdater`.
- Directed Claude's implementation with explicit constraints and small, reviewable steps, and reviewed each diff.
- Ran the test suite myself in VS Code at every stage.
- Caught Claude changing more of the test file than requested, including an unrelated test rename.
- Investigated the unexpected third Conjured test failure.
- Implemented `ConjuredItemUpdater` and wrote the two Conjured quality-floor tests.
- Identified the duplicated quality-boundary logic and the missing multi-item test during self-review.
- Extracted `QualityMath` myself and explicitly told Claude not to.
- Directed a targeted review of whether Conjured matching was coupled to one name, then decided on prefix matching, added the `"Conjured Bread"` test and changed the factory.
- Asked Claude for a critical review of the finished submission, checked its findings, and decided the follow-up changes, including the documentation corrections.
- Made the final engineering and documentation decisions.

**Claude:**

- Explained the existing `updateQuality()` behavior and highlighted edge cases.
- Generated the characterization tests and the two failing Conjured rate tests, using the starter fixture's `"Conjured Mana Cake"` as the only item name.
- Laid out the design alternatives.
- Wrote `BackstagePassUpdater`, `SulfurasUpdater` and the factory/cut-over under the Strategy design and constraints I specified.
- Wrote the multi-item inventory test.
- Reviewed the `QualityMath` extraction and the Conjured matching when asked, without changing code.
- Identified and corrected its own stale verification copy.
- Reviewed the finished submission critically, drafted `CLAUDE.md`, and wrote the six boundary tests, the comparison test, the multi-day Conjured test, the Java version change and the documentation corrections I approved.

## Steering files

I did not use a `CLAUDE.md` or other steering file during the main session; all guidance was given through prompts. After the review in Step 8, I added `CLAUDE.md` to the repository and it was used for the follow-up changes. It turns the problems from this session into standing rules:

- do not modify `Item.java` or the `items` field;
- do not edit, rename or reformat tests outside the current task (Step 5);
- only make the changes requested;
- a change is not done until `mvn test` passes, and say so if Maven cannot be run (Step 4);
- test both sides of every threshold (Step 8).

## Final state

The refactoring and tests are complete and pushed to https://github.com/varsha-sellen-backend/gilded-rose-kata

**33 tests passing:** 19 characterization, 6 Conjured, 1 multi-item, 6 boundary, 1 comparison with the original implementation.

Commit history:

1. Original starter code
2. Characterization tests (19)
3. `ItemUpdater` classes added, not yet used
4. `updateQuality()` cut over to the factory
5. `5f66b61`: Add Conjured item support via TDD (red and green in one commit)
6. `2bde864`: Extract `QualityMath` helper; add multi-item integration test
7. `811fc5d`: Route Conjured items by name prefix, not exact match
8. `3ec7737`: Add `CLAUDE.md` steering file
9. `180756c`: Build with Java 17
10. `ec1f745`: Add boundary tests for the upper side of each threshold
11. `8e6f78e`: Add behavior comparison test against the original implementation
12. `e7cc57a`: Align Sulfuras comment with the requirements
13. `51c563f`: Add multi-day Conjured test
14. Add README and AI-assisted development transcript

Throughout, Claude was used as an engineering assistant working from constrained prompts, not as an autonomous agent: every change was reviewed and tested by me before it was accepted.