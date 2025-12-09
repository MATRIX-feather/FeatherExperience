package xyz.nifeather.fexp.commands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.brigadier.IConvertibleBrigadier;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.features.pvp.PvPListener;
import xyz.nifeather.fexp.features.pvp.PvPStatus;
import xyz.nifeather.fexp.messages.MessageUtils;
import xyz.nifeather.fexp.messages.strings.PVPStrings;

public class MainTogglePvPCommand extends FPluginObject implements IConvertibleBrigadier
{
    @Resolved(shouldSolveImmediately = true)
    private PvPListener pvpListener;

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    public MainTogglePvPCommand()
    {
        config.bind(toggleAllowed, FConfigOptions.PVP_TOGGLE_ENABLED);
    }

    @Override
    public String name()
    {
        return "fpvp";
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return null;
    }

    @Override
    public boolean register(Commands dispatcher)
    {
        dispatcher.register(
                Commands.literal(name())
                        .executes(this::execute)
                        .build()
        );

        return IConvertibleBrigadier.super.register(dispatcher);
    }


    private final Bindable<Boolean> toggleAllowed = new Bindable<>(false);

    private int execute(CommandContext<CommandSourceStack> context)
    {
        if (!toggleAllowed.get()) return 0;

        var sender = context.getSource().getSender();

        if (!(sender instanceof Player player))
        {
            sender.sendMessage(Component.text("只有玩家可以执行此指令"));
            return 0;
        }

        var result = pvpListener.toggleFor(player);
        if (result == PvPStatus.ENABLED)
            MessageUtils.send(player, PVPStrings.enabledPVP());
        else
            MessageUtils.send(player, PVPStrings.disabledPVP());

        return 1;
    }
}
