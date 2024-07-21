package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.TrashCansConfig;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

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
    public ItemCapability<?,Void> getGasHandlerCapability(){
        return Capabilities.GAS.item();
    }

    @Override
    public boolean doesItemHaveGasStored(ItemStack stack){
        IGasHandler handler = stack.getCapability(Capabilities.GAS.item());
        if(handler == null)
            return false;
        for(int i = 0; i < handler.getTanks(); i++)
            if(!handler.getChemicalInTank(i).isEmpty())
                return true;
        return false;
    }

    @Override
    public boolean drainGasFromItem(ItemStack stack){
        IGasHandler handler = stack.getCapability(Capabilities.GAS.item());
        if(handler == null)
            return false;
        boolean changed = false;
        for(int tank = 0; tank < handler.getTanks(); tank++)
            if(!handler.getChemicalInTank(tank).isEmpty()){
                handler.extractChemical(handler.getChemicalInTank(tank), Action.EXECUTE);
                changed = true;
            }
        return changed;
    }

    @Override
    public Object getGasHandler(ArrayList<ItemFilter> filters, Supplier<Boolean> whitelist){
        return new IGasHandler() {
            @Override
            public int getTanks(){
                return 1;
            }

            @Override
            public GasStack getChemicalInTank(int i){
                return GasStack.EMPTY;
            }

            @Override
            public void setChemicalInTank(int i, GasStack gasStack){
            }

            @Override
            public long getTankCapacity(int i){
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isValid(int i, GasStack gasStack){
                if(gasStack.has(GasAttributes.Radiation.class) && !TrashCansConfig.allowVoidingNuclearWaste.get())
                    return false;

                for(ItemFilter filter : filters){
                    if(filter != null && filter.matches(gasStack))
                        return whitelist.get();
                }
                return !whitelist.get();
            }

            @Override
            public GasStack insertChemical(int i, GasStack gasStack, Action action){
                if(this.isValid(i, gasStack))
                    return GasStack.EMPTY;
                return gasStack;
            }

            @Override
            public GasStack extractChemical(int i, long l, Action action){
                return GasStack.EMPTY;
            }
        };
    }

    @Override
    public boolean isGasStack(Object obj){
        return obj instanceof GasStack;
    }

    @Override
    public ItemStack getChemicalTankForGasStack(Object gasStack){
        ItemStack stack = new ItemStack(MekanismBlocks.CREATIVE_CHEMICAL_TANK);
        IGasHandler handler = stack.getCapability(Capabilities.GAS.item());
        GasStack gas = ((GasStack)gasStack).copy();
        gas.setAmount(handler.getTankCapacity(0));
        handler.setChemicalInTank(0, gas);
        return stack;
    }
}
