package xyz.nifeather.fexp.misc.integrations.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.nifeather.fexp.FeatherExperience;

public class TownyHelper
{/*
    public static boolean playerItemUseAllowed(Player player, Location location, boolean defaultVal)
    {
        var plot = TownyAPI.getInstance().getTownBlock(location);

        if (plot == null || !plot.hasTown())
            return defaultVal;

        var asResident = TownyAPI.getInstance().getResident(player);
        if (asResident == null)
            return defaultVal;

        Town plotTown;

        try
        {
            plotTown = plot.getTown();
        }
        catch (Throwable e)
        {
            FeatherExperience.getInstance().getSLF4JLogger().error("This shouldn't happen! (%s)".formatted(e.getMessage()));
            e.printStackTrace();

            return false;
        }
    }
    */
}
