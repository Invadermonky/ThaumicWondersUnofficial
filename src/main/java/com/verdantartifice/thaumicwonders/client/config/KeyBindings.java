package com.verdantartifice.thaumicwonders.client.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityFlyingCarpet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBindings {
    private static final String KEY_CATEGORY = "key.categories." + ThaumicWonders.MODID;
    public static KeyBinding carpetForwardKey;
    public static KeyBinding carpetBackwardKey;

    private static IKeyConflictContext flyingCarpetContext = new IKeyConflictContext() {
        @Override
        public boolean isActive() {
            EntityPlayer player = Minecraft.getMinecraft().player;
            return player != null && player.getRidingEntity() instanceof EntityFlyingCarpet;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return other.isActive() && this.isActive();
        }
    };

    public static void init() {
        carpetForwardKey = new KeyBinding("key.carpet_forward", flyingCarpetContext, Keyboard.KEY_W, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(carpetForwardKey);

        carpetBackwardKey = new KeyBinding("key.carpet_backward", flyingCarpetContext, Keyboard.KEY_S, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(carpetBackwardKey);
    }
}
