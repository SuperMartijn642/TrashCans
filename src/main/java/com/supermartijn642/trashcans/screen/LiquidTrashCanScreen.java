package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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
        super(container, "gui.liquid_trash_can.title");
    }

    @Override
    protected void addButtons(TrashCanTile tile){
        this.whitelistButton = this.func_230480_a_(new WhitelistButton(this.guiLeft + 175, this.guiTop + this.ySize - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.pos))));
        this.whitelistButton.update(tile.liquidFilterWhitelist);
    }

    @Override
    protected void drawToolTips(MatrixStack matrixStack, TrashCanTile tile, int mouseX, int mouseY){
        if(this.whitelistButton.func_230449_g_())
            this.renderToolTip(matrixStack, true, "gui.whitelist." + (this.whitelistButton.white ? "on" : "off"), mouseX, mouseY);
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
        this.drawString(matrixStack, new TranslationTextComponent("gui.liquid_trash_can.filter"), 8, 52);
    }
}
