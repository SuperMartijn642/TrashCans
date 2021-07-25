package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanTile;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanScreen extends TrashCanScreen<ItemTrashCanContainer> {

    private WhitelistButton whitelistButton;

    public ItemTrashCanScreen(ItemTrashCanContainer container){
        super(container, "trashcans.gui.item_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanTile tile){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.sizeY() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.menu.getTilePos()))));
        this.whitelistButton.update(tile.itemFilterWhitelist);
    }

    @Override
    protected void renderTooltips(PoseStack matrixStack, int mouseX, int mouseY, TrashCanTile tile){
    }

    @Override
    protected void containerTick(TrashCanTile tile){
        this.whitelistButton.update(tile.itemFilterWhitelist);
    }

    @Override
    protected String getBackground(){
        return "item_screen.png";
    }

    @Override
    protected void drawText(PoseStack matrixStack, TrashCanTile tile){
        ScreenUtils.drawString(matrixStack, new TranslatableComponent("trashcans.gui.item_trash_can.filter"), 8, 52);
    }
}
