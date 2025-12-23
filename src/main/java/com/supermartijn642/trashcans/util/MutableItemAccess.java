package com.supermartijn642.trashcans.util;

import com.supermartijn642.core.util.Pair;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created 23/12/2025 by SuperMartijn642
 */
public class MutableItemAccess extends SnapshotJournal<Pair<ItemResource,Integer>> implements ItemAccess {

    private ItemResource resource;
    private int amount;

    public void setStack(ItemStack stack){
        this.resource = ItemResource.of(stack);
        this.amount = stack.getCount();
    }

    public boolean matches(ItemStack stack){
        return this.amount == stack.getCount() && this.resource.matches(stack);
    }

    public ItemStack getStack(){
        return this.resource.toStack(this.amount);
    }

    @Override
    public ItemResource getResource(){
        return this.resource;
    }

    @Override
    public int getAmount(){
        return this.amount;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction){
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if(!this.resource.isEmpty() && !this.resource.equals(resource))
            return 0;
        int inserted = Math.max(0, Math.min(this.resource.getMaxStackSize() - this.amount, amount));
        if(inserted == 0)
            return 0;
        this.updateSnapshots(transaction);
        this.resource = resource;
        this.amount += inserted;
        return inserted;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction){
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if(this.resource.isEmpty() || !this.resource.equals(resource))
            return 0;
        int extracted = Math.min(this.amount, amount);
        if(extracted == 0)
            return 0;
        this.updateSnapshots(transaction);
        this.amount -= extracted;
        if(this.amount == 0)
            this.resource = ItemResource.EMPTY;
        return extracted;
    }

    @Override
    protected Pair<ItemResource,Integer> createSnapshot(){
        return Pair.of(this.resource, this.amount);
    }

    @Override
    protected void revertToSnapshot(Pair<ItemResource,Integer> snapshot){
        assert snapshot != null;
        this.resource = snapshot.left();
        this.amount = snapshot.right();
    }
}
