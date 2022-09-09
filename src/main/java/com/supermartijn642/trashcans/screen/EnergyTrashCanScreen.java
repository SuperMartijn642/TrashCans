package com.supermartijn642.trashcans.screen;

import com.google.common.collect.Lists;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import com.supermartijn642.trashcans.packet.PacketChangeEnergyLimit;
import com.supermartijn642.trashcans.packet.PacketToggleEnergyLimit;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public class EnergyTrashCanScreen extends TrashCanScreen<EnergyTrashCanContainer> {

    private CheckBox checkBox;
    private ArrowButton leftArrow, rightArrow;

    private boolean shift, control;

    public EnergyTrashCanScreen(){
        super("trashcans.gui.energy_trash_can.title");
    }

    @Override
    protected void addWidgets(TrashCanBlockEntity entity){
        this.checkBox = this.addWidget(new CheckBox(21, 66, () -> TrashCans.CHANNEL.sendToServer(new PacketToggleEnergyLimit(this.container.getBlockEntityPos()))));
        this.checkBox.update(entity.useEnergyLimit);
        this.leftArrow = this.addWidget(new ArrowButton(49, 66, true, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? -1 : -100 : this.control ? -10000 : -1000))));
        this.leftArrow.setActive(entity.useEnergyLimit);
        this.rightArrow = this.addWidget(new ArrowButton(170, 66, false, () -> TrashCans.CHANNEL.sendToServer(new PacketChangeEnergyLimit(this.container.getBlockEntityPos(), this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000))));
        this.rightArrow.setActive(entity.useEnergyLimit);
    }

    @Override
    protected void renderTooltips(int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderTooltips(mouseX, mouseY, entity);
        if(this.leftArrow.isFocused() && this.leftArrow.isActive())
            ScreenUtils.drawTooltip(Lists.newArrayList(
                    TextComponents.string("-" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change1", "-100").color(TextFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change2", "-10000").color(TextFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change3", "-1").color(TextFormatting.AQUA).get()),
                mouseX, mouseY);
        if(this.rightArrow.isFocused() && this.rightArrow.isActive())
            ScreenUtils.drawTooltip(Lists.newArrayList(
                    TextComponents.string("+" + (this.shift ? this.control ? 1 : 100 : this.control ? 10000 : 1000)).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change1", "+100").color(TextFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change2", "+10000").color(TextFormatting.AQUA).get(),
                    TextComponents.translation("trashcans.gui.energy_trash_can.limit.change3", "+1").color(TextFormatting.AQUA).get()),
                mouseX, mouseY);
    }

    @Override
    protected void update(TrashCanBlockEntity entity){
        super.update(entity);
        this.checkBox.update(entity.useEnergyLimit);
        this.leftArrow.setActive(entity.useEnergyLimit);
        this.rightArrow.setActive(entity.useEnergyLimit);
    }

    @Override
    protected String getBackground(){
        return "energy_screen.png";
    }

    @Override
    protected void drawText(TrashCanBlockEntity entity){
        ScreenUtils.drawString(TextComponents.translation("trashcans.gui.energy_trash_can.limit").get(), 8, 52);
        ScreenUtils.drawCenteredString(TextComponents.string(I18n.get("trashcans.gui.energy_trash_can.value").replace("$number$", "" + entity.energyLimit)).get(), 114, 71);
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
