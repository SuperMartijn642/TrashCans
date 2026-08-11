package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.TrashCansConfig;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.screen.components.DeletedItemsList;
import com.supermartijn642.trashcans.screen.components.WhitelistButton;
import net.minecraft.util.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanScreen extends TrashCanScreen<ItemTrashCanContainer> {

    private static final ResourceLocation BACKGROUND = TrashCans.identifier("textures/item_screen.png");

    private WhitelistButton whitelistButton;

    public ItemTrashCanScreen(){
        super("trashcans.gui.item_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.getBlockEntityPos()))));
        this.whitelistButton.update(entity.isItemFilterWhitelist());
        if(TrashCansConfig.retrieveDeletedItems.get())
            this.addWidget(new DeletedItemsList(this.width(), 0, this.container.deletedItemSlots, () -> this.object.getDeletedItems(), () -> this.screen));
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.whitelistButton.update(entity.isItemFilterWhitelist());
    }

    @Override
    protected ResourceLocation getBackground(){
        return BACKGROUND;
    }

    @Override
    protected void drawText(TrashCanBlockEntity entity){
        ScreenUtils.drawString(TextComponents.translation("trashcans.gui.item_trash_can.filter").get(), 8, 52);
    }
}
