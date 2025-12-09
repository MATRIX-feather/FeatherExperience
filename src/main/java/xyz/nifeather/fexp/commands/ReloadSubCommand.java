package xyz.nifeather.fexp.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.pluginbase.Messages.MessageStore;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.commands.brigadier.BrigadierCommand;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.messages.MessageUtils;
import xyz.nifeather.fexp.messages.TranslateManager;
import xyz.nifeather.fexp.messages.strings.HelpStrings;

public class ReloadSubCommand extends BrigadierCommand
{
    @Override
    public @NotNull String name()
    {
        return "reload";
    }

    @Override
    public @Nullable String getPermissionRequirement()
    {
        return CommonPermissions.reloadCommand;
    }

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(Commands.literal(name())
                .executes(this::execute));

        super.registerAsChild(parentBuilder);
    }

    private int execute(CommandContext<CommandSourceStack> context)
    {
        config.reload();
        TranslateManager.instance().reload();

        var sender = context.getSource().getSender();
        MessageUtils.send(sender, "Reload complete.");

        return 1;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return HelpStrings.reloadDescription();
    }
}
