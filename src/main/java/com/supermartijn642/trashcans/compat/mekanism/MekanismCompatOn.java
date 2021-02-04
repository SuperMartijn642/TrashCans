package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import mekanism.api.gas.*;
import mekanism.common.MekanismBlocks;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.item.ItemBlockGasTank;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created 12/19/2020 by SuperMartijn642
 */
public class MekanismCompatOn extends MekanismCompatOff {

    public MekanismCompatOn(){
        LiquidTrashCanFilters.register(new GasFilterManager(), "gas");
    }

    @Override
    public boolean isInstalled(){
        return true;
    }

    @Override
    public Capability<?> getGasHandlerCapability(){
        return Capabilities.GAS_HANDLER_CAPABILITY;
    }

    @Override
    public boolean doesItemHaveGasStored(ItemStack stack){
        if(!(stack.getItem() instanceof IGasItem))
            return false;
        IGasItem item = (IGasItem)stack.getItem();
        return item.getGas(stack) != null && item.getGas(stack).amount > 0;
    }

    @Override
    public boolean drainGasFromItem(ItemStack stack){
        if(stack.getItem() instanceof IGasItem){
            IGasItem item = (IGasItem)stack.getItem();
            item.setGas(stack, null);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R getGasHandler(ArrayList<ItemFilter> filters, Supplier<Boolean> whitelist){
        return (R)new IGasHandler() {
            @Override
            public int receiveGas(EnumFacing enumFacing, GasStack gasStack, boolean b){
                return gasStack.amount;
            }

            @Override
            public GasStack drawGas(EnumFacing enumFacing, int i, boolean b){
                return null;
            }

            @Override
            public boolean canReceiveGas(EnumFacing enumFacing, Gas gas){
                return true;
            }

            @Override
            public boolean canDrawGas(EnumFacing enumFacing, Gas gas){
                return false;
            }

            @Override
            public GasTankInfo[] getTankInfo(){
                return new GasTankInfo[]{new GasTankInfo() {
                    @Override
                    public GasStack getGas(){
                        return null;
                    }

                    @Override
                    public int getStored(){
                        return 0;
                    }

                    @Override
                    public int getMaxGas(){
                        return Integer.MAX_VALUE;
                    }
                }};
            }
        };
    }

    @Override
    public boolean isGasStack(Object obj){
        return obj instanceof GasStack;
    }

    @Override
    public ItemStack getChemicalTankForGasStack(Object gasStack){
        ItemStack stack = new ItemStack(MekanismBlocks.GasTank);
        if(stack.getItem() instanceof IGasItem){
            IGasItem gasTank = ((ItemBlockGasTank)stack.getItem());
            GasStack gas = ((GasStack)gasStack).copy();
            gas.amount = gasTank.getMaxGas(stack);
            gasTank.setGas(stack, gas);
        }
        return stack;
    }
}
