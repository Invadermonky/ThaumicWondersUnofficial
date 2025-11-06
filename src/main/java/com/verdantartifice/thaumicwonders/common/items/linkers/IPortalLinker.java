package com.verdantartifice.thaumicwonders.common.items.linkers;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import net.minecraft.item.ItemStack;

public interface IPortalLinker {
    boolean isLinkValid(ItemStack linker, TilePortalAnchor anchor);

    void activatePortal(ItemStack linker, TilePortalAnchor anchor);

    boolean removeLinkerOnActivation(ItemStack linker, TilePortalAnchor anchor);

    void onUpdate(ItemStack linker, TilePortalAnchor anchor);

    String getFailureMessage();
}
