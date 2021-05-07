package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseContainerScreen;
import com.supermartijn642.trashcans.TrashCanTile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends TileEntityBaseContainerScreen<TrashCanTile,T> {

    public TrashCanScreen(T container, String title){
        super(container, new TextComponentTranslation(title));
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
    protected void renderBackground(int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        ScreenUtils.drawTexture(0, 0, this.sizeX(), this.sizeY());
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, TrashCanTile tile){
        ScreenUtils.drawCenteredString(this.title, this.sizeX() / 2f, 6);
        ScreenUtils.drawString(this.container.player.inventory.getDisplayName(), 21, this.sizeY() - 94);

        this.drawText(tile);
    }

    protected abstract void drawText(TrashCanTile tile);

    public void renderToolTip(boolean translate, String string, int x, int y){
        super.drawHoveringText(translate ? new TextComponentTranslation(string).getFormattedText() : string, x, y);
    }

    public void renderToolTip(List<ITextComponent> text, int x, int y){
        super.drawHoveringText(text.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList()), x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
    }
}
