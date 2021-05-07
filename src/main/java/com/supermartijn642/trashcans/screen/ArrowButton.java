package com.supermartijn642.trashcans.screen;

import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class ArrowButton extends AbstractButtonWidget {

    private final ResourceLocation BUTTONS = new ResourceLocation("trashcans", "textures/arrow_buttons.png");

    private final boolean left;

    public ArrowButton(int x, int y, boolean left, Runnable onPress){
        super(x, y, 11, 17, onPress);
        this.left = left;
    }

    @Override
    public void render(int i, int i1, float v){
        ScreenUtils.bindTexture(BUTTONS);
        GlStateManager.color(1, 1, 1, 1);
        ScreenUtils.drawTexture(this.x, this.y, this.width, this.height, this.left ? 0.5f : 0, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 0.5f, 1 / 3f);
    }

    @Override
    protected ITextComponent getNarrationMessage(){
        return new TextComponentTranslation("trashcans.gui.arrow." + (this.left ? "left" : "right"));
    }
}
