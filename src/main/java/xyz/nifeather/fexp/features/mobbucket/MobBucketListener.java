package xyz.nifeather.fexp.features.mobbucket;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.utilities.ItemUtils;
import xyz.nifeather.fexp.utilities.NmsRecord;

import java.util.List;
import java.util.UUID;

public class MobBucketListener extends FPluginObject implements Listener
{
    private final MobBucketHandler bucketHandler = new MobBucketHandler();

    public static boolean townyInstalled = false;

    private final List<UUID> blockedUUIDs = new ObjectArrayList<>();

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e)
    {
        if (e.getEntity().getType() != EntityType.EGG) return;

        var owner = e.getEntity().getOwnerUniqueId();
        if (owner == null) return;

        if (blockedUUIDs.removeIf(uuid -> uuid.equals(owner)))
            e.setCancelled(true);
    }

    @EventHandler
    public void onVillager(PlayerInteractAtEntityEvent e)
    {
        this.onEntityInteract(e);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e)
    {
        this.onEntityInteract(e);
    }

    private final Component eggBlockMessage = MiniMessage.miniMessage().deserialize("<red>不能对着实体使用此生成蛋</red>");

    private void onEntityInteract(PlayerInteractEntityEvent e)
    {
        var item = e.getPlayer().getEquipment().getItem(e.getHand());

        var player = e.getPlayer();
        if (!player.hasPermission(CommonPermissions.mobEggUse))
            return;

        // Block RC at entity
        if (ItemUtils.isEggMarked(item))
        {
            player.sendActionBar(eggBlockMessage);
            e.setCancelled(true);
            return;
        }

        if (townyInstalled)
        {
            if (!PlayerCacheUtil.getCachePermission(player, e.getRightClicked().getLocation(), item.getType(), TownyPermission.ActionType.DESTROY))
                return;
        }

        if (bucketHandler.onInteract(item, e.getRightClicked(), e.getPlayer()))
        {
            var nmsPlayer = NmsRecord.ofPlayer(e.getPlayer());

            if (nmsPlayer.gameMode.isSurvival())
                item.setAmount(item.getAmount() - 1);

            e.setCancelled(true);

            var playerUUID = e.getPlayer().getUniqueId();
            if (blockedUUIDs.stream().noneMatch(uuid -> uuid.equals(playerUUID)))
                blockedUUIDs.add(playerUUID);
        }
    }
}
