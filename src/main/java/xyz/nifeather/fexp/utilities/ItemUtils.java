package xyz.nifeather.fexp.utilities;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemUtils
{
    public static final String MOBEGG_IDENTIFIER = "fexp:is_mob_egg";

    public static ItemStack markEgg(ItemStack nms)
    {
        var customData = nms.getComponents().get(DataComponents.CUSTOM_DATA);
        if (customData == null) customData = CustomData.EMPTY;

        customData = customData.update(tag -> tag.putBoolean(MOBEGG_IDENTIFIER, true));
        nms.set(DataComponents.CUSTOM_DATA, customData);

        return nms;
    }

    public static boolean isEggMarked(org.bukkit.inventory.ItemStack stack)
    {
        var nms = net.minecraft.world.item.ItemStack.fromBukkitCopy(stack);
        var customData = nms.getComponents().get(DataComponents.CUSTOM_DATA);

        if (customData == null || !customData.contains(MOBEGG_IDENTIFIER)) return false;

        return customData.copyTag().getBoolean(MOBEGG_IDENTIFIER);
    }
}
