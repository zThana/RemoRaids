package ca.landonjw.remoraids.implementation.spawning.announcements;

import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.internal.text.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * An implementation of {@link SpawnAnnouncement} that will teleport players to a {@link IBossSpawnLocation} when it is clicked in chat.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class TeleportableSpawnAnnouncement extends SpawnAnnouncement {

    /**
     * Constructor for the spawn announcement.
     *
     * @param announcement
     */
    public TeleportableSpawnAnnouncement(@Nullable String announcement) {
        super(announcement);
    }

    /**
     * {@inheritDoc}
     *
     * Adds a callback to teleport a player to the spawn location if they click on the spawn announcement.
     */
    @Override
    protected ITextComponent getAnnouncementText(String announcement, IBossSpawner spawner) {
        ITextComponent text = super.getAnnouncementText(announcement, spawner);

        Consumer<ICommandSender> teleportConsumer = (sender) -> {
            if(sender instanceof EntityPlayerMP){
                EntityPlayerMP player = (EntityPlayerMP) sender;

                IBossSpawnLocation spawnLocation = spawner.getSpawnLocation();
                World spawnWorld = spawnLocation.getWorld();

                if(player.dimension != spawnWorld.provider.getDimension()){
                    PlayerList playerList = player.getServer().getPlayerList();
                    Teleporter teleporter = ((WorldServer) spawnWorld).getDefaultTeleporter();
                    playerList.transferPlayerToDimension(player, spawnWorld.provider.getDimension(), teleporter);
                }
                player.setPositionAndUpdate(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            }
        };

        return TextUtils.addCallback(text, teleportConsumer);
    }

}
