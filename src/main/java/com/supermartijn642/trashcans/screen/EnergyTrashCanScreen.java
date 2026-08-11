package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import com.supermartijn642.trashcans.screen.components.ArrowButton;
import com.supermartijn642.trashcans.screen.components.CheckBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanScreen extends TrashCanScreen<EnergyTrashCanContainer> {

    public static final ResourceLocation BACKGROUND = TrashCans.identifier("energy_screen");

    private CheckBox checkBox;
    private ArrowButton leftArrow, rightArrow;

    private boolean shift, control;

    public EnergyTrashCanScreen(){
        super("trashcans.gui.energy_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.checkBox = this.addWidget(new CheckBox(21, 66, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.getBlockEntityPos()))));
        this.checkBox.update(entity.isEnergyLimited());
        this.leftArrow = this.addWidget(new ArrowButton(49, 66, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? -1 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.setActive(entity.isEnergyLimited());
        this.rightArrow = this.addWidget(new ArrowButton(170, 66, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.setActive(entity.isEnergyLimited());
    }

    @Override
    protected void renderTooltips(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderTooltips(context, graphics, mouseX, mouseY, entity);
        if(this.leftArrow.isFocused() && this.leftArrow.isActive())
            graphics.submitTooltip(c -> c.text(
                    TextComponents.string("-" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change1", "-100").color(ChatFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change2", "-10000").color(ChatFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change3", "-1").color(ChatFormatting.AQUA).get()),
                mouseX, mouseY);
        if(this.rightArrow.isFocused() && this.rightArrow.isActive())
            graphics.submitTooltip(c -> c.text(
                    TextComponents.string("+" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change1", "+100").color(ChatFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change2", "+10000").color(ChatFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change3", "+1").color(ChatFormatting.AQUA).get()),
                mouseX, mouseY);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
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
        graphics.submitText(TextComponents.translation("trashcans.gui.energy_trash_can.limit").get(), 8, 52);
        //noinspection Convert2MethodRef
        graphics.submitText(TextComponents.string(I18n.get("trashcans.gui.energy_trash_can.value").replace("$number$", "" + entity.getEnergyLimit())).get(), 114, 71, p -> p.centerHorizontally());
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
