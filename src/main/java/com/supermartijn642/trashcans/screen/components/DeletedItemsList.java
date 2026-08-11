package com.supermartijn642.trashcans.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.Widget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.premade.ScissorWidget;
import com.supermartijn642.trashcans.TrashCans;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 11/08/2026 by SuperMartijn642
 */
public class DeletedItemsList extends BaseWidget {

    private static final ResourceLocation TOGGLE_TAB = TrashCans.identifier("textures/toggle_tab.png");
    private static final ResourceLocation SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath("supermartijn642corelib", "textures/gui/slot.png");

    private static boolean expanded;

    private final List<CustomSlot> slots;
    private final Supplier<List<ItemStack>> items;
    private SlotsBar slotsBar;
    private Toggle toggle;
    private float expansionProgress = expanded ? 1 : 0;

    public DeletedItemsList(int x, int y, List<CustomSlot> slots, Supplier<List<ItemStack>> items){
        super(x, y, 34, slots.size() * 18 + 8);
        this.slots = slots;
        this.items = items;
    }

    @Override
    public Component getNarrationMessage(){
        return null;
    }

    @Override
    protected void addWidgets(){
        List<Widget> widgets = new ArrayList<>();
        widgets.add(this.slotsBar = new SlotsBar(this.x, this.y));
        widgets.add(this.toggle = new Toggle(this.x, this.y + 4));
        this.addWidget(ScissorWidget.create(this.x, this.y, this.width, this.height, widgets.toArray(Widget[]::new)));
    }

    private int slotCount(){
        return Math.min(this.slots.size(), this.items.get().size());
    }

    private float getOffset(){
        return -(1 - this.expansionProgress) * 26;
    }

    @Override
    public void renderBackground(WidgetRenderContext context, int mouseX, int mouseY){
        if(this.items.get().isEmpty()){
            expanded = false;
            this.expansionProgress = 0;
        }
        if(expanded){
            if(this.expansionProgress < 1)
                this.expansionProgress = Math.min(1, this.expansionProgress + 0.15f * context.partialTicks());
        }else{
            if(this.expansionProgress > 0)
                this.expansionProgress = Math.max(0, this.expansionProgress - 0.15f * context.partialTicks());
        }
        super.renderBackground(context, mouseX, mouseY);
    }

    @Override
    public void update(){
        if(this.expansionProgress == 1){
            List<ItemStack> items = this.items.get();
            for(int i = 0; i < this.slots.size(); i++){
                CustomSlot slot = this.slots.get(i);
                if(items.size() > i){
                    slot.setActive(true);
                    slot.move(this.x + 5, this.y + 5 + i * 18);
                }else
                    slot.setActive(false);
            }
        }else{
            for(CustomSlot slot : this.slots)
                slot.setActive(false);
        }
        super.update();
    }

    private class SlotsBar extends BaseWidget {

        public SlotsBar(int x, int y){
            super(x, y, 26, 26);
        }

        @Override
        public Component getNarrationMessage(){
            return null;
        }

        private float x(){
            return DeletedItemsList.this.x + DeletedItemsList.this.getOffset();
        }

        @Override
        public void update(){
            super.update();
            this.x = (int)Math.floor(this.x());
            int slotCount = DeletedItemsList.this.slotCount();
            this.height = slotCount * 18 + 8;
        }

        @Override
        public void renderBackground(WidgetRenderContext context, int mouseX, int mouseY){
            super.renderBackground(context, mouseX, mouseY);
            int slots = DeletedItemsList.this.slotCount();
            float x = this.x();
            ScreenUtils.drawScreenBackground(context.poseStack(), x, this.y, this.width, this.height);
            for(int i = 0; i < slots; i++)
                ScreenUtils.drawTexture(SLOT_TEXTURE, context.poseStack(), x + 4, this.y + 4 + i * 18, 18, 18);
        }

        @Override
        public void render(WidgetRenderContext context, int mouseX, int mouseY){
            super.render(context, mouseX, mouseY);
            if(DeletedItemsList.this.expansionProgress != 0){
                float x = this.x();
                PoseStack poseStack = context.poseStack();
                poseStack.pushPose();
                poseStack.translate(x, 0, 0);
                int slotCount = DeletedItemsList.this.slotCount();
                for(int i = 0; i < slotCount; i++){
                    CustomSlot slot = DeletedItemsList.this.slots.get(i);
                    if(!slot.isActive()){
                        ItemStack stack = slot.getItem();
                        if(!stack.isEmpty())
                            ScreenUtils.drawItem(poseStack, stack, null, 5, this.y + 5 + i * 18);
                    }
                }
                poseStack.popPose();
            }
        }
    }

    private class Toggle extends AbstractButtonWidget {
        public Toggle(int x, int y){
            super(x, y, 7, 9, () -> expanded = !expanded);
        }

        @Override
        protected boolean isClickable(){
            return !DeletedItemsList.this.items.get().isEmpty();
        }

        @Override
        public Component getNarrationMessage(){
            return expanded ?
                TextComponents.translation("trashcans.gui.item_trash_can.deleted_items.hide").get() :
                TextComponents.translation("trashcans.gui.item_trash_can.deleted_items.show").get();
        }

        @Override
        protected void getTooltips(Consumer<Component> tooltips){
            if(this.isClickable())
                tooltips.accept(this.getNarrationMessage());
        }

        private float x(){
            return DeletedItemsList.this.x + DeletedItemsList.this.slotsBar.width() + DeletedItemsList.this.getOffset();
        }

        @Override
        public void update(){
            super.update();
            this.x = (int)Math.floor(this.x());
        }

        @Override
        public void renderBackground(WidgetRenderContext context, int mouseX, int mouseY){
            super.renderBackground(context, mouseX, mouseY);
            if(this.isClickable()){
                PoseStack poseStack = context.poseStack();
                poseStack.pushPose();
                poseStack.translate(this.x(), 0, 0);
                ScreenUtils.drawTexture(TOGGLE_TAB, poseStack, 0, this.y, this.width, this.height, expanded ? 0.5f : 0, this.isFocused() ? 1 / 3f : 0, 0.5f, 1 / 3f);
                poseStack.popPose();
            }
        }
    }
}
