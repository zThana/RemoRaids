package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.util.DataSerializable;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Controls how a raid boss is announced to the server.
 */
public interface ISpawnAnnouncement extends DataSerializable<ISpawnAnnouncement> {

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