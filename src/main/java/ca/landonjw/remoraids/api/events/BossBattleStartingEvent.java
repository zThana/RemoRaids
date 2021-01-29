package ca.landonjw.remoraids.api.events;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a battle is about to start with a {@link IBossEntity}.
 * Cancelling this event will prevent player from joining the boss battle.
 *
 * @author landonjw
 * @since 1.0.0
 */
@Cancelable
public class BossBattleStartingEvent extends Event {

	/** The boss entity battle is being started with. */
	private final IBossEntity bossEntity;
	/** The player attempting to start battle. */
	private final EntityPlayerMP player;

	/**
	 * Constructor for the event.
	 *
	 * @param bossEntity the boss entity battle is being started with
	 * @param player     the player attempting to start battle
	 */
	public BossBattleStartingEvent(@Nonnull IBossEntity bossEntity, @Nonnull EntityPlayerMP player) {
		this.bossEntity = bossEntity;
		this.player = player;
	}

	/**
	 * Gets the boss entity battle is being started with.
	 *
	 * @return the boss entity battle is being started with
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the player attempting to start battle.
	 *
	 * @return the player attempting to start battle
	 */
	public EntityPlayerMP getPlayer() {
		return player;
	}
}