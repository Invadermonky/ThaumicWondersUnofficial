package com.verdantartifice.thaumicwonders.client.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.config.KeyBindings;
import com.verdantartifice.thaumicwonders.common.entities.EntityFlyingCarpet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ThaumicWonders.MODID)
public class InputEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof EntityFlyingCarpet) {
                ((EntityFlyingCarpet) ridingEntity).updateInputs(
                        KeyBindings.carpetForwardKey.isKeyDown(),
                        KeyBindings.carpetBackwardKey.isKeyDown()
                );
            }
        }
    }
}
