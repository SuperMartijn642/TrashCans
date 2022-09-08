package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class WhitelistButton extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("trashcans", "textures/blacklist_button.png");

    public boolean white = true;
    private boolean active = true;

    public WhitelistButton(int x, int y, Runnable onPress){
        super(x, y, 20, 20, onPress);
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return this.active;
    }

    public void update(boolean white){
        this.white = white;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.bindTexture(BUTTONS);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ScreenUtils.drawTexture(poseStack, this.x, this.y, this.width, this.height, this.white ? 0 : 0.5f, (this.active ? this.isFocused() ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
    }

    @Override
    public Component getNarrationMessage(){
        Holder<Component> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    protected void getTooltips(Consumer<Component> tooltips){
        tooltips.accept(TextComponents.translation("trashcans.gui.whitelist." + (this.white ? "on" : "off")).get());
    }
}
