package ca.landonjw.remoraids.implementation.spawning.announcements;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.gson.JObject;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Base implementation for {@link ISpawnAnnouncement}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class SpawnAnnouncement implements ISpawnAnnouncement {

    /** The announcement to be sent to players when a spawn occurs. */
    private String announcement;

    /**
     * Constructor for the spawn announcement.
     *
     * @param announcement the announcement to be sent to players when a boss spawn occurs
     */
    public SpawnAnnouncement(@Nullable String announcement){
        this.announcement = announcement;
    }

    /** {@inheritDoc} */
    @Override
    public void setAnnouncement(@Nullable String announcement) {
        this.announcement = announcement;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getAnnouncement() {
        return Optional.ofNullable(announcement);
    }

    /** {@inheritDoc} */
    @Override
    public void sendAnnouncement(IBossSpawner spawner) {
        if(announcement != null){
            ITextComponent text = getAnnouncementText(announcement, spawner);
            PlayerList players = FMLServerHandler.instance().getServer().getPlayerList();
            players.sendMessage(text);
        }
    }

    /**
     * Gets the text representation of the announcement to be sent to players.
     *
     * @param announcement the announcement
     * @return the announcement as a text object
     */
    protected ITextComponent getAnnouncementText(@Nonnull String announcement, @Nonnull IBossSpawner spawner){
        String parsedAnnouncement = getParsedAnnouncement(announcement, spawner);
        return new TextComponentString(parsedAnnouncement);
    }

    /**
     * Replaces all placeholders in the announcement with the true values the placeholder represents.
     * Placeholders can be found within this class.
     *
     * @param announcement the announcement with placeholders
     * @param spawner      the spawner being used
     * @return the announcement with placeholders replaced
     */
    private String getParsedAnnouncement(@Nonnull String announcement, @Nonnull IBossSpawner spawner){
        IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
        IParsingContext context = IParsingContext.builder()
                .add(IBossSpawnLocation.class, spawner::getSpawnLocation)
                .add(IBoss.class, spawner::getBoss)
                .build();
        return service.interpret(announcement, context);
    }

    /** {@inheritDoc} */
    @Override
    public JObject serialize() {
        return new JObject().add("message", this.announcement);
    }

}
