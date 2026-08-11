package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleItemWhitelist;
import com.supermartijn642.trashcans.packet.PacketToggleLiquidWhitelist;
import com.supermartijn642.trashcans.screen.components.ArrowButton;
import com.supermartijn642.trashcans.screen.components.CheckBox;
import com.supermartijn642.trashcans.screen.components.WhitelistButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class UltimateTrashCanScreen extends TrashCanScreen<UltimateTrashCanContainer> {

    public static final ResourceLocation BACKGROUND = TrashCans.identifier("ultimate_screen");

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
        this.itemWhitelistButton.update(entity.isItemFilterWhitelist());

        this.liquidWhitelistButton = this.addWidget(new WhitelistButton(175, this.height() - 155, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleLiquidWhitelist(this.container.getBlockEntityPos()))));
        this.liquidWhitelistButton.update(entity.isFluidFilterWhitelist());

        this.checkBox = this.addWidget(new CheckBox(21, 127, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.getBlockEntityPos()))));
        this.checkBox.update(entity.isEnergyLimited());
        this.leftArrow = this.addWidget(new ArrowButton(49, 127, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.setActive(entity.isEnergyLimited());
        this.rightArrow = this.addWidget(new ArrowButton(170, 127, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.setActive(entity.isEnergyLimited());
    }

    @Override
    protected void renderTooltips(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderTooltips(context, graphics, mouseX, mouseY, entity);
        if(this.leftArrow.isFocused() && this.leftArrow.isActive())
            graphics.submitTooltip(c -> c.literal("" + (this.shift ? this.control ? -100000 : -100 : this.control ? -10000 : -1000)), mouseX, mouseY);
        if(this.rightArrow.isFocused() && this.rightArrow.isActive())
            graphics.submitTooltip(c -> c.literal("+" + (this.shift ? this.control ? 100000 : 100 : this.control ? 10000 : 1000)), mouseX, mouseY);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.itemWhitelistButton.update(entity.isItemFilterWhitelist());

        this.liquidWhitelistButton.update(entity.isFluidFilterWhitelist());

        this.checkBox.update(entity.isEnergyLimited());
        this.leftArrow.setActive(entity.isEnergyLimited());
        this.rightArrow.setActive(entity.isEnergyLimited());
    }

    @Override
    protected ResourceLocation getBackground(){
        return BACKGROUND;
    }

    @Override
    protected void drawText(GuiGraphicsHelper graphics, TrashCanBlockEntity entity){
        graphics.submitText(TextComponents.translation("trashcans.gui.ultimate_trash_can.item_filter").get(), 8, 53);

        graphics.submitText(TextComponents.translation("trashcans.gui.ultimate_trash_can.liquid_filter").get(), 8, 83);

        graphics.submitText(TextComponents.translation("trashcans.gui.ultimate_trash_can.energy_limit").get(), 8, 113);
        //noinspection Convert2MethodRef
        graphics.submitText(TextComponents.string(I18n.get("trashcans.gui.energy_trash_can.value").replace("$number$", "" + entity.getEnergyLimit())).get(), 114, 132, p -> p.centerHorizontally());
    }

    @Override
    protected boolean keyPressed(KeyEvent event, boolean hasBeenHandled, TrashCanBlockEntity object){
        this.shift = event.hasShiftDown();
        this.control = event.hasControlDown();
        return super.keyPressed(event, hasBeenHandled, object);
    }

    @Override
    protected boolean keyReleased(KeyEvent event, boolean hasBeenHandled, TrashCanBlockEntity object){
        this.shift = event.hasShiftDown();
        this.control = event.hasControlDown();
        return super.keyReleased(event, hasBeenHandled, object);
    }
}
