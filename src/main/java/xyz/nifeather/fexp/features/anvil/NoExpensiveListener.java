package xyz.nifeather.fexp.features.anvil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class NoExpensiveListener implements Listener
{
    @EventHandler
    public void onAnvilEvent(PrepareAnvilEvent event)
    {
        //event.getView().setRepairCost(39);
        //event.getView().bypassEnchantmentLevelRestriction(true);
        event.getView().setMaximumRepairCost(Integer.MAX_VALUE);
    }
}
