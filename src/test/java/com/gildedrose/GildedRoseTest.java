package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    @Nested
    @DisplayName("Normal items")
    class NormalItems {

        @Test
        @DisplayName("quality decreases by 1 per day before sell date")
        void decreasesByOneBeforeSellDate() {
            Item[] items = new Item[]{new Item("+5 Dexterity Vest", 10, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(9, items[0].sellIn);
            assertEquals(19, items[0].quality);
        }

        @Test
        @DisplayName("quality decreases by 2 per day after sell date")
        void decreasesByTwoAfterSellDate() {
            Item[] items = new Item[]{new Item("+5 Dexterity Vest", 0, 10)};
            new GildedRose(items).updateQuality();
            assertEquals(-1, items[0].sellIn);
            assertEquals(8, items[0].quality);
        }

        @Test
        @DisplayName("quality never goes negative")
        void qualityNeverNegative() {
            Item[] items = new Item[]{new Item("+5 Dexterity Vest", 5, 0)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }

        @Test
        @DisplayName("quality never goes negative even after sell date (double decrement)")
        void qualityNeverNegativeAfterSellDate() {
            Item[] items = new Item[]{new Item("+5 Dexterity Vest", 0, 1)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }
    }

    @Nested
    @DisplayName("Aged Brie")
    class AgedBrie {

        @Test
        @DisplayName("quality increases by 1 per day before sell date")
        void increasesByOneBeforeSellDate() {
            Item[] items = new Item[]{new Item("Aged Brie", 10, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(21, items[0].quality);
        }

        @Test
        @DisplayName("quality increases by 2 per day after sell date (preserved existing behavior)")
        void increasesByTwoAfterSellDate() {
            Item[] items = new Item[]{new Item("Aged Brie", 0, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(22, items[0].quality);
        }

        @Test
        @DisplayName("quality never exceeds 50")
        void qualityNeverExceeds50() {
            Item[] items = new Item[]{new Item("Aged Brie", 10, 50)};
            new GildedRose(items).updateQuality();
            assertEquals(50, items[0].quality);
        }

        @Test
        @DisplayName("quality never exceeds 50 even with double increment after sell date")
        void qualityNeverExceeds50AfterSellDate() {
            Item[] items = new Item[]{new Item("Aged Brie", 0, 49)};
            new GildedRose(items).updateQuality();
            assertEquals(50, items[0].quality);
        }
    }

    @Nested
    @DisplayName("Backstage passes")
    class BackstagePasses {

        private static final String NAME = "Backstage passes to a TAFKAL80ETC concert";

        @Test
        @DisplayName("sellIn > 10: quality increases by 1")
        void moreThanTenDaysLeft() {
            Item[] items = new Item[]{new Item(NAME, 15, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(21, items[0].quality);
        }

        @Test
        @DisplayName("sellIn == 10: quality increases by 2")
        void exactlyTenDaysLeft() {
            Item[] items = new Item[]{new Item(NAME, 10, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(22, items[0].quality);
        }

        @Test
        @DisplayName("sellIn between 6 and 10: quality increases by 2")
        void betweenSixAndTenDaysLeft() {
            Item[] items = new Item[]{new Item(NAME, 7, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(22, items[0].quality);
        }

        @Test
        @DisplayName("sellIn == 5: quality increases by 3")
        void exactlyFiveDaysLeft() {
            Item[] items = new Item[]{new Item(NAME, 5, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(23, items[0].quality);
        }

        @Test
        @DisplayName("sellIn between 1 and 5: quality increases by 3")
        void betweenOneAndFiveDaysLeft() {
            Item[] items = new Item[]{new Item(NAME, 3, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(23, items[0].quality);
        }

        @Test
        @DisplayName("sellIn == 0 (day of concert): quality drops to 0")
        void dayOfConcert() {
            Item[] items = new Item[]{new Item(NAME, 0, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }

        @Test
        @DisplayName("sellIn negative (after concert): quality stays 0")
        void afterConcert() {
            Item[] items = new Item[]{new Item(NAME, -1, 0)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }

        @Test
        @DisplayName("quality never exceeds 50 even with triple increment near concert")
        void qualityNeverExceeds50() {
            Item[] items = new Item[]{new Item(NAME, 5, 49)};
            new GildedRose(items).updateQuality();
            assertEquals(50, items[0].quality);
        }
    }

    @Nested
    @DisplayName("Sulfuras")
    class Sulfuras {

        private static final String NAME = "Sulfuras, Hand of Ragnaros";

        @Test
        @DisplayName("quality never changes")
        void qualityNeverChanges() {
            Item[] items = new Item[]{new Item(NAME, 5, 80)};
            new GildedRose(items).updateQuality();
            assertEquals(80, items[0].quality);
        }

        @Test
        @DisplayName("sellIn never changes either")
        void sellInNeverChanges() {
            Item[] items = new Item[]{new Item(NAME, 5, 80)};
            new GildedRose(items).updateQuality();
            assertEquals(5, items[0].sellIn);
        }

        @Test
        @DisplayName("quality unaffected even with negative sellIn")
        void qualityUnaffectedWithNegativeSellIn() {
            Item[] items = new Item[]{new Item(NAME, -5, 80)};
            new GildedRose(items).updateQuality();
            assertEquals(80, items[0].quality);
            assertEquals(-5, items[0].sellIn);
        }
    }

    @Nested
    @DisplayName("Conjured items")
    class Conjured {

        private static final String NAME = "Conjured Mana Cake";

        @Test
        @DisplayName("quality decreases by 2 per day before sell date (twice normal rate)")
        void decreasesByTwoBeforeSellDate() {
            Item[] items = new Item[]{new Item(NAME, 10, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(9, items[0].sellIn);
            assertEquals(18, items[0].quality);
        }

        @Test
        @DisplayName("quality decreases by 4 per day after sell date (twice normal's post-expiry rate)")
        void decreasesByFourAfterSellDate() {
            Item[] items = new Item[]{new Item(NAME, 0, 20)};
            new GildedRose(items).updateQuality();
            assertEquals(-1, items[0].sellIn);
            assertEquals(16, items[0].quality);
        }

        @Test
        @DisplayName("quality never goes negative")
        void qualityNeverNegative() {
            Item[] items = new Item[]{new Item(NAME, 10, 1)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }

        @Test
        @DisplayName("quality never goes negative even after sell date")
        void qualityNeverNegativeAfterSellDate() {
            Item[] items = new Item[]{new Item(NAME, 0, 3)};
            new GildedRose(items).updateQuality();
            assertEquals(0, items[0].quality);
        }
    }
    @Nested
    @DisplayName("Multiple items in one inventory")
    class MultipleItemsInInventory {

        @Test
        @DisplayName("each item updates independently and correctly when processed together")
        void eachItemUpdatesIndependently() {
            Item[] items = new Item[]{
                new Item("+5 Dexterity Vest", 10, 20),
                new Item("Aged Brie", 10, 20),
                new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20),
                new Item("Sulfuras, Hand of Ragnaros", 10, 80),
                new Item("Conjured Mana Cake", 10, 20)
            };

            new GildedRose(items).updateQuality();

            assertEquals(9, items[0].sellIn);
            assertEquals(19, items[0].quality);

            assertEquals(9, items[1].sellIn);
            assertEquals(21, items[1].quality);

            assertEquals(9, items[2].sellIn);
            assertEquals(22, items[2].quality);

            assertEquals(10, items[3].sellIn);
            assertEquals(80, items[3].quality);

            assertEquals(9, items[4].sellIn);
            assertEquals(18, items[4].quality);
        }
    }
}
