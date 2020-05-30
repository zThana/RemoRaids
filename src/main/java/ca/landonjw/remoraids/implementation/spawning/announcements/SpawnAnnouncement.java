package ca.landonjw.remoraids.implementation.spawning.announcements;

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

    /** The placeholder for a boss's species. */
    public static final String BOSS_SPECIES_PLACEHOLDER = "{boss-species}";
    /** The placeholder for the x coordinate of the location of a boss spawn. */
    public static final String BOSS_LOCATION_X_PLACEHOLDER = "{boss-x}";
    /** The placeholder for the y coordinate of the location of a boss spawn. */
    public static final String BOSS_LOCATION_Y_PLACEHOLDER = "{boss-y}";
    /** The placeholder for the z coordinate of the location of a boss spawn. */
    public static final String BOSS_LOCATION_Z_PLACEHOLDER = "{boss-z}";

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
        return announcement
                .replace(BOSS_SPECIES_PLACEHOLDER, spawner.getBoss().getPokemon().getSpecies().name)
                .replace(BOSS_LOCATION_X_PLACEHOLDER, "" + spawner.getSpawnLocation().getX())
                .replace(BOSS_LOCATION_Y_PLACEHOLDER, "" + spawner.getSpawnLocation().getY())
                .replace(BOSS_LOCATION_Z_PLACEHOLDER, "" + spawner.getSpawnLocation().getZ());
    }

    /** {@inheritDoc} */
    @Override
    public JObject serialize() {
        return new JObject().add("message", this.announcement);
    }

}
