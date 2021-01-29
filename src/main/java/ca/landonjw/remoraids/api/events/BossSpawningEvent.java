package ca.landonjw.remoraids.api.events;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when an {@link IBoss} is about to spawn.
 * Cancelling this event will cause the boss to not spawn.
 *
 * @author landonjw
 * @since 1.0.0
 */
@Cancelable
public class BossSpawningEvent extends Event {

	/** The boss being spawned. */
	private final IBoss boss;
	/** The spawner being used to spawn the boss. */
	private final IBossSpawner spawner;

	/**
	 * Constructor for the event.
	 *
	 * @param boss    the boss being spawned
	 * @param spawner the spawner being used to spawn the boss
	 */
	public BossSpawningEvent(@Nonnull IBoss boss, @Nonnull IBossSpawner spawner) {
		this.boss = boss;
		this.spawner = spawner;
	}

	/**
	 * Gets the boss being spawned.
	 *
	 * @return boss being spawned
	 */
	public IBoss getBoss() {
		return boss;
	}

	/**
	 * Gets the spawner being used to spawn the boss.
	 *
	 * @return spawner being used to spawn the boss
	 */
	public IBossSpawner getSpawner() {
		return spawner;
	}

}
