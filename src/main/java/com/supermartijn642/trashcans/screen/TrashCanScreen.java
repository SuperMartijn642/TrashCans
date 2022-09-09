package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.ObjectBaseContainerWidget;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 7/11/2020 by SuperMartijn642
 */
public abstract class TrashCanScreen<T extends TrashCanContainer> extends ObjectBaseContainerWidget<TrashCanBlockEntity,T> {

    private final ITextComponent title;

    public TrashCanScreen(String title){
        super(0, 0, 1, 1);
        this.title = TextComponents.translation(title).get();
    }

    @Override
    protected ITextComponent getNarrationMessage(TrashCanBlockEntity object){
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
    protected void renderBackground(int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderBackground(mouseX, mouseY, entity);
        ScreenUtils.bindTexture(new ResourceLocation("trashcans", "textures/" + this.getBackground()));
        ScreenUtils.drawTexture(0, 0, this.width(), this.height());
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, TrashCanBlockEntity entity){
        super.renderForeground(mouseX, mouseY, entity);
        ScreenUtils.drawCenteredString(this.title, this.width() / 2f, 6);
        ScreenUtils.drawString(ClientUtils.getPlayer().inventory.getName(), 21, this.height() - 94);

        this.drawText(entity);
    }

    protected abstract void drawText(TrashCanBlockEntity entity);
}
