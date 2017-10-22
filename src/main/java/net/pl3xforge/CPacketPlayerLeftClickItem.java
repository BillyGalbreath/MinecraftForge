package net.pl3xforge;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

import java.io.IOException;

public class CPacketPlayerLeftClickItem implements Packet<INetHandlerPlayServer>
{
    private EnumHand hand;

    public CPacketPlayerLeftClickItem()
    {
    }

    public CPacketPlayerLeftClickItem(EnumHand handIn)
    {
        this.hand = handIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.hand = (EnumHand)buf.readEnumValue(EnumHand.class);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.hand);
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processLeftClickItem(this);
    }

    public EnumHand getHand()
    {
        return this.hand;
    }
}