package net.solostudio.vaultcher.item;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public interface ItemFactory {
    static ItemFactory create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    static ItemFactory create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage, byte data) {
        return new ItemBuilder(material, count, damage, data);
    }

    static ItemFactory create(ItemStack item) {
        return new ItemBuilder(item);
    }

    ItemFactory setType(@NotNull Material material);

    ItemFactory setCount(int newCount);

    ItemFactory setName(@NotNull String name);

    ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level);

    default ItemFactory addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);

        return this;
    }

    ItemBuilder addLore(@NotNull String... lores);

    ItemFactory setUnbreakable();

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays
                .stream(flags)
                .forEach(this::addFlag);
    }

    default ItemFactory setLore(@NotNull String... lores) {
        Arrays
                .stream(lores)
                .forEach(this::addLore);
        return this;
    }

    ItemFactory removeLore(int line);

    ItemStack finish();

    boolean isFinished();

    static ItemStack createItemFromString(@NotNull String path) {
        ConfigurationSection section = Vaultcher.getInstance().getConfiguration().getSection(path);

        Material material = Material.valueOf(Objects.requireNonNull(section).getString("material"));
        int amount = section.getInt("amount", 1);
        String name = section.getString("name");
        String[] loreArray = section.getStringList("lore").toArray(new String[0]);

        IntStream.range(0, loreArray.length).forEach(index -> loreArray[index] = MessageProcessor.process(loreArray[index]));

        return ItemFactory.create(material, amount)
                .setName(Objects.requireNonNull(name))
                .addLore(loreArray)
                .finish();
    }
}
