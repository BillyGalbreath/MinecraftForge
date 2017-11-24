package net.pl3x.forge.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BannerSlotChangedEvent extends Event {
    private final ContainerPlayer container;
    private final Slot slot;
    private final EntityPlayer player;

    public BannerSlotChangedEvent(ContainerPlayer container, Slot slot, EntityPlayer player) {
        this.container = container;
        this.slot = slot;
        this.player = player;
    }

    public ContainerPlayer getContainer() {
        return container;
    }

    public Slot getSlot() {
        return slot;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return slot.getStack();
    }
}
