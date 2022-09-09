package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.client.resources.I18n;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanScreen extends TrashCanScreen<UltimateTrashCanContainer> {

    private WhitelistButton itemWhitelistButton;
    private WhitelistButton liquidWhitelistButton;
    private CheckBox checkBox;
    private ArrowButton leftArrow, rightArrow;

    private boolean shift, control;

    public UltimateTrashCanScreen(){
        super("trashcans.gui.ultimate_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.itemWhitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 185, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.getBlockEntityPos()))));
        this.itemWhitelistButton.update(entity.itemFilterWhitelist);

        this.liquidWhitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 155, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.getBlockEntityPos()))));
        this.liquidWhitelistButton.update(entity.liquidFilterWhitelist);

        this.checkBox = this.addWidget(new CheckBox(21, 127, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.getBlockEntityPos()))));
        this.checkBox.update(entity.useEnergyLimit);
        this.leftArrow = this.addWidget(new ArrowButton(49, 127, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.setActive(entity.useEnergyLimit);
        this.rightArrow = this.addWidget(new ArrowButton(170, 127, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.setActive(entity.useEnergyLimit);
    }

    @Override
    protected void renderTooltips(MatrixStack poseStack, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderTooltips(poseStack, mouseX, mouseY, entity);
        if(this.leftArrow.isFocused() && this.leftArrow.isActive())
            ScreenUtils.drawTooltip(poseStack, "" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000), mouseX, mouseY);
        if(this.rightArrow.isFocused() && this.rightArrow.isActive())
            ScreenUtils.drawTooltip(poseStack, "+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000), mouseX, mouseY);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.itemWhitelistButton.update(entity.itemFilterWhitelist);

        this.liquidWhitelistButton.update(entity.liquidFilterWhitelist);

        this.checkBox.update(entity.useEnergyLimit);
        this.leftArrow.setActive(entity.useEnergyLimit);
        this.rightArrow.setActive(entity.useEnergyLimit);
    }

    @Override
    protected String getBackground(){
        return "ultimate_screen.png";
    }

    @Override
    protected void drawText(MatrixStack poseStack, TrashCanBlockEntity entity){
        ScreenUtils.drawString(poseStack, TextComponents.translation("trashcans.gui.ultimate_trash_can.item_filter").get(), 8, 53);

        ScreenUtils.drawString(poseStack, TextComponents.translation("trashcans.gui.ultimate_trash_can.liquid_filter").get(), 8, 83);

        ScreenUtils.drawString(poseStack, TextComponents.translation("trashcans.gui.ultimate_trash_can.energy_limit").get(), 8, 113);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.string(I18n.get("trashcans.gui.energy_trash_can.value").replace("$number$", "" + entity.energyLimit)).get(), 114, 132);
    }

    @Override
    protected boolean keyPressed(int keyCode, boolean hasBeenHandled, TrashCanBlockEntity object){
        if(keyCode == 340)
            this.shift = true;
        else if(keyCode == 341)
            this.control = true;
        return super.keyPressed(keyCode, hasBeenHandled, object);
    }

    @Override
    protected boolean keyReleased(int keyCode, boolean hasBeenHandled, TrashCanBlockEntity object){
        if(keyCode == 340)
            this.shift = false;
        else if(keyCode == 341)
            this.control = false;
        return super.keyReleased(keyCode, hasBeenHandled, object);
    }
}
