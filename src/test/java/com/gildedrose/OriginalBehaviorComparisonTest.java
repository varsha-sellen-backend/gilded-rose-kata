package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks that the refactoring preserved behavior. Runs the refactored GildedRose side by side
 * with a verbatim copy of the original updateQuality() logic (starter commit c43eff0) for 30
 * simulated days, from every starting state in a grid of item names, sellIn and quality values.
 *
 * Conjured items are deliberately excluded: the original code treated them as normal items, and
 * changing that was the new requirement.
 */
class OriginalBehaviorComparisonTest {

    private static final String[] NAMES = {
        "+5 Dexterity Vest",
        "Elixir of the Mongoose",
        "Aged Brie",
        "Backstage passes to a TAFKAL80ETC concert",
        "Sulfuras, Hand of Ragnaros",
        // near-miss names must still behave like normal items
        "aged brie",
        "Backstage passes",
        "Sulfuras",
        ""
    };
    private static final int MIN_SELL_IN = -5;
    private static final int MAX_SELL_IN = 15;
    private static final int MIN_QUALITY = -1;
    private static final int MAX_QUALITY = 80;
    private static final int DAYS = 30;

    @Test
    @DisplayName("matches the original implementation for 30 days from every non-Conjured starting state")
    void matchesOriginalImplementation() {
        for (String name : NAMES) {
            for (int sellIn = MIN_SELL_IN; sellIn <= MAX_SELL_IN; sellIn++) {
                for (int quality = MIN_QUALITY; quality <= MAX_QUALITY; quality++) {
                    Item[] original = {new Item(name, sellIn, quality)};
                    Item[] refactored = {new Item(name, sellIn, quality)};
                    GildedRose app = new GildedRose(refactored);

                    for (int day = 1; day <= DAYS; day++) {
                        OriginalGildedRose.updateQuality(original);
                        app.updateQuality();
                        if (original[0].sellIn != refactored[0].sellIn
                                || original[0].quality != refactored[0].quality) {
                            assertEquals(original[0].toString(), refactored[0].toString(),
                                "Diverged on day " + day + " starting from ("
                                    + name + ", " + sellIn + ", " + quality + ")");
                        }
                    }
                }
            }
        }
    }

    /** The original updateQuality() from the starter code, unchanged except for being static. */
    private static final class OriginalGildedRose {

        static void updateQuality(Item[] items) {
            for (int i = 0; i < items.length; i++) {
                if (!items[i].name.equals("Aged Brie")
                        && !items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                    if (items[i].quality > 0) {
                        if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
                            items[i].quality = items[i].quality - 1;
                        }
                    }
                } else {
                    if (items[i].quality < 50) {
                        items[i].quality = items[i].quality + 1;

                        if (items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                            if (items[i].sellIn < 11) {
                                if (items[i].quality < 50) {
                                    items[i].quality = items[i].quality + 1;
                                }
                            }

                            if (items[i].sellIn < 6) {
                                if (items[i].quality < 50) {
                                    items[i].quality = items[i].quality + 1;
                                }
                            }
                        }
                    }
                }

                if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
                    items[i].sellIn = items[i].sellIn - 1;
                }

                if (items[i].sellIn < 0) {
                    if (!items[i].name.equals("Aged Brie")) {
                        if (!items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                            if (items[i].quality > 0) {
                                if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
                                    items[i].quality = items[i].quality - 1;
                                }
                            }
                        } else {
                            items[i].quality = items[i].quality - items[i].quality;
                        }
                    } else {
                        if (items[i].quality < 50) {
                            items[i].quality = items[i].quality + 1;
                        }
                    }
                }
            }
        }
    }
}
