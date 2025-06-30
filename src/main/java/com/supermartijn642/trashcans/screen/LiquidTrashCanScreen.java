package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class LiquidTrashCanScreen extends TrashCanScreen<LiquidTrashCanContainer> {

    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("trashcans", "liquid_screen");

    private WhitelistButton whitelistButton;

    public LiquidTrashCanScreen(){
        super("trashcans.gui.liquid_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.whitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 118, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.getBlockEntityPos()))));
        this.whitelistButton.update(entity.liquidFilterWhitelist);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.whitelistButton.update(entity.liquidFilterWhitelist);
    }

    @Override
    protected ResourceLocation getBackground(){
        return BACKGROUND;
    }

    @Override
    protected void drawText(GuiGraphicsHelper graphics, TrashCanBlockEntity entity){
        graphics.submitText(TextComponents.translation("trashcans.gui.liquid_trash_can.filter").get(), 8, 52);
    }
}
