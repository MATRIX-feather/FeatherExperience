package xyz.nifeather.fexp.commands;

import io.papermc.paper.command.brigadier.Commands;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Unmodifiable;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.brigadier.IConvertibleBrigadier;
import xyz.nifeather.fexp.commands.brigadier.IHaveFormattableHelp;
import xyz.nifeather.fexp.messages.strings.HelpStrings;

import java.util.Arrays;
import java.util.List;

public class MainPluginCommand extends FPluginObject implements IConvertibleBrigadier
{
    public MainPluginCommand()
    {
        registerRange(new OptionSubCommand(), new ReloadSubCommand(), new HelpSubCommand());
    }

    @Override
    public String name()
    {
        return "fexp";
    }

    @Override
    public boolean register(Commands dispatcher)
    {
        this.registerAs("fexp", dispatcher);
        this.registerAs("fe", dispatcher);

        return true;
    }

    private void registerAs(String name, Commands dispatcher)
    {
        var cmd = Commands.literal(name);

        for (IConvertibleBrigadier child : this.subCommands)
            child.registerAsChild(cmd);

        dispatcher.register(cmd.build());
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return HelpStrings.mmorphDescription();
    }

    @Override
    public List<FormattableMessage> getNotes()
    {
        return List.of();
    }

    private final List<IConvertibleBrigadier> subCommands = new ObjectArrayList<>();

    @Override
    public @Unmodifiable List<? extends IHaveFormattableHelp> children()
    {
        return new ObjectArrayList<>(subCommands);
    }

    public boolean register(IConvertibleBrigadier cmd)
    {
        subCommands.add(cmd);
        return true;
    }

    public boolean registerRange(List<IConvertibleBrigadier> cmdList)
    {
        var allSuccess = true;

        for (IConvertibleBrigadier cmd : cmdList)
        {
            allSuccess = register(cmd) && allSuccess;
        }

        return allSuccess;
    }

    public boolean registerRange(IConvertibleBrigadier... cmdArray)
    {
        return registerRange(Arrays.stream(cmdArray).toList());
    }
}
