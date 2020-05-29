package ca.landonjw.remoraids.internal.network;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.implementation.rewards.RaidDropQuery;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ServerItemDropPacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQuery;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import io.netty.channel.ChannelHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Optional;

/**
 * This replaces the native Pixelmon packet handler for player interactions with the drop UI.
 * This is done through the use of reflection, and allows for raids to utilize the drop UI for rewards.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RaidsDropPacketHandler extends ServerItemDropPacket.Handler {

    /**
     * Replaces the old handler with an instance of the new one through reflection.
     */
    public static void initialize(){
        // Get the channels and handlers used in the network wrapper.
        Field channelsField = ObfuscationReflectionHelper.findField(SimpleNetworkWrapper.class, "channels");
        Field messageHandlerField = ObfuscationReflectionHelper.findField(SimpleChannelHandlerWrapper.class, "messageHandler");

        // Go through each handler and once we find the one corresponding to the drop UI, replace it.
        FMLEmbeddedChannel channel;
        try {
            channel = ((EnumMap<Side, FMLEmbeddedChannel>) channelsField.get(Pixelmon.network)).get(Side.SERVER);

            for (ChannelHandler handler : channel.pipeline().toMap().values()) {

                if (handler instanceof SimpleChannelHandlerWrapper) {

                    if (ServerItemDropPacket.Handler.class.isInstance(messageHandlerField.get(handler))) {
                        messageHandlerField.set(handler, RaidsDropPacketHandler.class.newInstance());
                    }
                }
            }

        }
        catch (Exception e) {
            RemoRaids.logger.error("Drop UI packet handler could not be initialized!");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Invoked when the user interacts with the drop UI and sends the server a packet.
     *
     * @param message the packet being sent
     * @param ctx     additional packet details
     * @return always null
     */
    @Override
    public IMessage onMessage(ServerItemDropPacket message, MessageContext ctx) {
        // Get the player sending the packet
        EntityPlayerMP player = ctx.getServerHandler().player;

        player.getServer().addScheduledTask(() -> {
            Class messageClass = message.getClass();
            try{
                // Use reflection to get the interaction type and slotID from the original packet, due to them being default access.
                Field mode = messageClass.getDeclaredField("mode");
                Field itemID = messageClass.getDeclaredField("itemID");
                mode.setAccessible(true);
                itemID.setAccessible(true);

                ServerItemDropPacket.PacketMode modeValue = (ServerItemDropPacket.PacketMode) mode.get(message);
                int itemIdValue = itemID.getInt(message);

                /*
                 * Splits the interactions depending on the packet interaction type.
                 * If the player has a drop UI query for a raid boss, we will redirect to our methods.
                 */
                switch(modeValue){
                    case TakeItem:
                        if(getRaidDropQuery(player).isPresent()){
                            getRaidDropQuery(player).get().takeReward(player, itemIdValue);
                        }
                        else{
                            DropItemQueryList.takeItem(player, itemIdValue);
                        }
                        break;
                    case DropAllItems:
                        if(getRaidDropQuery(player).isPresent()){
                            DropItemQueryList.removeQuery(player);
                        }
                        else{
                            DropItemQueryList.dropAllItems(player);
                        }
                        break;
                    case TakeAllItems:
                        if(getRaidDropQuery(player).isPresent()){
                            getRaidDropQuery(player).get().takeAll(player);
                        }
                        else{
                            DropItemQueryList.takeAllItems(player);
                        }
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        });
        return null;
    }

    /**
     * Gets a raid drop query for a player if available.
     *
     * @param player player to get raid drop query for
     * @return optional containing raid drop query, or empty optional if no raid drop query is found
     */
    private Optional<RaidDropQuery> getRaidDropQuery(EntityPlayerMP player){
        for(DropItemQuery query : DropItemQueryList.queryList){
            if(query.playerUUID.equals(player.getUniqueID())){
                if(query instanceof RaidDropQuery){
                    return Optional.of((RaidDropQuery) query);
                }
            }
        }
        return Optional.empty();
    }

}
