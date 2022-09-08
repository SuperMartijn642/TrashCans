package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class ArrowButton extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("trashcans", "textures/arrow_buttons.png");

    private final boolean left;
    private boolean active = true;

    public ArrowButton(int x, int y, boolean left, Runnable onPress){
        super(x, y, 11, 17, onPress);
        this.left = left;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return this.active;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.bindTexture(BUTTONS);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ScreenUtils.drawTexture(poseStack, this.x, this.y, this.width, this.height, this.left ? 0.5f : 0, (this.active ? this.isFocused() ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
    }

    @Override
    public Component getNarrationMessage(){
        return TextComponents.translation("trashcans.gui.arrow." + (this.left ? "left" : "right")).get();
    }
}
