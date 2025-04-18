package xyz.nifeather.fexp.features.enchantments.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.features.enchantments.impl.VoidSaveEnchantment;

import java.util.Random;

public class VoidSaveEnchantmentListener extends FPluginObject implements Listener
{
    private final Enchantment ench = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
            .getOrThrow(VoidSaveEnchantment.voidProtectionEnchantmentKey);

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        var entity = event.getEntity();
        var stack = entity.getItemStack();

        if (!stack.getEnchantments().containsKey(ench))
            return;

        entity.setMetadata("nifeather:voidsave_aware", new FixedMetadataValue(plugin, true));
        entity.setMetadata("nifeather:voidsave_activate_position", new FixedMetadataValue(plugin, entity.getLocation()));

        this.scheduleOn(entity, () -> updateOnItem(entity));
    }

    private void updateOnItem(Item itemEntity)
    {
        if (updateItem(itemEntity))
            this.scheduleOn(itemEntity, () -> updateOnItem(itemEntity));
    }

    private boolean updateItem(Item itemEntity)
    {
        if (!itemEntity.isValid()) return false;

        // 如果三叉戟在世界高度里，则计划下一轮更新
        if (itemEntity.getLocation().getY() > itemEntity.getWorld().getMinHeight())
            return true;

        var metadata = itemEntity.getMetadata("nifeather:voidsave_activate_position");
        if (metadata.isEmpty())
        {
            logger.warn("No position metadata found for void save enchantment on item " + itemEntity + "!");
            return false;
        }

        var val = metadata.getFirst();
        if (!(val.value() instanceof Location lastValidLocation))
        {
            logger.warn("Invalid position metadata found for void save enchantment on item " + itemEntity + "!");
            return false;
        }

        // 如果起始位置已经在最低高度下面，则不要处理
        if (lastValidLocation.getY() < itemEntity.getWorld().getMinHeight())
            return false;

        itemEntity.teleportAsync(itemEntity.getLocation().add(0, 6000, 0)).thenRun(() ->
        {
            itemEntity.teleportAsync(lastValidLocation).thenRun(() ->
            {
                itemEntity.getItemStack().editMeta(meta -> meta.removeEnchant(ench));
                itemEntity.setItemStack(itemEntity.getItemStack());
                itemEntity.setPickupDelay(0);

                var random = new Random();
                itemEntity.setVelocity(new Vector(
                        random.nextDouble(-0.05d, 0.05d),
                        0.1d,
                        random.nextDouble(-0.05d, 0.05d)
                ));

                itemEntity.getWorld().playSound(itemEntity.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            });
        });

        return false;
    }
}
