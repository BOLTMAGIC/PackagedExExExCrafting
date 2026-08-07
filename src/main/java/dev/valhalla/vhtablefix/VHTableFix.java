package dev.valhalla.vhtablefix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraftforge.fml.common.Mod;

/**
 * VH Table Fix - server-side Forge 1.20.1 mod.
 * Raises the vanilla ServerboundContainerClickPacket changed-slots decode limit
 * (128 -> 4096) so 13x13 Extended Crafting Legendary-table crafts don't kick
 * the client with "Value N is larger than limit 128".
 */
@Mod("vhtablefix")
public final class VHTableFix {
    public VHTableFix() {
        LoggerFactory.getLogger("vhtablefix").info("VHTableFix loaded");
    }
}

