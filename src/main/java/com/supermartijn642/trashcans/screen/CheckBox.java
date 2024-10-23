package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
public class CheckBox extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = ResourceLocation.fromNamespaceAndPath("trashcans", "textures/checkmarkbox.png");

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
    public void render(WidgetRenderContext context, int mouseX, int mouseY){
        ScreenUtils.drawTexture(BUTTONS, context.poseStack(), this.x, this.y - 3, this.width + 3, this.height + 3, this.checked ? 0 : 0.5f, (this.active ? this.isFocused() ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
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
