package com.gildedrose;

class ConjuredItemUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        decreaseQualityTwice(item);
        item.sellIn--;
        if (item.sellIn < 0) {
            decreaseQualityTwice(item);
        }
    }

    private void decreaseQualityTwice(Item item) {
        decreaseQuality(item);
        decreaseQuality(item);
    }

    private void decreaseQuality(Item item) {
        if (item.quality > 0) {
            item.quality--;
        }
    }
}
