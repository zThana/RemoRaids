package ca.landonjw.remoraids.api.spawning;

import javax.annotation.Nullable;
import java.util.Optional;

public interface ISpawnAnnouncement {

    /**
     * Sets the announcement to be sent to players. Formatting can be applied here.
     *
     * @param announcement the announcement to be sent to players
     */
    void setAnnouncement(@Nullable String announcement);

    /**
     * Gets the announcement to be sent to players if there is an announcement set.
     *
     * @return the announcement to be sent to players if present
     */
    Optional<String> getAnnouncement();

    /**
     * Sends the announcement to players.
     *
     * @param spawner the spawner sending the announcement
     */
    void sendAnnouncement(IBossSpawner spawner);

}