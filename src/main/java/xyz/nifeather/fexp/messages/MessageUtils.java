package xyz.nifeather.fexp.messages;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Configuration.ConfigOption;
import xiamomc.pluginbase.Managers.DependencyManager;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.utilities.NmsRecord;

import java.util.List;
import java.util.function.Function;

public class MessageUtils
{
    private static void setupConfigManager()
    {
        if (pluginDepMgr == null)
            pluginDepMgr = DependencyManager.getInstance(FeatherExperience.namespaceStatic());

        config = pluginDepMgr.get(FConfigManager.class);
    }

    private static DependencyManager pluginDepMgr;
    private static FConfigManager config;

    public static void send(CommandSender sender, String m)
    {
        var context = new FormattableMessage(FeatherExperience.getInstance(), "<content>")
                .resolve("content", locale -> Component.text(m));

        send(sender, context);
    }

    public static void send(CommandSender sender, Component... componentArray)
    {
        var finalComponent = Component.empty();

        for (var subComponent : componentArray)
            finalComponent = finalComponent.append(subComponent);

        Component finalComponent1 = finalComponent;
        var context = new FormattableMessage(FeatherExperience.getInstance(), "<content>")
                .resolve("content", locale -> finalComponent1);

        send(sender, context);
    }

    public static void send(CommandSender sender, FormattableMessage context)
    {
        send(sender, context, List.of());
    }

    public static void send(CommandSender sender, FormattableMessage context, Function<Component, Component> componentModifier)
    {
        send(sender, context, List.of(componentModifier));
    }

    public static void send(CommandSender sender, FormattableMessage context, List<Function<Component, Component>> componentModifiers)
    {
        if (config == null)
            setupConfigManager();

        var rootMessage = new FormattableMessage(FeatherExperience.getInstance(), config.getOrDefault(FConfigOptions.MESSAGE_PREFIX));

        var inputComponent = context.createComponent();
        if (inputComponent.equals(Component.empty()))
            return;

        var outputComponent = rootMessage.resolve("message", context)
                .createComponent(getLocale(sender));

        for (var componentModifier : componentModifiers)
            outputComponent = componentModifier.apply(outputComponent);

        sender.sendMessage(outputComponent);
    }

    @NotNull
    public static String getLocale(Player player)
    {
        if (isSingleLanguage())
            return getServerLocale();

        var nmsLocale = NmsRecord.ofPlayer(player).language;

        //     Yes this is nullable
        return nmsLocale == null ? getServerLocale() : nmsLocale.toLowerCase().replace('-', '_');
    }

    @NotNull
    public static String getLocaleOr(CommandSender sender, @NotNull String defaultValue)
    {
        var locale = getLocale(sender);
        return locale == null ? defaultValue : locale;
    }

    private static void initializeConfigManager()
    {
        if (config != null) return;

        var depMgr = DependencyManager.getInstance(FeatherExperience.namespaceStatic());
        var configMgr = depMgr.get(FConfigManager.class);

        if (configMgr != null)
        {
            configMgr.bind(serverLocale, FConfigOptions.LANGUAGE_CODE);
            //configMgr.bind(singleLanguage, FConfigManager.SINGLE_LANGUAGE);
        }

        config = configMgr;
    }

    private final static Bindable<String> serverLocale = new Bindable<>("zh_cn");
    private final static Bindable<Boolean> singleLanguage = new Bindable<>(true);

    public static String getServerLocale()
    {
        initializeConfigManager();

        return serverLocale.get();
    }

    public static boolean isSingleLanguage()
    {
        initializeConfigManager();

        return singleLanguage.get();
    }

    @Nullable
    public static String getLocale(CommandSender sender)
    {
        if (sender instanceof Player player && !isSingleLanguage())
            return getLocale(player);
        else
            return getServerLocale();
    }
}
