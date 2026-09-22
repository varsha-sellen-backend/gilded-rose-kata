package com.gildedrose;

class AgedBrieUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        increaseQuality(item);
        item.sellIn--;
        if (item.sellIn < 0) {
            // Preserved from the original implementation: quality increases a second time
            // once past the sell date (mirroring the doubling normal items get). The
            // requirements only say Brie "increases in Quality the older it gets" and don't
            // explicitly define post-sell-date behavior, but the kata instructs treating the
            // existing code as the guide for ambiguous cases, so this is kept intentionally
            // rather than reinvented.
            increaseQuality(item);
        }
    }

    private void increaseQuality(Item item) {
        if (item.quality < 50) {
            item.quality++;
        }
    }
}
