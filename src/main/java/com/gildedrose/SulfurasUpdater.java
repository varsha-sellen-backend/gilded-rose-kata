package com.gildedrose;

class SulfurasUpdater implements ItemUpdater {

    @Override
    public void update(Item item) {
        // Sulfuras is a legendary item. The requirements state it "never has to be sold or
        // decreases in Quality", and separately that its Quality "is 80 and it never alters".
        // Nothing in the spec describes an active sell-in countdown for an item that never
        // needs to be sold, so sellIn is intentionally left untouched here, matching the
        // original implementation's behavior.
    }
}
