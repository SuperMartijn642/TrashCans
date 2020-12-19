package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
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
        if(!stack.hasCapability(Capabilities.GAS_HANDLER_CAPABILITY, null))
            return false;
        IGasHandler handler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null);
        if(handler == null)
            return false;
        GasTankInfo[] properties = handler.getTankInfo();
        if(properties == null)
            return false;
        for(GasTankInfo property : properties)
            if(property != null && property.getGas() != null && property.getStored() > 0 && handler.canDrawGas(null, property.getGas().getGas()))
                return true;
        return false;
    }

    @Override
    public boolean drainGasFromItem(ItemStack stack){
        IGasHandler handler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null);
        if(handler != null){
            GasTankInfo[] properties = handler.getTankInfo();
            if(properties != null){
                boolean changed = false;
                for(GasTankInfo property : properties){
                    if(property.getGas() != null && property.getStored() > 0 && handler.canDrawGas(null, property.getGas().getGas())){
                        handler.drawGas(null, property.getStored(), true);
                        changed = true;
                    }
                }
                return changed;
            }
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
}
