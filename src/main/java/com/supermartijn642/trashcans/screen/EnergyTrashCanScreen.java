package com.supermartijn642.trashcans.screen;

import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanScreen extends TrashCanScreen<EnergyTrashCanContainer> {

    private CheckBox checkBox;
    private ArrowButton leftArrow, rightArrow;

    private boolean shift, control;

    public EnergyTrashCanScreen(EnergyTrashCanContainer container){
        super(container, "gui.energy_trash_can.title");
    }

    @Override
    protected void addButtons(TrashCanTile tile){
        this.checkBox = this.addButton(new CheckBox(0, this.guiLeft + 21, this.guiTop + 66, () -> TrashCans.channel.sendToServer(new PacketToggleEnergyLimit(this.container.pos))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addButton(new ArrowButton(1, this.guiLeft + 49, this.guiTop + 66, true, () -> TrashCans.channel.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.enabled = tile.useEnergyLimit;
        this.rightArrow = this.addButton(new ArrowButton(2, this.guiLeft + 170, this.guiTop + 66, false, () -> TrashCans.channel.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.enabled = tile.useEnergyLimit;
    }

    @Override
    protected void drawToolTips(TrashCanTile tile, int mouseX, int mouseY){
        if(this.checkBox.isMouseOver())
            this.renderToolTip(true, "gui.energy_trash_can.check." + (this.checkBox.checked ? "on" : "off"), mouseX, mouseY);
        if(this.leftArrow.isMouseOver() && this.leftArrow.enabled)
            this.renderToolTip(false, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.isMouseOver() && this.rightArrow.enabled)
            this.renderToolTip(false, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow.enabled = tile.useEnergyLimit;
        this.rightArrow.enabled = tile.useEnergyLimit;
    }

    @Override
    protected String getBackground(){
        return "energy_screen.png";
    }

    @Override
    protected void drawText(TrashCanTile tile){
        this.drawString(new TextComponentTranslation("gui.energy_trash_can.limit"), 8, 52);
        this.drawCenteredString(I18n.format("gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit), 114, 71);
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
