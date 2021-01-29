package ca.landonjw.remoraids.api.events;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a boss is spawned and an {@link IBossEntity} is produced.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossSpawnedEvent extends Event {

	/** The boss entity spawned. */
	private final IBossEntity bossEntity;
	/** The spawner responsible for spawning the boss entity. */
	private final IBossSpawner spawner;

	/**
	 * Constructor for the event.
	 *
	 * @param bossEntity the boss entity spawned
	 * @param spawner    the spawner responsible for spawning the boss entity
	 */
	public BossSpawnedEvent(@Nonnull IBossEntity bossEntity, @Nonnull IBossSpawner spawner) {
		this.bossEntity = bossEntity;
		this.spawner = spawner;
	}

	/**
	 * Gets the boss entity spawned.
	 *
	 * @return the boss entity spawned
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the spawner responsible for spawning the boss entity
	 *
	 * @return the spawner responsible for spawning the boss entity
	 */
	public IBossSpawner getSpawner() {
		return spawner;
	}

}
