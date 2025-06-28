package xyz.nifeather.fexp.features.enchantments.impl;

import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.ItemTypeKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import xyz.nifeather.fexp.features.enchantments.IEnchantment;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class VoidSaveEnchantment implements IEnchantment
{
    public static final TagKey<ItemType> voidProtectionItemKey = TagKey.create(RegistryKey.ITEM, Key.key("nifeather", "void_saving_items"));
    public static final TypedKey<Enchantment> voidProtectionEnchantmentKey = EnchantmentKeys.create(Key.key("nifeather", "void_saving"));

    @Override
    public void onItemTagRegister(ReloadableRegistrarEvent<PostFlattenTagRegistrar<ItemType>> event)
    {
        var registry = event.registrar();

        // 为【虚空排斥】提前设定支持的物品
        var voidProtItems = new ObjectArrayList<TypedKey<ItemType>>();
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.SWORDS));
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.AXES));
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.PICKAXES));
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.SHOVELS));
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.HOES));
        voidProtItems.addAll(registry.getTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR));

        voidProtItems.add(ItemTypeKeys.TRIDENT);
        voidProtItems.add(ItemTypeKeys.CROSSBOW);
        voidProtItems.add(ItemTypeKeys.BOW);

        registry.setTag(voidProtectionItemKey, voidProtItems);
    }

    @Override
    public void onEnchantmentTagRegister(ReloadableRegistrarEvent<PostFlattenTagRegistrar<Enchantment>> event)
    {
        var registry = event.registrar();

        registry.addToTag(EnchantmentTagKeys.TRADEABLE, List.of(
                voidProtectionEnchantmentKey
        ));
    }

    @Override
    public void onEnchantmentRegister(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event)
    {
        var registry = event.registry();

        registry.register(
                voidProtectionEnchantmentKey,
                builder ->
                {
                    builder.description(Component.translatable("enchantment.nifeather.void_protection", "虚空排斥"))
                            .maxLevel(1)
                            .supportedItems(event.getOrCreateTag(voidProtectionItemKey))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(0)
                            .weight(5)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(15, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 1));
                }
        );
    }
}
