package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.WidgetContainerScreen;

/**
 * Created 08/09/2022 by SuperMartijn642
 */
public class TrashCanWidgetContainerScreen extends WidgetContainerScreen<TrashCanScreen<?>,TrashCanContainer> {

    // Need this class just for the JEI GhostIngredientHandler to target
    public TrashCanWidgetContainerScreen(TrashCanScreen<?> widget, TrashCanContainer container, boolean drawSlots){
        super(widget, container, drawSlots);
    }

    public TrashCanScreen<?> getWidget(){
        return this.widget;
    }

    public TrashCanContainer getContainer(){
        return this.container;
    }
}
