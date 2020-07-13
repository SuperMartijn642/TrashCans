package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends GuiContainer {

    protected final T container;
    protected final String title;

    public TrashCanScreen(T container, String title){
        super(container);
        this.container = container;
        this.title = title;

        this.xSize = container.width;
        this.ySize = container.height;
    }

    @Override
    public void initGui(){
        super.initGui();

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.addButtons(tile);
    }

    protected abstract void addButtons(TrashCanTile tile);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawToolTips(tile, mouseX, mouseY);
    }

    protected abstract void drawToolTips(TrashCanTile tile, int mouseX, int mouseY);

    @Override
    public void updateScreen(){
        TrashCanTile tile = this.container.getTileOrClose();
        if(tile == null)
            return;

        super.updateScreen();
        this.tick(tile);
    }

    protected abstract void tick(TrashCanTile tile);

    protected abstract String getBackground();

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        this.drawCenteredString(new TextComponentTranslation(this.title), this.xSize / 2, 6);
        this.drawString(this.container.player.inventory.getDisplayName(), 21, this.ySize - 94);

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawText(tile);
    }

    protected abstract void drawText(TrashCanTile tile);

    public void drawCenteredString(String s, int x, int y){
        this.fontRenderer.drawString(s, this.guiLeft + x - this.fontRenderer.getStringWidth(s) / 2, this.guiTop + y, 4210752);
    }

    public void drawCenteredString(ITextComponent text, int x, int y){
        String s = text.getFormattedText();
        this.fontRenderer.drawString(s, this.guiLeft + x - this.fontRenderer.getStringWidth(s) / 2, this.guiTop + y, 4210752);
    }

    public void drawString(ITextComponent text, int x, int y){
        String s = text.getFormattedText();
        this.fontRenderer.drawString(s, this.guiLeft + x, this.guiTop + y, 4210752);
    }

    public void renderToolTip(boolean translate, String string, int x, int y){
        super.drawHoveringText(translate ? new TextComponentTranslation(string).getFormattedText() : string, x, y);
    }

    @Override
    protected void actionPerformed(GuiButton button){
        if(button instanceof Pressable)
            ((Pressable)button).onPress();
    }
}
