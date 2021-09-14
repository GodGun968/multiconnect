package net.earthcomputer.multiconnect.packets;

import net.earthcomputer.multiconnect.ap.Message;
import net.earthcomputer.multiconnect.ap.OnlyIf;

@Message
public class SPacketEntityEquipmentUpdate {
    public int entityId;
    public Entry firstEntry;

    @Message
    public static class Entry {
        public byte slot;
        public CommonTypes.ItemStack stack;
        @OnlyIf(field = "slot", condition = "hasNextEntry")
        public Entry nextEntry;

        public static boolean hasNextEntry(byte slot) {
            return (slot & 0x80) != 0;
        }
    }
}