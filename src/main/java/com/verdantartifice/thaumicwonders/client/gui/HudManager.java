package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.entities.EntityFlyingCarpet;
import com.verdantartifice.thaumicwonders.common.items.tools.ItemPrimalDestroyer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.casters.ICaster;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.tools.ItemSanityChecker;
import thaumcraft.common.items.tools.ItemThaumometer;

@SideOnly(Side.CLIENT)
public class HudManager {
    private static final ResourceLocation HUD = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/hud.png");
    
    public void renderHuds(Minecraft mc, float renderTickTime, EntityPlayer player, long time) {
        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glClear(GL11.GL_ACCUM);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        if (mc.inGameHasFocus && Minecraft.isGuiEnabled()) {
            mc.renderEngine.bindTexture(HUD);
            
            ItemStack mhStack = player.getHeldItemMainhand();
            Item mhStackItem = null;
            if (mhStack != null && !mhStack.isEmpty()) {
                mhStackItem = mhStack.getItem();
            }
            ItemStack ohStack = player.getHeldItemOffhand();
            Item ohStackItem = null;
            if (ohStack != null && !ohStack.isEmpty()) {
                ohStackItem = ohStack.getItem();
            }
            
            // Determine Y offset for GUI if other GUIs are being shown
            int yStart = 0;
            if (mhStackItem instanceof ICaster || ohStackItem instanceof ICaster) {
                if (!ModConfig.CONFIG_GRAPHICS.dialBottom) {
                    yStart += 33;
                }
            }
            if (mhStackItem instanceof ItemThaumometer || ohStackItem instanceof ItemThaumometer) {
                yStart += 80;
            }
            if (mhStackItem instanceof ItemSanityChecker || ohStackItem instanceof ItemSanityChecker) {
                yStart += 75;
            }
            
            // Show the Primal Destroyer GUI if applicable
            if (mhStackItem instanceof ItemPrimalDestroyer) {
                this.renderPrimalDestroyerHud(mc, renderTickTime, player, mhStack, time, yStart);
                yStart += 77;
            }
            if (ohStackItem instanceof ItemPrimalDestroyer) {
                this.renderPrimalDestroyerHud(mc, renderTickTime, player, ohStack, time, yStart);
                yStart += 77;
            }
            
            // Show the carpet GUI if applicable
            Entity ridingEntity = player.getRidingEntity();
            if (ridingEntity instanceof EntityFlyingCarpet) {
                this.renderCarpetHud(mc, renderTickTime, player, (EntityFlyingCarpet)ridingEntity, time, yStart);
                yStart += 77;
            }
        }
        
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    
    private void renderPrimalDestroyerHud(Minecraft mc, float partialTicks, EntityPlayer player, ItemStack itemStack, long time, int yStart) {
        // Draw background bars
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(0.0D, yStart, 0.0D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.drawTexturedQuad(1.0F, 1.0F, 152.0F, 0.0F, 20.0F, 78.0F, -90.0D);
        
        int hunger = itemStack.hasTagCompound() ? itemStack.getTagCompound().getInteger("hunger") : 0;
        int gap = (int)(((float)ItemPrimalDestroyer.MAX_HUNGER - hunger) / (float)ItemPrimalDestroyer.MAX_HUNGER * 48.0F);
        
        // Draw hunger level
        if (hunger > 0) {
            GL11.glPushMatrix();
            GL11.glColor4f(0.5F, 0.0F, 0.5F, 1.0F);
            UtilsFX.drawTexturedQuad(7.0F, 23 + gap, 200.0F, gap, 8.0F, 48.0F, -90.0D);
            GL11.glPopMatrix();
        }
        
        // Draw foreground meter
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.drawTexturedQuad(1.0F, 1.0F, 128.0F, 0.0F, 20.0F, 78.0F, -90.0D);
        GL11.glPopMatrix();
     
        // Pop initially pushed matrix
        GL11.glPopMatrix();
    }

    private void renderCarpetHud(Minecraft mc, float partialTicks, EntityPlayer player, EntityFlyingCarpet carpet, long time, int yStart) {
        // Draw background bars
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(0.0D, yStart, 0.0D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.drawTexturedQuad(1.0F, 1.0F, 152.0F, 0.0F, 20.0F, 78.0F, -90.0D);
        
        int vis = carpet.getVisCharge();
        int capacity =  ConfigHandlerTW.flying_carpet.visCapacity;
        int gap = (int)(((float)capacity - vis) / (float)capacity * 48.0F);
        
        // Draw vis level
        if (vis > 0) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 0.5F, 1.0F, 1.0F);
            UtilsFX.drawTexturedQuad(7.0F, 23 + gap, 200.0F, gap, 8.0F, 48.0F, -90.0D);
            GL11.glPopMatrix();
        }
        
        // Draw foreground meter
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.drawTexturedQuad(1.0F, 1.0F, 176.0F, 0.0F, 20.0F, 78.0F, -90.0D);
        GL11.glPopMatrix();
     
        // Pop initially pushed matrix
        GL11.glPopMatrix();
    }
}
