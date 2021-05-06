package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class LiquidTrashCanScreen extends TrashCanScreen<LiquidTrashCanContainer> {

    private WhitelistButton whitelistButton;

    public LiquidTrashCanScreen(LiquidTrashCanContainer container){
        super(container, "trashcans.gui.liquid_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanTile tile){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.sizeY() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.getTilePos()))));
        this.whitelistButton.update(tile.liquidFilterWhitelist);
    }

    @Override
    protected void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.whitelistButton.update(tile.liquidFilterWhitelist);
    }

    @Override
    protected String getBackground(){
        return "liquid_screen.png";
    }

    @Override
    protected void drawText(MatrixStack matrixStack, TrashCanTile tile){
        ScreenUtils.drawString(matrixStack, new TranslationTextComponent("trashcans.gui.liquid_trash_can.filter"), 8, 52);
    }
}
