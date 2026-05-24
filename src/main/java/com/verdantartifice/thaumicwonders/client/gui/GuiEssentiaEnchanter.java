package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.gui.elements.enchanter.*;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.containers.ContainerEssentiaEnchanter;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.enchanter.EssentiaEnchanterRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketStartEnchanting;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileEssentiaEnchanter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEssentiaEnchanter extends GuiContainer {
    public static final ResourceLocation TEXTURE = ConfigHandlerTW.essentia_enchanter.alternateGui
            ? new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_essentia_enchanter_alt.png")
            : new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_essentia_enchanter.png");
    public static final int BUTTON_START_ENCH;
    private static final int BUTTON_PREV_ENCH_PAGE;
    private static final int BUTTON_NEXT_ENCH_PAGE;
    private static final int BUTTON_PREV_ASPECT_PAGE;
    private static final int BUTTON_NEXT_ASPECT_PAGE;
    private static final int BUTTON_INCREASE_ENCH_LEVEL;
    private static final int BUTTON_DECREASE_ENCH_LEVEL;
    private static final int BUTTON_CURRENT_ENCH;
    private static final int[] BUTTON_VALID_ENCHANTS = new int[12];

    private final Map<EssentiaEnchanterRecipe, Integer> selectedRecipes = new LinkedHashMap<>();
    private final GuiButtonEnchantRecipe[] recipeButtons = new GuiButtonEnchantRecipe[BUTTON_VALID_ENCHANTS.length];
    private GuiButtonEnchantRecipe currentRecipeButton;
    public List<GuiEnchanterTooltip> aspectTooltips = new ArrayList<>();
    protected final Rectangle ENCHANT_AREA = new Rectangle();
    protected final Rectangle ESSENTIA_AREA = new Rectangle();
    private int enchantmentRow = 0;
    private int aspectRow = 0;

    public GuiEssentiaEnchanter(InventoryPlayer inventorySlotsIn, TileEssentiaEnchanter enchanter) {
        super(new ContainerEssentiaEnchanter(inventorySlotsIn, enchanter));
        this.xSize = 176;
        this.ySize = 204;
    }

    public ContainerEssentiaEnchanter getContainer() {
        return (ContainerEssentiaEnchanter) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.ENCHANT_AREA.setBounds(this.guiLeft + 28, this.guiTop + 17, 108, 36);
        this.ESSENTIA_AREA.setBounds(this.guiLeft + 28, this.guiTop + 71, 108, 36);
        this.addButton(new GuiButtonStartEnchant(this, BUTTON_START_ENCH, 8, 81));
        this.addButton(new GuiButtonScrollEnchants(this, BUTTON_PREV_ENCH_PAGE, 138, 19, false));
        this.addButton(new GuiButtonScrollEnchants(this, BUTTON_NEXT_ENCH_PAGE, 138, 40, true));
        this.addButton(new GuiButtonScrollAspects(this, BUTTON_PREV_ASPECT_PAGE, 138, 73, false));
        this.addButton(new GuiButtonScrollAspects(this, BUTTON_NEXT_ASPECT_PAGE, 138, 94, true));
        this.addButton(new GuiButtonEnchantLevel(this, BUTTON_INCREASE_ENCH_LEVEL, 151, 34, true));
        this.addButton(new GuiButtonEnchantLevel(this, BUTTON_DECREASE_ENCH_LEVEL, 151, 72, false));
        this.addButton(this.currentRecipeButton = new GuiButtonEnchantRecipe(this, BUTTON_CURRENT_ENCH, 152, 54));
        for (int i = 0; i < BUTTON_VALID_ENCHANTS.length; i++) {
            int id = BUTTON_VALID_ENCHANTS[i];
            int x = 29 + (18 * (i % 6));
            int y = 18 + (18 * (int) (i / 6.0));
            this.addButton(this.recipeButtons[i] = new GuiButtonEnchantRecipe(this, id, x, y));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.renderEngine.bindTexture(TEXTURE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int xStart = (this.width - this.xSize) / 2;
        int yStart = ((this.height - this.ySize) / 2);
        GlStateManager.enableBlend();
        drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
        this.drawEnchantArea(xStart, yStart);
        this.drawAspectArea(xStart, yStart);
        this.drawEssentiaBar(xStart, yStart);
    }

    private void drawEnchantArea(int xStart, int yStart) {
        drawTexturedModalRect(xStart + 28, yStart + 17, 0, 204, 108, 36);
        //Loading recipes into buttons
        List<EssentiaEnchanterRecipe> validRecipes = this.getValidRecipes();
        for(int i = 0; i < recipeButtons.length; i++) {
            GuiButtonEnchantRecipe recipeButton = this.recipeButtons[i];
            int recipeIndex = i + (this.getEnchantmentRow() * 6);
            EssentiaEnchanterRecipe recipe = recipeIndex < validRecipes.size() ? validRecipes.get(recipeIndex) : null;
            int level = recipe != null ? this.selectedRecipes.getOrDefault(recipe, 0) : 0;
            recipeButton.setButtonRecipe(recipe, level);
        }
        this.mc.renderEngine.bindTexture(TEXTURE);
    }

    private void drawAspectArea(int xStart, int yStart) {
        drawTexturedModalRect(xStart + 28, yStart + 71, 0, 204, 108, 36);
        //Rendering aspects
        AspectList aspectList = this.getRequiredAspects();
        Aspect[] aspects = aspectList.getAspects();
        this.aspectTooltips.clear();
        for(int i = 0; i < 12; i++) {
            int actualIndex = i + this.getAspectRow() * 6;
            if(actualIndex < aspects.length) {
                Aspect aspect = aspects[actualIndex];
                int amount = aspectList.getAmount(aspect);
                if (amount > 0) {
                    int xPos = 29 + (18 * (i % 6));
                    int yPos = 72 + (18 * (int) (i / 6.0));
                    this.renderAspect(aspect, amount, xPos, yPos);
                }
            } else {
                break;
            }
        }
        this.mc.renderEngine.bindTexture(TEXTURE);
    }

    private void drawEssentiaBar(int xStart, int yStart) {
        int totalVis = this.getContainer().getEnchanter().getAspectsCache().visSize();
        int renderWidth = 64;
        int start = 0;
        if(totalVis > 0) {
            AspectList aspectList = this.getContainer().getEnchanter().getAspectsToRender();
            Aspect[] aspects = aspectList.getAspects();
            for(Aspect aspect : aspects) {
                int amount = aspectList.getAmount(aspect);
                if(amount > 0) {
                    int fill = Math.max(1, (int) Math.round((double) renderWidth * (double) amount / (double) totalVis));
                    Color color = new Color(aspect.getColor());
                    GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
                    drawTexturedModalRect(this.guiLeft + 50 + start, this.guiTop + 58, 182 + start, 101, fill, 8);
                    start += fill;
                }
            }
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        drawTexturedModalRect(this.guiLeft + 44, this.guiTop + 55, 176, 86, 76, 14);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        super.renderHoveredToolTip(mouseX, mouseY);
        this.aspectTooltips.stream().filter(tooltip -> tooltip.isMouseOver(mouseX, mouseY)).forEach(tooltip -> tooltip.renderTooltip(mouseX, mouseY));
        this.buttonList.stream().filter(GuiButton::isMouseOver).forEach(button -> button.drawButtonForegroundLayer(mouseX, mouseY));
    }

    @Override
    public void handleMouseInput() throws IOException {
        int dWheel = Mouse.getEventDWheel();
        if(dWheel != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            if(ENCHANT_AREA.contains(mouseX, mouseY)) {
                if(dWheel < 0) {
                    this.nextEnchantmentRow();
                } else {
                    this.prevEnchantmentRow();
                }
            } else if(ESSENTIA_AREA.contains(mouseX, mouseY)) {
                if(dWheel < 0) {
                    this.nextAspectRow();
                } else {
                    this.prevAspectRow();
                }
            }
        }
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(!button.enabled || !button.visible)
            return;

        if (button.id == BUTTON_START_ENCH) {
            ItemStack output = this.getEnchantedOutput();
            if(!output.isEmpty()) {
                BlockPos pos = this.getContainer().getEnchanter().getPos();
                AspectList aspectList = this.getRequiredAspects();
                PacketHandler.INSTANCE.sendToServer(new PacketStartEnchanting(pos, output, aspectList));
                this.setCurrentRecipe(null);
            }
        } else if(button.id == BUTTON_PREV_ENCH_PAGE) {
            this.prevEnchantmentRow();
        } else if(button.id == BUTTON_NEXT_ENCH_PAGE) {
            this.nextEnchantmentRow();
        } else if(button.id == BUTTON_PREV_ASPECT_PAGE) {
            this.prevAspectRow();
        } else if(button.id == BUTTON_NEXT_ASPECT_PAGE) {
            this.nextAspectRow();
        } else if(button.id == BUTTON_INCREASE_ENCH_LEVEL) {
            this.addSelectedRecipeLevel();
        } else if(button.id == BUTTON_DECREASE_ENCH_LEVEL) {
            this.removeSelectedRecipeLevel();
        } else if(button.id == BUTTON_CURRENT_ENCH) {
            this.setCurrentRecipe(null);
        } else {
            for(int id : BUTTON_VALID_ENCHANTS) {
                if(button.id == id && button instanceof GuiButtonEnchantRecipe) {
                    EssentiaEnchanterRecipe recipe = ((GuiButtonEnchantRecipe) button).getButtonRecipe();
                    if(recipe != null) {
                        this.setCurrentRecipe(recipe);
                    }
                    break;
                }
            }
        }
    }

    public boolean isReadyToEnchant() {
        return !this.selectedRecipes.isEmpty();
    }

    public List<EssentiaEnchanterRecipe> getValidRecipes() {
        ItemStack stack = this.getContainer().getEnchanter().getItemToEnchant();
        if(!stack.isEmpty() && stack.isItemEnchantable()) {
            if(!this.getContainer().getEnchanter().isCrafting()) {
                return EssentiaEnchanterRecipeRegistry.getValidEnchantments(stack, this.selectedRecipes.keySet().toArray(new EssentiaEnchanterRecipe[0]));
            }
        } else {
            this.selectedRecipes.clear();
        }
        return Collections.emptyList();
    }

    public Map<EssentiaEnchanterRecipe, Integer> getSelectedRecipes() {
        return this.selectedRecipes;
    }

    public void addSelectedRecipeLevel() {
        EssentiaEnchanterRecipe recipe = this.getCurrentRecipe();
        if(recipe != null) {
            if(!this.selectedRecipes.containsKey(recipe)) {
                this.enchantmentRow = 0;
            }
            this.selectedRecipes.put(recipe, Math.min(recipe.getMaxEnchantmentLevel(), this.selectedRecipes.getOrDefault(recipe, 0) + 1));
        }
    }

    public void removeSelectedRecipeLevel() {
        EssentiaEnchanterRecipe recipe = this.getCurrentRecipe();
        if(recipe != null) {
            int currLevel = this.selectedRecipes.getOrDefault(recipe, 0);
            if(currLevel <= 1) {
                this.selectedRecipes.remove(recipe);
                this.aspectRow = 0;
            } else {
                this.selectedRecipes.put(recipe, currLevel - 1);
            }
        }
    }

    public ItemStack getEnchantedOutput() {
        ItemStack stack = this.getContainer().getEnchanter().getItemToEnchant().copy();
        if(!stack.isEmpty()) {
            getSelectedRecipes().forEach((recipe, level) -> recipe.applyEnchant(stack, level));
        }
        return stack;
    }

    public AspectList getRequiredAspects() {
        if(this.getContainer().getEnchanter().isCrafting()) {
            return this.getContainer().getEnchanter().getAspects();
        } else {
            AspectList aspectList = new AspectList();
            ItemStack stack = this.getContainer().getEnchanter().getItemToEnchant();
            if(stack.isEmpty()) {
                this.getSelectedRecipes().clear();
            } else {
                this.getSelectedRecipes().keySet().removeIf(recipe -> !recipe.canApplyTo(stack));
            }
            this.getSelectedRecipes().forEach((recipe, level) -> aspectList.add(recipe.getEnchantAspects(level)));
            return aspectList;
        }
    }

    @Nullable
    public EssentiaEnchanterRecipe getCurrentRecipe() {
        return this.currentRecipeButton.getButtonRecipe();
    }

    public int getCurrentRecipeLevel() {
        EssentiaEnchanterRecipe recipe = this.getCurrentRecipe();
        return recipe != null ? this.getSelectedRecipes().getOrDefault(recipe, 0) : 0;
    }

    public void setCurrentRecipe(@Nullable EssentiaEnchanterRecipe recipe) {
        int level = recipe != null ? this.selectedRecipes.getOrDefault(recipe, 0) : 0;
        this.currentRecipeButton.setButtonRecipe(recipe, level);
    }

    public int getEnchantmentRow() {
        return enchantmentRow;
    }

    public int getMaxEnchantRows() {
        return (int) Math.ceil((double) this.getValidRecipes().size() / 6.0);
    }

    public void nextEnchantmentRow() {
        int maxRows = this.getMaxEnchantRows();
        if(maxRows > 2 && this.getEnchantmentRow() < maxRows - 2) {
            this.enchantmentRow++;
        }
    }

    public void prevEnchantmentRow() {
        if(this.enchantmentRow > 0) {
            this.enchantmentRow--;
        }
    }

    public int getAspectRow() {
        return aspectRow;
    }

    public int getMaxAspectRows() {
        return (int) Math.ceil((double) this.getRequiredAspects().size() / 6.0);
    }

    public void nextAspectRow() {
        int maxRows = this.getMaxAspectRows();
        if(maxRows > 2 && this.getAspectRow() < maxRows - 2) {
            this.aspectRow++;
        }
    }

    public void prevAspectRow() {
        if(this.aspectRow > 0) {
            this.aspectRow--;
        }
    }

    public void renderAspect(Aspect aspect, int amount, int xPos, int yPos) {
        GlStateManager.pushMatrix();
        this.mc.renderEngine.bindTexture(aspect.getImage());
        GlStateManager.enableBlend();
        GlStateManager.translate(this.guiLeft, this.guiTop, 0);
        Color color = new Color(aspect.getColor());
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 0.0F, 0.0F, 16, 16, 16, 16);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(0.5, 0.5, 0.5);
        if (amount > 1 && this.mc.currentScreen != null) {
            this.mc.currentScreen.drawCenteredString(this.mc.fontRenderer, TextFormatting.WHITE + "" + amount, (xPos + 12) * 2, (yPos + 12) * 2, 0);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.aspectTooltips.add(new GuiEnchanterTooltip(xPos, yPos, 16, 16, Collections.singletonList(aspect.getName())));
    }

    public class GuiEnchanterTooltip {
        public final int xPos;
        public final int yPos;
        public final int width;
        public final int height;
        public final List<String> tooltips;

        public GuiEnchanterTooltip(int xPos, int yPos, int width, int height, List<String> tooltips) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = width;
            this.height = height;
            this.tooltips = tooltips;
        }

        public boolean isMouseOver(int mouseX, int mouseY) {
            return isPointInRegion(this.xPos, this.yPos, this.width, this.height, mouseX, mouseY);
        }

        public void renderTooltip(int mouseX, int mouseY) {
            drawHoveringText(this.tooltips, mouseX, mouseY, fontRenderer);
        }

        public List<String> getTooltips() {
            return this.tooltips;
        }
    }

    static {
        BUTTON_START_ENCH = 0;
        BUTTON_PREV_ENCH_PAGE = 1;
        BUTTON_NEXT_ENCH_PAGE = 2;
        BUTTON_PREV_ASPECT_PAGE = 3;
        BUTTON_NEXT_ASPECT_PAGE = 4;
        BUTTON_INCREASE_ENCH_LEVEL = 5;
        BUTTON_DECREASE_ENCH_LEVEL = 6;
        BUTTON_CURRENT_ENCH = 7;
        int buttonId = BUTTON_CURRENT_ENCH + 1;
        for(int i = 0; i < BUTTON_VALID_ENCHANTS.length; i++) {
            BUTTON_VALID_ENCHANTS[i] = buttonId;
            buttonId++;
        }
    }
}
