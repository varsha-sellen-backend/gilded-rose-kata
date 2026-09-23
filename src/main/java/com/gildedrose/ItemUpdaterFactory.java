package com.gildedrose;

import java.util.Map;

class ItemUpdaterFactory {

    // Items with a single, fixed, well-known name each get their own exact-match entry.
    private static final Map<String, ItemUpdater> EXACT_MATCH_UPDATERS = Map.of(
        "Aged Brie", new AgedBrieUpdater(),
        "Backstage passes to a TAFKAL80ETC concert", new BackstagePassUpdater(),
        "Sulfuras, Hand of Ragnaros", new SulfurasUpdater()
    );

    // "Conjured" is a category, not one fixed item name (the requirements say "Conjured
    // items", plural), so it is matched by prefix rather than by one hardcoded example name.
    // Any item named "Conjured ..." routes here, not just "Conjured Mana Cake".
    private static final String CONJURED_PREFIX = "Conjured";
    private static final ItemUpdater CONJURED_UPDATER = new ConjuredItemUpdater();

    private static final ItemUpdater DEFAULT = new NormalItemUpdater();

    static ItemUpdater forItem(Item item) {
        ItemUpdater exactMatch = EXACT_MATCH_UPDATERS.get(item.name);
        if (exactMatch != null) {
            return exactMatch;
        }
        if (item.name.startsWith(CONJURED_PREFIX)) {
            return CONJURED_UPDATER;
        }
        return DEFAULT;
    }
}
