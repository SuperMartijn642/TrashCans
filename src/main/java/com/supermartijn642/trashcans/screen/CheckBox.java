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
 * Created 7/7/2020 by SuperMartijn642
 */
public class CheckBox extends AbstractButtonWidget implements IHoverTextWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("trashcans", "textures/checkmarkbox.png");

    public boolean checked;

    public CheckBox(int x, int y, Runnable onPress){
        super(x, y, 17, 17, onPress);
    }

    public void update(boolean checked){
        this.checked = checked;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        ScreenUtils.bindTexture(BUTTONS);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ScreenUtils.drawTexture(matrixStack, this.x, this.y - 3, this.width + 3, this.height + 3, this.checked ? 0 : 0.5f, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
    }

    @Override
    protected Component getNarrationMessage(){
        return this.getHoverText();
    }

    @Override
    public Component getHoverText(){
        return new TranslatableComponent("trashcans.gui.energy_trash_can.check." + (this.checked ? "on" : "off"));
    }
}
