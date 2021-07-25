package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseContainerScreen;
import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends TileEntityBaseContainerScreen<TrashCanTile,T> {

    public TrashCanScreen(T container, String title){
        super(container, new TranslatableComponent(title));
        this.setDrawSlots(false);
    }

    @Override
    protected int sizeX(TrashCanTile trashCanTile){
        return this.menu.width;
    }

    @Override
    protected int sizeY(TrashCanTile trashCanTile){
        return this.menu.height;
    }

    protected abstract String getBackground();

    @Override
    protected void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        ScreenUtils.drawTexture(matrixStack, 0, 0, this.sizeX(), this.sizeY());
    }

    @Override
    protected void renderForeground(PoseStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.drawCenteredString(matrixStack, this.title, this.sizeX() / 2f, 6);
        ScreenUtils.drawString(matrixStack, this.playerInventoryTitle, 21, this.sizeY() - 94);

        this.drawText(matrixStack, tile);
    }

    protected abstract void drawText(PoseStack matrixStack, TrashCanTile tile);

    public void renderToolTip(PoseStack matrixStack, boolean translate, String string, int x, int y){
        super.renderTooltip(matrixStack, translate ? new TranslatableComponent(string) : new TextComponent(string), x, y);
    }

    public void renderToolTip(PoseStack matrixStack, List<Component> text, int x, int y){
        super.renderComponentToolTip(matrixStack, text, x, y, this.font);
    }
}
