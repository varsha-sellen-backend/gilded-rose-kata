package com.gildedrose;

class BackstagePassUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        QualityMath.increase(item);
        if (item.sellIn < 11) {
            QualityMath.increase(item);
        }
        if (item.sellIn < 6) {
            QualityMath.increase(item);
        }

        item.sellIn--;

        if (item.sellIn < 0) {
            item.quality = 0;
        }
    }
}
