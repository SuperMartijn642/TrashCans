package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.util.text.TextComponentTranslation;

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
        this.whitelistButton = this.addButton(new WhitelistButton(0, this.guiLeft + 175, this.guiTop + this.ySize - 118, () -> TrashCans.channel.sendToServer(new PacketToggleLiquidWhitelist(this.container.pos))));
        this.whitelistButton.update(tile.liquidFilterWhitelist);
    }

    @Override
    protected void drawToolTips(TrashCanTile tile, int mouseX, int mouseY){
        if(this.whitelistButton.isMouseOver())
            this.renderToolTip(true, "gui.whitelist." + (this.whitelistButton.white ? "on" : "off"), mouseX, mouseY);
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
    protected void drawText(TrashCanTile tile){
        this.drawString(new TextComponentTranslation("gui.liquid_trash_can.filter"), 8, 52);
    }
}
