package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseContainerScreen;
import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends TileEntityBaseContainerScreen<TrashCanTile,T> {

    public TrashCanScreen(T container, String title){
        super(container, new TranslationTextComponent(title));
        this.setDrawSlots(false);
    }

    @Override
    protected int sizeX(TrashCanTile trashCanTile){
        return this.container.width;
    }

    @Override
    protected int sizeY(TrashCanTile trashCanTile){
        return this.container.height;
    }

    protected abstract String getBackground();

    @Override
    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        ScreenUtils.drawTexture(matrixStack, 0, 0, this.sizeX(), this.sizeY());
    }

    @Override
    protected void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.drawCenteredString(matrixStack, this.title, this.sizeX() / 2f, 6);
        ScreenUtils.drawString(matrixStack, this.playerInventory.getDisplayName(), 21, this.sizeY() - 94);

        this.drawText(matrixStack, tile);
    }

    protected abstract void drawText(MatrixStack matrixStack, TrashCanTile tile);

    public void renderToolTip(MatrixStack matrixStack, boolean translate, String string, int x, int y){
        super.renderTooltip(matrixStack, translate ? new TranslationTextComponent(string) : new StringTextComponent(string), x, y);
    }

    public void renderToolTip(MatrixStack matrixStack, List<ITextComponent> text, int x, int y){
        super.renderWrappedToolTip(matrixStack, text, x, y, this.font);
    }
}
