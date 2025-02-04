package xyz.nifeather.fexp.features.ac.packetlisteners;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ChargedProjectiles;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.sql.Wrapper;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EquipListener extends AbstractListener
{
    private final List<Enchantment> fakeEnchantments = List.of(
            Enchantment.builder()
                    .type(EnchantmentTypes.SHARPNESS)
                    .level(1)
                    .build()
    );

    private final Bindable<Boolean> enableWhitelist = new Bindable<>(true);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableWhitelist, FConfigOptions.COMPONENT_WHITELIST);
    }

    @Override
    protected void onPacketSending(PacketSendEvent event)
    {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        if (!enableWhitelist.get()) return;

        try
        {
            handle(event);
        }
        catch (Throwable t)
        {
            logger.warn("Can't handle equipment packet: " + t.getMessage());
            t.printStackTrace();

            logger.warn("Something may gone wrong after this error, be careful!");
        }
    }

    private void handle(PacketSendEvent event)
    {
        WrapperPlayServerEntityEquipment wrapper;

        try
        {
            wrapper = new WrapperPlayServerEntityEquipment(event);
        }
        catch (Throwable t)
        {
            logger.warn("Can't read WrapperPlayServerEntityEquipment from protocol, this is a packetevent bug!");
            return;
        }

        var user = event.getUser();
        var userClientVersion = user.getClientVersion();

        wrapper.getEquipment().forEach(equipment ->
        {
            var item = equipment.getItem();

            if (item.isEnchanted(userClientVersion))
                item.setEnchantments(fakeEnchantments, userClientVersion);

            item.setAmount(1);

            item.setDamageValue(1);
        });
    }
}
