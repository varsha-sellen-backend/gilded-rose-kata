package com.gildedrose;

class NormalItemUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        QualityMath.decrease(item);
        item.sellIn--;
        if (item.sellIn < 0) {
            QualityMath.decrease(item);
        }
    }
}
