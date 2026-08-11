package com.supermartijn642.trashcans.screen.components;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
public class CheckBox extends AbstractButtonWidget {

    public static final Identifier BUTTONS = TrashCans.identifier("checkmarkbox");

    public boolean checked;
    private boolean active = true;

    public CheckBox(int x, int y, Runnable onPress){
        super(x, y, 17, 17, onPress);
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return this.active;
    }

    public void update(boolean checked){
        this.checked = checked;
    }

    @Override
    public void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        graphics.submitSprite(BUTTONS, this.x, this.y - 3, this.width + 3, this.height + 3, p -> p.uv(this.checked ? 0 : 0.5f, (this.active ? this.isFocused() ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f));
    }

    @Override
    public Component getNarrationMessage(){
        Holder<Component> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    protected void getTooltips(Consumer<Component> tooltips){
        tooltips.accept(TextComponents.translation("trashcans.gui.energy_trash_can.check." + (this.checked ? "on" : "off")).get());
    }
}
