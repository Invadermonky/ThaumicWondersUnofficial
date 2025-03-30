package com.verdantartifice.thaumicwonders.client.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.gui.HudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ThaumicWonders.MODID)
public class RenderEvents {
    private static HudManager hudManager = new HudManager();
    
    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
            if (entity instanceof EntityPlayer) {
                hudManager.renderHuds(FMLClientHandler.instance().getClient(), event.renderTickTime, (EntityPlayer)entity, System.currentTimeMillis());
            }
        }
    }
}
