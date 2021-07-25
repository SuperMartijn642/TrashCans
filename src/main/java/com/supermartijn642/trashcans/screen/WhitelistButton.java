package com.supermartijn642.trashcans.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.IHoverTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class WhitelistButton extends AbstractButtonWidget implements IHoverTextWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("trashcans", "textures/blacklist_button.png");

    public boolean white = true;

    public WhitelistButton(int x, int y, Runnable onPress){
        super(x, y, 20, 20, onPress);
    }

    public void update(boolean white){
        this.white = white;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        ScreenUtils.bindTexture(BUTTONS);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ScreenUtils.drawTexture(matrixStack, this.x, this.y, this.width, this.height, this.white ? 0 : 0.5f, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
    }

    @Override
    protected Component getNarrationMessage(){
        return this.getHoverText();
    }

    @Override
    public Component getHoverText(){
        return new TranslatableComponent("trashcans.gui.whitelist." + (this.white ? "on" : "off"));
    }
}
