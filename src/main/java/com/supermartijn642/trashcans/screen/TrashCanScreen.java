package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.ObjectBaseContainerWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends ObjectBaseContainerWidget<TrashCanBlockEntity,T> {

    private final ResourceLocation background = ResourceLocation.fromNamespaceAndPath("trashcans", "textures/" + this.getBackground());
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

    protected abstract String getBackground();

    @Override
    protected void renderBackground(WidgetRenderContext context, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderBackground(context, mouseX, mouseY, entity);
        ScreenUtils.drawTexture(this.background, context.poseStack(), 0, 0, this.width(), this.height());
    }

    @Override
    protected void renderForeground(WidgetRenderContext context, int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderForeground(context, mouseX, mouseY, entity);
        ScreenUtils.drawCenteredString(context.poseStack(), this.title, this.width() / 2f, 6);
        ScreenUtils.drawString(context.poseStack(), ClientUtils.getPlayer().getInventory().getName(), 21, this.height() - 94);

        this.drawText(context.poseStack(), entity);
    }

    protected abstract void drawText(PoseStack poseStack, TrashCanBlockEntity entity);
}
