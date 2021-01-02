package com.supermartijn642.trashcans.screen;

import com.google.common.collect.Lists;
import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
        this.checkBox = this.addButton(new CheckBox(this.guiLeft + 21, this.guiTop + 66, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.pos))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addButton(new ArrowButton(this.guiLeft + 49, this.guiTop + 66, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? -1 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow = this.addButton(new ArrowButton(this.guiLeft + 170, this.guiTop + 66, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected void drawToolTips(TrashCanTile tile, int mouseX, int mouseY){
        if(this.checkBox.isHovered())
            this.renderToolTip(true, "gui.energy_trash_can.check." + (this.checkBox.checked ? "on" : "off"), mouseX, mouseY);
        if(this.leftArrow.isHovered() && this.leftArrow.active)
            this.renderToolTip(Lists.newArrayList(
                new StringTextComponent("-" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)),
                new TranslationTextComponent("gui.energy_trash_can.limit.change1", "-100").applyTextStyle(TextFormatting.AQUA),
                new TranslationTextComponent("gui.energy_trash_can.limit.change2", "-10000").applyTextStyle(TextFormatting.AQUA),
                new TranslationTextComponent("gui.energy_trash_can.limit.change3", "-1").applyTextStyle(TextFormatting.AQUA)),
                mouseX, mouseY);
        if(this.rightArrow.isHovered() && this.rightArrow.active)
            this.renderToolTip(Lists.newArrayList(
                new StringTextComponent("+" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)),
                new TranslationTextComponent("gui.energy_trash_can.limit.change1", "+100").applyTextStyle(TextFormatting.AQUA),
                new TranslationTextComponent("gui.energy_trash_can.limit.change2", "+10000").applyTextStyle(TextFormatting.AQUA),
                new TranslationTextComponent("gui.energy_trash_can.limit.change3", "+1").applyTextStyle(TextFormatting.AQUA)),
                mouseX, mouseY);
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected String getBackground(){
        return "energy_screen.png";
    }

    @Override
    protected void drawText(TrashCanTile tile){
        this.drawString(new TranslationTextComponent("gui.energy_trash_can.limit"), 8, 52);
        this.drawCenteredString(I18n.format("gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit), 114, 71);
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
