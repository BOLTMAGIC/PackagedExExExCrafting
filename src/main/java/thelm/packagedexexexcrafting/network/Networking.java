package thelm.packagedexexexcrafting.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import thelm.packagedexexexcrafting.PackagedExExExCrafting;

public class Networking {

    private static final String PROTOCOL = "1";
    @SuppressWarnings("deprecation")
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PackagedExExExCrafting.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void register() {
        CHANNEL.registerMessage(nextID(), PacketGridClick.class, PacketGridClick::encode, PacketGridClick::decode, PacketGridClick::handle);
        CHANNEL.registerMessage(nextID(), PacketGridDrag.class, PacketGridDrag::encode, PacketGridDrag::decode, PacketGridDrag::handle);
    }
}


