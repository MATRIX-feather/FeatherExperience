package xyz.nifeather.fexp;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.KeyPattern;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Messages.MessageStore;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.commands.FCommandHelper;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.ac.eventlisteners.BeaconListener;
import xyz.nifeather.fexp.features.anvil.NoExpensiveListener;
import xyz.nifeather.fexp.features.bonemeal.BonemealListener;
import xyz.nifeather.fexp.features.deepslateFarm.DeepslateListener;
import xyz.nifeather.fexp.features.enchantments.EnchantmentListenerHub;
import xyz.nifeather.fexp.features.mobbucket.MobBucketListener;
import xyz.nifeather.fexp.features.pvp.PvPListener;
import xyz.nifeather.fexp.features.shulker.ShulkerListener;
import xyz.nifeather.fexp.features.trident.TridentSaverListener;
import xyz.nifeather.fexp.features.xpCooldown.ExpCooldownListener;
import xyz.nifeather.fexp.messages.TranslateManager;
import xyz.nifeather.fexp.misc.integrations.coreprotect.CoreProtectIntegration;

public final class FeatherExperience extends XiaMoJavaPlugin
{
    public static FeatherExperience getInstance()
    {
        return instance;
    }

    private static FeatherExperience instance;

    private final static String namespace = "fexp";

    public static String namespaceStatic()
    {
        return namespace;
    }

    @Override
    public String getNamespace()
    {
        return namespace;
    }

    public FeatherExperience()
    {
        instance = this;
    }

    private Metrics metrics;
    private PvPListener pvpListener;

    private static boolean enablePacketEvents = false;

    @Override
    public void onLoad()
    {
        super.onLoad();

        try
        {
            PacketEvents.getAPI();
        }
        catch (Throwable t)
        {
            logger.info("No packetevents detected, won't enabling related features.");
            enablePacketEvents = false;
        }

        if (enablePacketEvents)
        {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
            PacketEvents.getAPI().getSettings().reEncodeByDefault(true).checkForUpdates(false);

            PacketEvents.getAPI().load();
        }
    }

    @Override
    public void enable()
    {
        super.enable();

        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BonemealListener(), this);
        pluginManager.registerEvents(new DeepslateListener(), this);
        pluginManager.registerEvents(shulkerListener = new ShulkerListener(), this);
        pluginManager.registerEvents(new TridentSaverListener(), this);
        pluginManager.registerEvents(new MobBucketListener(), this);
        pluginManager.registerEvents(new BeaconListener(), this);
        pluginManager.registerEvents(new NoExpensiveListener(), this);

        new EnchantmentListenerHub().registerListeners();

        pluginManager.registerEvents(new ExpCooldownListener(), this);

        pvpListener = new PvPListener();
        pluginManager.registerEvents(pvpListener, this);
        dependencyManager.cache(pvpListener);

        softDeps.setHandle("CoreProtect", pl ->
                dependencyManager.cache(new CoreProtectIntegration()), true);

        softDeps.setHandle("Towny", pl -> MobBucketListener.townyInstalled = true, true);

        var config = new FConfigManager(this);
        config.reload();
        dependencyManager.cache(config);
        dependencyManager.cacheAs(MessageStore.class, TranslateManager.instance().asFrameworkMessageStore());

        var cmdHelper = new FCommandHelper();
        dependencyManager.cache(cmdHelper);

        // Commands
        var lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event ->
                cmdHelper.register(event));

        this.metrics = new Metrics(this, 21211);

        if (enablePacketEvents)
            PacketEvents.getAPI().init();
    }

    private ShulkerListener shulkerListener;

    @Override
    public void disable()
    {
        super.disable();

        try
        {
            if (shulkerListener != null)
                shulkerListener.onDisable();

            if (metrics != null)
                metrics.shutdown();

            if (pvpListener != null)
                pvpListener.dispose();

            if (enablePacketEvents)
                PacketEvents.getAPI().terminate();
        }
        catch (Throwable t)
        {
            logger.warn("Error occurred while disabling: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void startMainLoop(Runnable r)
    {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, o -> r.run(), 1, 1);
    }

    @Override
    public void runAsync(Runnable r)
    {
        Bukkit.getAsyncScheduler().runNow(this, o -> r.run());
    }
}
