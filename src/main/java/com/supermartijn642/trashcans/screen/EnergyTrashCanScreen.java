package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
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
        this.checkBox = this.func_230480_a_(new CheckBox(this.guiLeft + 21, this.guiTop + 66, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.pos))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.func_230480_a_(new ArrowButton(this.guiLeft + 49, this.guiTop + 66, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.field_230693_o_ = tile.useEnergyLimit;
        this.rightArrow = this.func_230480_a_(new ArrowButton(this.guiLeft + 170, this.guiTop + 66, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.pos, this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.field_230693_o_ = tile.useEnergyLimit;
    }

    @Override
    protected void drawToolTips(MatrixStack matrixStack, TrashCanTile tile, int mouseX, int mouseY){
        if(this.checkBox.func_230449_g_())
            this.renderToolTip(matrixStack, true, "gui.energy_trash_can.check." + (this.checkBox.checked ? "on" : "off"), mouseX, mouseY);
        if(this.leftArrow.func_230449_g_() && this.leftArrow.field_230693_o_)
            this.renderToolTip(matrixStack, false, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.func_230449_g_() && this.rightArrow.field_230693_o_)
            this.renderToolTip(matrixStack, false, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
    }

    @Override
    protected void tick(TrashCanTile tile){
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow.field_230693_o_ = tile.useEnergyLimit;
        this.rightArrow.field_230693_o_ = tile.useEnergyLimit;
    }

    @Override
    protected String getBackground(){
        return "energy_screen.png";
    }

    @Override
    protected void drawText(MatrixStack matrixStack, TrashCanTile tile){
        this.drawString(matrixStack, new TranslationTextComponent("gui.energy_trash_can.limit"), 8, 52);
        this.drawCenteredString(matrixStack, new StringTextComponent(I18n.format("gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit)), 114, 71);
    }

    @Override
    public boolean func_231046_a_(int keyCode, int scanCode, int modifiers){
        if(keyCode == 340)
            this.shift = true;
        else if(keyCode == 341)
            this.control = true;
        return super.func_231046_a_(keyCode, scanCode, modifiers);
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
