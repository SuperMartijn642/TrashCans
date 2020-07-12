package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanScreen extends TrashCanScreen<UltimateTrashCanContainer> {

    private WhitelistButton itemWhitelistButton;
    private WhitelistButton liquidWhitelistButton;
    private CheckBox checkBox;
    private ArrowButton leftArrow, rightArrow;

    private boolean shift, control;

    public UltimateTrashCanScreen(UltimateTrashCanContainer container){
        super(container, "gui.ultimate_trash_can.title");
    }

    @Override
    protected void addButtons(TrashCanTile tile){
        this.itemWhitelistButton = this.addButton(new WhitelistButton(this.guiLeft + 175,this.guiTop + this.ySize - 185, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.pos))));
        this.itemWhitelistButton.update(tile.itemFilterWhitelist);

        this.liquidWhitelistButton = this.addButton(new WhitelistButton(this.guiLeft + 175,this.guiTop + this.ySize - 155, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.pos))));
        this.liquidWhitelistButton.update(tile.liquidFilterWhitelist);

        this.checkBox = this.addButton(new CheckBox(this.guiLeft + 21,this.guiTop + 127,() -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.pos))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addButton(new ArrowButton(this.guiLeft + 49,this.guiTop + 127, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow = this.addButton(new ArrowButton(this.guiLeft + 170,this.guiTop + 127, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected void drawToolTips(TrashCanTile tile, int mouseX, int mouseY){
        if(this.itemWhitelistButton.isHovered())
            this.renderToolTip(true, "gui.whitelist." + (this.itemWhitelistButton.white ? "on" : "off"), mouseX, mouseY);

        if(this.liquidWhitelistButton.isHovered())
            this.renderToolTip(true, "gui.whitelist." + (this.liquidWhitelistButton.white ? "on" : "off"), mouseX, mouseY);

        if(this.checkBox.isHovered())
            this.renderToolTip(true, "gui.energy_trash_can.check." + (this.checkBox.checked ? "on" : "off"), mouseX, mouseY);
        if(this.leftArrow.isHovered() && this.leftArrow.active)
            this.renderToolTip(false, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.isHovered() && this.rightArrow.active)
            this.renderToolTip(false, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.itemWhitelistButton.update(tile.itemFilterWhitelist);

        this.liquidWhitelistButton.update(tile.liquidFilterWhitelist);

        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected String getBackground(){
        return "ultimate_screen.png";
    }

    @Override
    protected void drawText(TrashCanTile tile){
        this.drawString(new TranslationTextComponent("gui.ultimate_trash_can.item_filter"), 8, 53);

        this.drawString(new TranslationTextComponent("gui.ultimate_trash_can.liquid_filter"), 8, 83);

        this.drawString(new TranslationTextComponent("gui.ultimate_trash_can.energy_limit"), 8, 113);
        this.drawCenteredString(I18n.format("gui.energy_trash_can.value").replace("$number$","" + tile.energyLimit), 114, 132);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if(keyCode == 340)
            this.shift = true;
        else if(keyCode == 341)
            this.control = true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        if(keyCode == 340)
            this.shift = false;
        else if(keyCode == 341)
            this.control = false;
        return false;
    }
}
