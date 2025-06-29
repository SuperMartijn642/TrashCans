package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.ObjectBaseContainerWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends ObjectBaseContainerWidget<TrashCanBlockEntity,T> {

    private final Component title;

    public TrashCanScreen(String title){
        super(0, 0, 1, 1);
        this.title = TextComponents.translation(title).get();
    }

    @Override
    protected Component getNarrationMessage(TrashCanBlockEntity object){
        return this.title;
    }

    @Override
    protected TrashCanBlockEntity getObject(TrashCanBlockEntity oldObject){
        return this.container.getBlockEntity();
    }

    @Override
    protected boolean validateObject(TrashCanBlockEntity object){
        return object != null && !object.isRemoved();
    }

    @Override
    protected int width(TrashCanBlockEntity object){
        return this.container.width;
    }

    @Override
    protected int height(TrashCanBlockEntity object){
        return this.container.height;
    }

    protected abstract ResourceLocation getBackground();

    @Override
    protected void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderBackground(context, graphics, mouseX, mouseY, entity);
        graphics.submitSprite(this.getBackground(), 0, 0, this.width(), this.height());
    }

    @Override
    protected void renderForeground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderForeground(context, graphics, mouseX, mouseY, entity);
        //noinspection Convert2MethodRef
        graphics.submitText(this.title, this.width() / 2f, 6, p -> p.centerHorizontally());
        graphics.submitText(ClientUtils.getPlayer().getInventory().getName(), 21, this.height() - 94);

        this.drawText(graphics, entity);
    }

    protected abstract void drawText(GuiGraphicsHelper graphics, TrashCanBlockEntity entity);
}
