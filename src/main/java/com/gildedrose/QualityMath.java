package com.gildedrose;

/**
 * Shared quality-bound logic: Quality is never negative and never exceeds 50
 * (per the requirements). Every updater that increments or decrements quality
 * routes through here instead of re-implementing the same guard.
 */
final class QualityMath {

    private QualityMath() {
    }

    static void increase(Item item) {
        if (item.quality < 50) {
            item.quality++;
        }
    }

    static void decrease(Item item) {
        if (item.quality > 0) {
            item.quality--;
        }
    }
}
