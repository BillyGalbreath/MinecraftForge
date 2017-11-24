package net.pl3x.forge.event;

import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BannerSlotChangedEvent extends Event {
    private final ContainerPlayer container;
    private final Slot slot;

    public BannerSlotChangedEvent(ContainerPlayer container, Slot slot) {
        this.container = container;
        this.slot = slot;
    }

    public ContainerPlayer getContainer() {
        return container;
    }

    public Slot getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return slot.getStack();
    }
}
