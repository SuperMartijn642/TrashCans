package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

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
        this.itemWhitelistButton = this.addButton(new WhitelistButton(0, this.guiLeft + 175, this.guiTop + this.ySize - 185, () -> TrashCans.channel.sendToServer(new PacketToggleItemWhitelist(this.container.pos))));
        this.itemWhitelistButton.update(tile.itemFilterWhitelist);

        this.liquidWhitelistButton = this.addButton(new WhitelistButton(1, this.guiLeft + 175, this.guiTop + this.ySize - 155, () -> TrashCans.channel.sendToServer(new PacketToggleLiquidWhitelist(this.container.pos))));
        this.liquidWhitelistButton.update(tile.liquidFilterWhitelist);

        this.checkBox = this.addButton(new CheckBox(2, this.guiLeft + 21, this.guiTop + 127, () -> TrashCans.channel.sendToServer(new PacketToggleEnergyLimit(this.container.pos))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addButton(new ArrowButton(3, this.guiLeft + 49, this.guiTop + 127, true, () -> TrashCans.channel.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.enabled = tile.useEnergyLimit;
        this.rightArrow = this.addButton(new ArrowButton(4, this.guiLeft + 170, this.guiTop + 127, false, () -> TrashCans.channel.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.enabled = tile.useEnergyLimit;
    }

    @Override
    protected void drawToolTips(TrashCanTile tile, int mouseX, int mouseY){
        if(this.itemWhitelistButton.isMouseOver())
            this.renderToolTip(true, "gui.whitelist." + (this.itemWhitelistButton.white ? "on" : "off"), mouseX, mouseY);

        if(this.liquidWhitelistButton.isMouseOver())
            this.renderToolTip(true, "gui.whitelist." + (this.liquidWhitelistButton.white ? "on" : "off"), mouseX, mouseY);

        if(this.checkBox.isMouseOver())
            this.renderToolTip(true, "gui.energy_trash_can.check." + (this.checkBox.checked ? "on" : "off"), mouseX, mouseY);
        if(this.leftArrow.isMouseOver() && this.leftArrow.enabled)
            this.renderToolTip(false, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.isMouseOver() && this.rightArrow.enabled)
            this.renderToolTip(false, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.itemWhitelistButton.update(tile.itemFilterWhitelist);

        this.liquidWhitelistButton.update(tile.liquidFilterWhitelist);

        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow.enabled = tile.useEnergyLimit;
        this.rightArrow.enabled = tile.useEnergyLimit;
    }

    @Override
    protected String getBackground(){
        return "ultimate_screen.png";
    }

    @Override
    protected void drawText(TrashCanTile tile){
        this.drawString(new TextComponentTranslation("gui.ultimate_trash_can.item_filter"), 8, 53);

        this.drawString(new TextComponentTranslation("gui.ultimate_trash_can.liquid_filter"), 8, 83);

        this.drawString(new TextComponentTranslation("gui.ultimate_trash_can.energy_limit"), 8, 113);
        this.drawCenteredString(I18n.format("gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit), 114, 132);
    }

    @Override
    public void handleKeyboardInput() throws IOException{
        if(Keyboard.getEventKey() == 42)
            this.shift = Keyboard.getEventKeyState();
        if(Keyboard.getEventKey() == 29)
            this.control = Keyboard.getEventKeyState();
        super.handleKeyboardInput();
    }
}
