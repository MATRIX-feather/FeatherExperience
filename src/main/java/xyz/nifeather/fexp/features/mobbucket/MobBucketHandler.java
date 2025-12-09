package xyz.nifeather.fexp.features.mobbucket;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Bindables.BindableList;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.misc.integrations.coreprotect.CoreProtectIntegration;
import xyz.nifeather.fexp.utilities.ItemUtils;

import java.util.List;

public class MobBucketHandler extends FPluginObject
{
    private final Bindable<Boolean> enabled = new Bindable<>(false);
    private BindableList<String> disabledWorlds = new BindableList<>(List.of());
    private BindableList<String> disabledMobs = new BindableList<>(List.of());
    private BindableList<String> whitelist = new BindableList<>(List.of());

    @Initializer
    private void load(FConfigManager configManager)
    {
        configManager.bind(enabled, FConfigOptions.VILLAGER_EGG);

        disabledWorlds = configManager.getBindableList(String.class, FConfigOptions.EGG_DISABLED_WORLDS);
        disabledMobs = configManager.getBindableList(String.class, FConfigOptions.EGG_DISABLED_MOBS);
        whitelist = configManager.getBindableList(String.class, FConfigOptions.EGG_WHITELIST);
    }

    /**
     * Whether this operation operates successfully
     * @param item
     * @param clickedEntity
     * @return
     */
    public boolean onInteract(ItemStack item, @Nullable Entity clickedEntity, Player player)
    {
        if (!enabled.get()) return false;

        if (item.getType() == Material.EGG)
        {
            var success = onEmptyInteract(item, clickedEntity);

            if (success && coreProtectIntegration != null)
                coreProtectIntegration.logInteract(player, clickedEntity.getLocation());

            return success;
        }

        return false;
    }

    @Resolved(allowNull = true)
    @Nullable
    private CoreProtectIntegration coreProtectIntegration;

    /**
     * @param item
     * @param clickedEntity
     * @return Whether this operation operates successfully
     */
    private boolean onEmptyInteract(ItemStack item, @Nullable Entity clickedEntity)
    {
        if (clickedEntity == null) return false;

        if (!clickedEntity.isValid()) return false;

        if (disabledWorlds.stream().anyMatch(s -> s.equalsIgnoreCase(clickedEntity.getWorld().getName())))
            return false;

        // 不允许收集玩家和非LivingEntity的实体
        if (!(clickedEntity instanceof LivingEntity) || clickedEntity instanceof Player)
            return false;

        // 不允许收集Boss
        if (clickedEntity instanceof Boss)
            return false;

        var mobId = clickedEntity.getType().key().asString();

        // 不允许收集黑名单里的生物
        if (disabledMobs.contains(mobId))
            return false;

        // 白名单
        if (!whitelist.isEmpty() && !whitelist.contains(mobId))
            return false;

        var typeName = "%s_SPAWN_EGG".formatted(clickedEntity.getType().name().toUpperCase());
        Material eggType = Material.ALLAY_SPAWN_EGG;

        try
        {
            eggType = Material.valueOf(typeName);
        }
        catch (Throwable t)
        {
            //logger.warn("Can't find egg type for entity type " + clickedEntity.getType());
            //logger.warn("Not activating mob egg feature...");

            return false;
        }

        var newItem = ItemStack.of(eggType, 1);

        var nmsItem = CraftItemStack.asNMSCopy(newItem);

        var nmsEntity = ((CraftEntity)clickedEntity).getHandle();
        var compound = CraftEntitySnapshot.create((CraftEntity) clickedEntity).getData();

        compound.putBoolean("PersistenceRequired", true);

        if (clickedEntity instanceof Ageable ageable && ageable.isAdult())
        {
            compound.putInt("Age", 0);
            compound.putBoolean("IsBaby", false);
        }

        nmsItem.set(DataComponents.ENTITY_DATA, TypedEntityData.of(nmsEntity.getType(), compound));
        newItem = ItemUtils.markEgg(nmsItem).asBukkitCopy();

        clickedEntity.remove();

        var world = clickedEntity.getWorld();
        world.dropItem(clickedEntity.getLocation(), newItem);

        world.playSound(clickedEntity.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        return true;
    }
}
