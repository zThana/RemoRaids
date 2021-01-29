package ca.landonjw.remoraids.api.events;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a {@link IBossEntity} dies.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossDeathEvent extends Event {

	/** The boss entity that has died. */
	private final IBossEntity bossEntity;
	/** The boss entity's battle information. */
	private final IBossBattle bossBattle;

	/**
	 * Constructor for the boss death event.
	 *
	 * @param bossEntity the boss entity that has died
	 * @param bossBattle the boss entity's battle information
	 */
	public BossDeathEvent(@Nonnull IBossEntity bossEntity, @Nonnull IBossBattle bossBattle) {
		this.bossEntity = bossEntity;
		this.bossBattle = bossBattle;
	}

	/**
	 * Gets the boss entity that has died.
	 *
	 * @return boss entity that has died
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the boss entity's battle information.
	 * 
	 * @return
	 */
	public IBossBattle getBossBattle() {
		return bossBattle;
	}

	/**
	 * Gets the killer of the boss, if available.
	 * This may return an empty optional if the boss was killed in an unnatural way, such as a forced despawn.
	 *
	 * @return killer of the boss, if present
	 */
	public Optional<UUID> getKiller() {
		if (bossBattle != null) {
			return bossBattle.getKiller();
		}
		return Optional.empty();
	}

}
