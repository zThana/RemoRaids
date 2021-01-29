package ca.landonjw.remoraids.api.events;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a battle has ended between an {@link IBossEntity} and a player.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossBattleEndedEvent extends Event {

	/** The boss entity the player was battling. */
	private final IBossEntity bossEntity;
	/** The player in the battle. */
	private final EntityPlayerMP player;
	/** The boss battle container. */
	private final IBossBattle bossBattle;

	/**
	 * Constructor for the event.
	 *
	 * @param bossEntity the boss entity the player was battling
	 * @param player     the player in the battle
	 * @param bossBattle the boss battle container
	 */
	public BossBattleEndedEvent(@Nonnull IBossEntity bossEntity, @Nonnull EntityPlayerMP player, @Nonnull IBossBattle bossBattle) {
		this.bossEntity = bossEntity;
		this.player = player;
		this.bossBattle = bossBattle;
	}

	/**
	 * Gets the boss entity the player was battling.
	 *
	 * @return the boss entity player was battling
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the player in the battle.
	 *
	 * @return the player in the battle
	 */
	public EntityPlayerMP getPlayer() {
		return player;
	}

	/**
	 * Gets the boss battle container.
	 * This contains information regarding all battles with the boss.
	 *
	 * @return the boss battle container
	 */
	public IBossBattle getBossBattle() {
		return bossBattle;
	}

}
