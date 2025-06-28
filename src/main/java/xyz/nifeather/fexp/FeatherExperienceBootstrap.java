package xyz.nifeather.fexp;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.fexp.features.enchantments.EnchantmentIndex;

@SuppressWarnings("UnstableApiUsage")
public class FeatherExperienceBootstrap implements PluginBootstrap
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatherExperienceBootstrap.class);

    @Override
    public void bootstrap(BootstrapContext bootstrapContext)
    {
        var lifeCycleManager = bootstrapContext.getLifecycleManager();

        LOGGER.info("Running bootstrap...");

        lifeCycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ITEM).newHandler(this::onItemTagsPostFlatten));
        lifeCycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT).newHandler(this::onEnchantmentTagsPostFlatten));
        lifeCycleManager.registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(this::onEnchantmentFreeze));

        LOGGER.info("Done running bootstrap!");
    }

    private void onItemTagsPostFlatten(ReloadableRegistrarEvent<PostFlattenTagRegistrar<ItemType>> event)
    {
        EnchantmentIndex.INSTANCE.getEnchantments().forEach(enchantment -> enchantment.onItemTagRegister(event));
    }

    private void onEnchantmentTagsPostFlatten(ReloadableRegistrarEvent<PostFlattenTagRegistrar<Enchantment>> event)
    {
        EnchantmentIndex.INSTANCE.getEnchantments().forEach(enchantment -> enchantment.onEnchantmentTagRegister(event));
    }

    private void onEnchantmentFreeze(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event)
    {
        EnchantmentIndex.INSTANCE.getEnchantments().forEach(enchantment -> enchantment.onEnchantmentRegister(event));
    }
}
