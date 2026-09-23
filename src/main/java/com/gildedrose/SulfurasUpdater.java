package com.gildedrose;

class SulfurasUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        // Sulfuras is a legendary item. The requirements state it "never has to be sold or
        // decreases in Quality", and separately that its Quality "is 80 and it never alters".
        // The general rule that the system lowers both values for every item is in tension with
        // "never has to be sold". The original implementation never decrements sellIn and is the
        // behavioral reference, so sellIn is intentionally left untouched here.
    }
}
