package com.supermartijn642.trashcans.screen;

import com.google.common.collect.Lists;
import com.supermartijn642.core.gui.ScreenUtils;
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
        super(container, "trashcans.gui.energy_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanTile tile){
        this.checkBox = this.addWidget(new CheckBox(21, 66, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.menu.getTilePos()))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addWidget(new ArrowButton(49, 66, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.menu.getTilePos(), this.shift ? this.control ? -1 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow = this.addWidget(new ArrowButton(170, 66, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.menu.getTilePos(), this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected void renderTooltips(int mouseX, int mouseY, TrashCanTile tile){
        if(this.leftArrow.isHovered() && this.leftArrow.active)
            this.renderToolTip(Lists.newArrayList(
                new StringTextComponent("-" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change1", "-100").withStyle(TextFormatting.AQUA),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change2", "-10000").withStyle(TextFormatting.AQUA),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change3", "-1").withStyle(TextFormatting.AQUA)),
                mouseX, mouseY);
        if(this.rightArrow.isHovered() && this.rightArrow.active)
            this.renderToolTip(Lists.newArrayList(
                new StringTextComponent("+" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change1", "+100").withStyle(TextFormatting.AQUA),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change2", "+10000").withStyle(TextFormatting.AQUA),
                new TranslationTextComponent("trashcans.gui.energy_trash_can.limit.change3", "+1").withStyle(TextFormatting.AQUA)),
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
        ScreenUtils.drawString(new TranslationTextComponent("trashcans.gui.energy_trash_can.limit"), 8, 52);
        ScreenUtils.drawCenteredString(new StringTextComponent(I18n.get("trashcans.gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit)), 114, 71);
    }

    @Override
    public boolean keyPressed(int keyCode){
        if(keyCode == 340)
            this.shift = true;
        else if(keyCode == 341)
            this.control = true;
        return super.keyPressed(keyCode);
    }

    @Override
    public boolean keyReleased(int keyCode){
        if(keyCode == 340)
            this.shift = false;
        else if(keyCode == 341)
            this.control = false;
        return super.keyReleased(keyCode);
    }
}
