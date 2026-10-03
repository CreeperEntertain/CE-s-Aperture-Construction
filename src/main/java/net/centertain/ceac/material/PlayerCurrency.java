package net.centertain.ceac.material;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerCurrency {
    public static final String CURRENCY_TAG = "ceac_currency";

    private PlayerCurrency() {}

    public static double get(Player player) {
        return getPersisted(player).getDouble(CURRENCY_TAG);
    }

    public static void set(Player player, double amount) {
        getPersisted(player).putDouble(CURRENCY_TAG, amount);
    }

    public static void increase(Player player, double amount) {
        set(player, get(player) + amount);
    }

    public static void decrease(Player player, double amount) {
        set(player, get(player) - amount);
    }

    public static void initialize(Player player) {
        CompoundTag persisted = getPersisted(player);
        if (!persisted.contains(CURRENCY_TAG))
            persisted.putDouble(CURRENCY_TAG, 0.0);
    }

    private static CompoundTag getPersisted(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    }
}
