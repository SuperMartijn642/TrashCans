package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.TrashCansConfig;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.screen.components.DeletedItemsList;
import com.supermartijn642.trashcans.screen.components.WhitelistButton;
import net.minecraft.resources.Identifier;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class ItemTrashCanScreen extends TrashCanScreen<ItemTrashCanContainer> {

    public static final Identifier BACKGROUND = TrashCans.identifier("item_screen");

    private WhitelistButton whitelistButton;

    public ItemTrashCanScreen(){
        super("trashcans.gui.item_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleItemWhitelist(this.container.getBlockEntityPos()))));
        this.whitelistButton.update(entity.isItemFilterWhitelist());
        if(TrashCansConfig.retrieveDeletedItems.get())
            this.addWidget(new DeletedItemsList(this.width(), 0, this.container.deletedItemSlots, () -> this.object.getDeletedItems()));
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.whitelistButton.update(entity.isItemFilterWhitelist());
    }

    @Override
    protected Identifier getBackground(){
        return BACKGROUND;
    }

    @Override
    protected void drawText(GuiGraphicsHelper graphics, TrashCanBlockEntity entity){
        graphics.submitText(TextComponents.translation("trashcans.gui.item_trash_can.filter").get(), 8, 52);
    }
}
