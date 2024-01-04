package xiamomc.bonemeal.features.shulker;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xiamomc.bonemeal.XiaMoExperience;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ShulkerManager
{
    private final Logger logger = XiaMoExperience.getInstance().getSLF4JLogger();

    public static class OpenMeta
    {
        private final Player bindingPlayer;
        private final ItemStack stackCopy;
        private final int slot;

        public OpenMeta(Player player, ItemStack stackCopy, int slot)
        {
            this.bindingPlayer = player;
            this.slot = slot;
            this.stackCopy = stackCopy;
        }

        public Player player()
        {
            return bindingPlayer;
        }

        public ItemStack stack()
        {
            return stackCopy;
        }

        public int slot()
        {
            return slot;
        }
    }

    private final Map<OpenMeta, Inventory> playerInventoryMap = new Object2ObjectArrayMap<>();

    @Nullable
    public Map.Entry<OpenMeta, Inventory> getPlayerEntryMeta(Player player)
    {
        return playerInventoryMap.entrySet()
                .stream().filter(set -> set.getKey().player() == player)
                .findFirst().orElse(null);
    }

    public boolean openingBox(Player player)
    {
        return getPlayerEntryMeta(player) != null;
    }

    /**
     *
     * @param itemStack
     * @param player
     * @param slot
     * @return 操作是否成功
     */
    public boolean tryOpenBox(ItemStack itemStack, Player player, int slot)
    {
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta))
        {
            logger.warn("ItemMeta is not a BlockStateMeta");
            return false;
        }

        if (!(blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox))
        {
            logger.warn("BlockStats is not a ShulkerBox");
            return false;
        }

        if (getPlayerEntryMeta(player) != null)
            throw new RuntimeException("Already opened another shulker box!");

        var inventory = shulkerBox.getInventory();

        try
        {
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            player.openInventory(inventory);
        }
        catch (Throwable t)
        {
            logger.error("Unable to open shulker box for player " + player.getName());
            return false;
        }

        var itemCopy = new ItemStack(itemStack);

        playerInventoryMap.put(new OpenMeta(player, itemCopy, slot), inventory);

        player.playSound(player, Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);

        return true;
    }

    public void closeBox(Player player)
    {
        var entrySet = getPlayerEntryMeta(player);

        if (entrySet == null) return;

        var currentInv = player.getOpenInventory().getTopInventory();
        if (currentInv != entrySet.getValue())
        {
            //如果当前界面上测不是打开的盒子，则不要关闭界面
            //logger.warn("Current opened inventory is not box, not closing inventory...");
        }
        else
        {
            //关闭此Inventory
            player.closeInventory();
        }

        //todo: 只进行一次remove
        playerInventoryMap.remove(entrySet.getKey());

        var openMeta = entrySet.getKey();

        var atomicItem = new AtomicReference<ItemStack>(null);

        //先获取特定槽位的物品
        var itemFromSlot = player.getInventory().getItem(openMeta.slot);

        if (openMeta.stackCopy.equals(itemFromSlot))
        {
            atomicItem.set(itemFromSlot);
        }
        else
        {
            player.getInventory().forEach(i ->
            {
                if (openMeta.stackCopy.equals(i))
                    atomicItem.set(i);
            });
        }

        var boxItem = atomicItem.get();

        if (boxItem == null)
        {
            logger.error("BINDING STACK DISAPPEARED FROM PLAYER '" + player.getName() + "'!");
            logger.error("THE CONTENT OF THE BOX IS NOT SAVED, A DUPE GLITCH MAY OCCUR!");
            return;
        }

        var meta = (BlockStateMeta) boxItem.getItemMeta();
        var shulker = (ShulkerBox) meta.getBlockState();

        shulker.getInventory().setContents(entrySet.getValue().getContents());
        meta.setBlockState(shulker);
        boxItem.setItemMeta(meta);

        player.playSound(player, Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
    }
}