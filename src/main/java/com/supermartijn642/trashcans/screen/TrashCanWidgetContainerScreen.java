package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.trashcans.screen.components.DeletedItemsList;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCanWidgetContainerScreen extends WidgetContainerScreen<TrashCanScreen<?>,TrashCanContainer> {

    private final boolean addExtraWidth;

    public TrashCanWidgetContainerScreen(TrashCanScreen<?> widget, TrashCanContainer container, boolean addExtraWidth){
        super(widget, container, false);
        this.addExtraWidth = addExtraWidth;
    }

    @Override
    protected void containerTick(){
        if(this.addExtraWidth){
            if(DeletedItemsList.expanded)
                this.imageWidth = this.widget.width() + 26;
            else
                this.imageWidth = this.widget.width();
        }
        super.containerTick();
    }

    public int left(){
        return this.leftPos;
    }

    public int top(){
        return this.topPos;
    }

    public TrashCanContainer getContainer(){
        return this.container;
    }
}
