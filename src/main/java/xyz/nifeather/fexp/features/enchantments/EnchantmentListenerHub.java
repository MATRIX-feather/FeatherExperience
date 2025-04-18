package xyz.nifeather.fexp.features.enchantments;

import org.bukkit.event.Listener;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.features.enchantments.listeners.VoidSaveEnchantmentListener;

import java.util.List;

public class EnchantmentListenerHub
{
    private final List<Listener> listeners;

    public EnchantmentListenerHub()
    {
        listeners = List.of(
                new VoidSaveEnchantmentListener()
        );
    }

    public void registerListeners()
    {
        var plugin = FeatherExperience.getInstance();
        listeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }
}
