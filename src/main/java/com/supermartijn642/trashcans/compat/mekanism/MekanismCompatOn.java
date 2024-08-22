package com.supermartijn642.trashcans.compat.mekanism;

import com.supermartijn642.trashcans.TrashCansConfig;
import com.supermartijn642.trashcans.filter.ItemFilter;
import com.supermartijn642.trashcans.filter.LiquidTrashCanFilters;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.attribute.ChemicalAttributes;
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
        return Capabilities.CHEMICAL.item();
    }

    @Override
    public boolean doesItemHaveGasStored(ItemStack stack){
        IChemicalHandler handler = stack.getCapability(Capabilities.CHEMICAL.item());
        if(handler == null)
            return false;
        for(int i = 0; i < handler.getChemicalTanks(); i++)
            if(!handler.getChemicalInTank(i).isEmpty())
                return true;
        return false;
    }

    @Override
    public boolean drainGasFromItem(ItemStack stack){
        IChemicalHandler handler = stack.getCapability(Capabilities.CHEMICAL.item());
        if(handler == null)
            return false;
        boolean changed = false;
        for(int tank = 0; tank < handler.getChemicalTanks(); tank++)
            if(!handler.getChemicalInTank(tank).isEmpty()){
                handler.extractChemical(handler.getChemicalInTank(tank), Action.EXECUTE);
                changed = true;
            }
        return changed;
    }

    @Override
    public Object getGasHandler(ArrayList<ItemFilter> filters, Supplier<Boolean> whitelist){
        return new IChemicalHandler() {
            @Override
            public int getChemicalTanks(){
                return 1;
            }

            @Override
            public ChemicalStack getChemicalInTank(int i){
                return ChemicalStack.EMPTY;
            }

            @Override
            public void setChemicalInTank(int i, ChemicalStack gasStack){
            }

            @Override
            public long getChemicalTankCapacity(int i){
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isValid(int i, ChemicalStack gasStack){
                if(gasStack.has(ChemicalAttributes.Radiation.class) && !TrashCansConfig.allowVoidingNuclearWaste.get())
                    return false;

                for(ItemFilter filter : filters){
                    if(filter != null && filter.matches(gasStack))
                        return whitelist.get();
                }
                return !whitelist.get();
            }

            @Override
            public ChemicalStack insertChemical(int i, ChemicalStack gasStack, Action action){
                if(this.isValid(i, gasStack))
                    return ChemicalStack.EMPTY;
                return gasStack;
            }

            @Override
            public ChemicalStack extractChemical(int i, long l, Action action){
                return ChemicalStack.EMPTY;
            }
        };
    }

    @Override
    public boolean isGasStack(Object obj){
        return obj instanceof ChemicalStack;
    }

    @Override
    public ItemStack getChemicalTankForGasStack(Object gasStack){
        ItemStack stack = new ItemStack(MekanismBlocks.CREATIVE_CHEMICAL_TANK);
        IChemicalHandler handler = stack.getCapability(Capabilities.CHEMICAL.item());
        ChemicalStack gas = ((ChemicalStack)gasStack).copy();
        gas.setAmount(handler.getChemicalTankCapacity(0));
        handler.setChemicalInTank(0, gas);
        return stack;
    }
}
