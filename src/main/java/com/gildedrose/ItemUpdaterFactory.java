package com.gildedrose;

import java.util.Map;

class ItemUpdaterFactory {

    private static final Map<String, ItemUpdater> UPDATERS = Map.of(
        "Aged Brie", new AgedBrieUpdater(),
        "Backstage passes to a TAFKAL80ETC concert", new BackstagePassUpdater(),
        "Sulfuras, Hand of Ragnaros", new SulfurasUpdater(),
        "Conjured Mana Cake", new ConjuredItemUpdater()
    );

    private static final ItemUpdater DEFAULT = new NormalItemUpdater();

    static ItemUpdater forItem(Item item) {
        return UPDATERS.getOrDefault(item.name, DEFAULT);
    }
}
