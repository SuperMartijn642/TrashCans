package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends ContainerScreen<T> {

    public TrashCanScreen(T container, String title){
        super(container, container.player.inventory, new TranslationTextComponent(title));
        this.xSize = container.width;
        this.ySize = container.height;
    }

    @Override
    protected void init(){
        super.init();

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.addButtons(tile);
    }

    protected abstract void addButtons(TrashCanTile tile);

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawToolTips(tile, mouseX, mouseY);
    }

    protected abstract void drawToolTips(TrashCanTile tile, int mouseX, int mouseY);

    @Override
    public void tick(){
        TrashCanTile tile = this.container.getTileOrClose();
        if(tile == null)
            return;

        super.tick();
        this.tick(tile);
    }

    protected abstract void tick(TrashCanTile tile);

    protected abstract String getBackground();

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        this.drawCenteredString(this.title, this.xSize / 2f, 6);
        this.drawString(this.playerInventory.getDisplayName(), 21, this.ySize - 94);

        TrashCanTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawText(tile);
    }

    protected abstract void drawText(TrashCanTile tile);

    protected void drawCenteredString(ITextComponent text, float x, float y){
        this.drawCenteredString(text.getFormattedText(), x, y);
    }

    protected void drawCenteredString(String s, float x, float y){
        this.font.drawString(s, this.guiLeft + x - this.font.getStringWidth(s) / 2f, this.guiTop + y, 4210752);
    }

    protected void drawString(ITextComponent text, float x, float y){
        this.drawString(text.getFormattedText(), x, y);
    }

    protected void drawString(String s, float x, float y){
        this.font.drawString(s, this.guiLeft + x, this.guiTop + y, 4210752);
    }

    public void renderToolTip(List<ITextComponent> text, int x, int y){
        super.renderTooltip(text.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList()), x, y);
    }

    public void renderToolTip(boolean translate, String string, int x, int y){
        super.renderTooltip(translate ? new TranslationTextComponent(string).getFormattedText() : string, x, y);
    }
}
