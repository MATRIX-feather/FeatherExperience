package xyz.nifeather.fexp.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.utilities.PluginAssetUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayingMessageStore
{
    private final String targetLocale;
    public String targetLocale()
    {
        return targetLocale;
    }

    protected final Logger logger;
    protected final FeatherExperience plugin;
    protected final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    // Message Key <-> Value
    protected final Map<String, String> i18nMap = new ConcurrentHashMap<>();

    public OverlayingMessageStore(String targetLocale)
    {
        this.targetLocale = targetLocale;
        plugin = FeatherExperience.getInstance();
        logger = FeatherExperience.getInstance().getSLF4JLogger();
    }

    public Optional<String> lookup(String messageKey)
    {
        return Optional.ofNullable(i18nMap.getOrDefault(messageKey, null));
    }

    public void load()
    {
        loadFromPluginAsset();

        var messagesDirectory = new File(plugin.getDataFolder(), "messages");
        var languageFileOnDisk = new File(messagesDirectory, "%s.json".formatted(targetLocale));

        if (languageFileOnDisk.exists())
            this.loadFromFileSystem(languageFileOnDisk);
        else
            logger.info("Skipping Override File for '%s' because '%s' not exist".formatted(targetLocale, languageFileOnDisk.getAbsolutePath()));
    }

    private void loadFromFileSystem(File i18nFile)
    {
        try
        {
            var str = Files.readString(i18nFile.toPath());
            var fileI18nMap = gson.fromJson(str, new TypeToken<Map<String, String>>(){});
            if (fileI18nMap == null) // empty file, ignore it
                return;

            this.i18nMap.putAll(fileI18nMap);
        }
        catch (Exception e)
        {
            logger.error("Unable to read i18n for language '%s' from file %s on disk".formatted(targetLocale, i18nFile.getName()), e);
        }
    }

    public void clear()
    {
        this.i18nMap.clear();
    }

    public void reload()
    {
        clear();
        load();
    }

    public boolean isEmpty()
    {
        return this.i18nMap.isEmpty();
    }

    private void loadFromPluginAsset()
    {
        var path = PluginAssetUtils.langPath(targetLocale);
        var asset = PluginAssetUtils.getFileStringsOptional(path);
        if (asset.isEmpty())
        {
            //logger.info("Skipping %s from plugin assets because it doesn't exists".formatted(targetLocale));
            return;
        }

        var typeToken = new TypeToken<Map<String, String>>(){}.getType();

        try
        {
            Map<String, String> assetI18n = gson.fromJson(asset.get(), typeToken);
            this.i18nMap.putAll(assetI18n);
        }
        catch (Exception e)
        {
            logger.error("Unable to read i18n for language '%s' from plugin assets".formatted(targetLocale), e);
        }
    }
}
