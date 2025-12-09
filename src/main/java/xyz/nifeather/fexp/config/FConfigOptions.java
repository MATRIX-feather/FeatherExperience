package xyz.nifeather.fexp.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.ConfigOption;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FConfigOptions
{
    public static final ConfigOption<String> MESSAGE_PREFIX = new ConfigOption<>(ConfigNode.create().append("message_pattern"), String.class, "[FExp] <message>");
    public static final ConfigOption<String> LANGUAGE_CODE = new ConfigOption<>(ConfigNode.create().append("language"), String.class, "en_us");
    public static final ConfigOption<Boolean> SINGLE_LANGUAGE = new ConfigOption<>(ConfigNode.create().append("single_language"), Boolean.class, false);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_CORAL = new ConfigOption<>(featureBonemealNode().append("coral"), Boolean.class, true);
    public static final ConfigOption<Boolean> CORAL_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("coral_allow_dispenser"), Boolean.class, true);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_FLOWER = new ConfigOption<>(featureBonemealNode().append("flower"), Boolean.class, true);
    public static final ConfigOption<Boolean> FLOWER_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("flower_allow_dispenser"), Boolean.class, true);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_SUGARCANE = new ConfigOption<>(featureBonemealNode().append("sugarcane"), Boolean.class, true);
    public static final ConfigOption<Boolean> SUGARCANE_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("sugarcane_allow_dispenser"), Boolean.class, true);

    public static final ConfigOption<Boolean> FEAT_OPEN_SHULKERBOX = new ConfigOption<>(featureNode().append("shulkerbox").append("open"), Boolean.class, true);
    public static final ConfigOption<Integer> SHULKERBOX_OPEN_DELAY = new ConfigOption<>(featureNode().append("shulkerbox").append("wkar_open_delay"), Integer.class, 40);

    public static final ConfigOption<Boolean> FEAT_DEEPSLATE_FARM = new ConfigOption<>(featureNode().append("deepslate").append("farm"), Boolean.class, true);

    public static final ConfigOption<Boolean> TRIDENT = new ConfigOption<>(featureNode().append("save_tridents_from_void").append("enabled"), Boolean.class, true);

    public static final ConfigOption<Boolean> VILLAGER_EGG = new ConfigOption<>(featureNode().append("mob_eggs").append("enabled"), Boolean.class, true);
    public static final ConfigOption<List<String>> EGG_DISABLED_WORLDS = ConfigOption.<List<String>>builder()
            .node(featureNode().append("mob_eggs").append("disabled_worlds"))
            .defaultValue(new ArrayList<>(List.of("disabled_world1", "disabled_world2")))
            .build();
    public static final ConfigOption<List<String>> EGG_DISABLED_MOBS = ConfigOption.<List<String>>builder()
            .node(featureNode().append("mob_eggs").append("disabled_mobs"))
            .defaultValue(new ArrayList<>(List.of("minecraft:disabled_1", "minecraft:disabled_2")))
            .build();
    public static final ConfigOption<List<String>> EGG_WHITELIST = ConfigOption.<List<String>>builder()
            .node(featureNode().append("mob_eggs").append("mob_whitelist"))
            .defaultValue(new ArrayList<>())
            .build();

    public static final ConfigOption<Boolean> PVP_TOGGLE_ENABLED = new ConfigOption<>(featureNode().append("pvp").append("enabled"), Boolean.class, true);
    public static final ConfigOption<String> PVP_ENABLED_MESSAGE = new ConfigOption<>(featureNode().append("pvp").append("enabled_message"), String.class, "<color:#e0f2f1>PVP已启用</color>");
    public static final ConfigOption<String> PVP_DISABLED_MESSAGE = new ConfigOption<>(featureNode().append("pvp").append("disabled_message"), String.class, "<color:#e0f2f1>PVP已禁用</color>");

    public static final ConfigOption<Boolean> ALLOW_TORCH_CROSSBOW = new ConfigOption<>(featureNode().append("torch_crossbow").append("enabled"), Boolean.class, true);

    public static final ConfigOption<Boolean> NO_EXP_COOLDOWN = new ConfigOption<>(featureNode().append("no_exp_cooldown").append("enabled"), Boolean.class, false);

    public static final ConfigOption<Boolean> ALLOW_FIRE_ASPECT_LIT_CANDLES = new ConfigOption<>(featureNode().append("fire_aspect_lit_candles"), Boolean.class, true);

    public static final ConfigOption<Integer> VERSION = new ConfigOption<>(ConfigNode.create().append("version"), Integer.class, 0);

    public static List<ConfigOption<?>> values()
    {
        var fields = Arrays.stream(FConfigOptions.class.getFields())
                .filter(f -> f.getType().equals(ConfigOption.class) && Modifier.isStatic(f.getModifiers()))
                .toList();

        var list = fields.stream().map(f ->
        {
            try
            {
                return (ConfigOption<?>) f.get(null);
            }
            catch (Throwable t)
            {
                throw new RuntimeException(t);
            }
        }).toList();

        return new ObjectArrayList<>(list);
    }

    private static ConfigNode featureNode()
    {
        return ConfigNode.create().append("features");
    }

    private static ConfigNode featureBonemealNode()
    {
        return featureNode().append("bone_meal");
    }
}
