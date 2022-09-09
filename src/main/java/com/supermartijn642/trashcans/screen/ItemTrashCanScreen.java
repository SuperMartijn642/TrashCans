package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanScreen extends TrashCanScreen<ItemTrashCanContainer> {

    private WhitelistButton whitelistButton;

    public ItemTrashCanScreen(){
        super("trashcans.gui.item_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.getBlockEntityPos()))));
        this.whitelistButton.update(entity.itemFilterWhitelist);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.whitelistButton.update(entity.itemFilterWhitelist);
    }

    @Override
    protected String getBackground(){
        return "item_screen.png";
    }

    @Override
    protected void drawText(MatrixStack poseStack, TrashCanBlockEntity entity){
        ScreenUtils.drawString(poseStack, TextComponents.translation("trashcans.gui.item_trash_can.filter").get(), 8, 52);
    }
}
