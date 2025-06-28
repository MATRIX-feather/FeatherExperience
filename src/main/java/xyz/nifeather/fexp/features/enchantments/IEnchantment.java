package xyz.nifeather.fexp.features.enchantments;

import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;

public interface IEnchantment
{
    void onItemTagRegister(ReloadableRegistrarEvent<PostFlattenTagRegistrar<ItemType>> event);
    void onEnchantmentTagRegister(ReloadableRegistrarEvent<PostFlattenTagRegistrar<Enchantment>> event);
    void onEnchantmentRegister(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event);
}
