package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
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
        super(container, "trashcans.gui.ultimate_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanTile tile){
        this.itemWhitelistButton = this.addWidget(new WhitelistButton(175, this.sizeY() - 185, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.getTilePos()))));
        this.itemWhitelistButton.update(tile.itemFilterWhitelist);

        this.liquidWhitelistButton = this.addWidget(new WhitelistButton(175, this.sizeY() - 155, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.getTilePos()))));
        this.liquidWhitelistButton.update(tile.liquidFilterWhitelist);

        this.checkBox = this.addWidget(new CheckBox(21, 127, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.getTilePos()))));
        this.checkBox.update(tile.useEnergyLimit);
        this.leftArrow = this.addWidget(new ArrowButton(49, 127, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getTilePos(), this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.active = tile.useEnergyLimit;
        this.rightArrow = this.addWidget(new ArrowButton(170, 127, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getTilePos(), this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.active = tile.useEnergyLimit;
    }

    @Override
    protected void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
        if(this.leftArrow.isHovered() && this.leftArrow.active)
            this.renderToolTip(matrixStack, false, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.isHovered() && this.rightArrow.active)
            this.renderToolTip(matrixStack, false, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
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
    protected void drawText(MatrixStack matrixStack, TrashCanTile tile){
        ScreenUtils.drawString(matrixStack, new TranslationTextComponent("trashcans.gui.ultimate_trash_can.item_filter"), 8, 53);

        ScreenUtils.drawString(matrixStack, new TranslationTextComponent("trashcans.gui.ultimate_trash_can.liquid_filter"), 8, 83);

        ScreenUtils.drawString(matrixStack, new TranslationTextComponent("trashcans.gui.ultimate_trash_can.energy_limit"), 8, 113);
        ScreenUtils.drawCenteredString(matrixStack, new StringTextComponent(I18n.format("trashcans.gui.energy_trash_can.value").replace("$number$", "" + tile.energyLimit)), 114, 132);
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
