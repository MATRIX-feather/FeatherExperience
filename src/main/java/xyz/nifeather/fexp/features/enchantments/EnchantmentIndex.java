package xyz.nifeather.fexp.features.enchantments;

import org.jetbrains.annotations.Unmodifiable;
import xyz.nifeather.fexp.features.enchantments.impl.VoidSaveEnchantment;

import java.util.Collection;
import java.util.List;

public class EnchantmentIndex
{
    public static final EnchantmentIndex INSTANCE = new EnchantmentIndex();

    private final List<IEnchantment> enchantments;

    public EnchantmentIndex()
    {
        enchantments = List.of(
                new VoidSaveEnchantment()
        );
    }

    @Unmodifiable
    public Collection<IEnchantment> getEnchantments()
    {
        return enchantments;
    }
}
