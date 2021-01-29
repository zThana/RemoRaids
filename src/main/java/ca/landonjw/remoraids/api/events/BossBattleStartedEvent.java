package ca.landonjw.remoraids.api.events;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a battle is started with a {@link IBossEntity}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossBattleStartedEvent extends Event {

	/** The boss entity the battle is being started with. */
	private final IBossEntity bossEntity;
	/** The player the battle is being started with. */
	private final EntityPlayerMP player;
	/** The boss battle container. */
	private final IBossBattle bossBattle;
	/** The battle controller of the battle. */
	private final BattleControllerBase battleController;

	/**
	 * Constructor for the event.
	 *
	 * @param bossEntity       the boss entity the battle is being started with
	 * @param player           the player the battle is being started with
	 * @param bossBattle       the boss battle container
	 * @param battleController the battle controller of the battle
	 */
	public BossBattleStartedEvent(@Nonnull IBossEntity bossEntity, @Nonnull EntityPlayerMP player, @Nonnull IBossBattle bossBattle, @Nonnull BattleControllerBase battleController) {
		this.bossEntity = bossEntity;
		this.player = player;
		this.bossBattle = bossBattle;
		this.battleController = battleController;
	}

	/**
	 * Gets the boss entity the battle is being started with.
	 *
	 * @return the boss entity the battle is being started with
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the player the battle is being started with.
	 *
	 * @return the player the battle is being started with
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

	/**
	 * Gets the battle controller for the boss battle.
	 *
	 * @return the battle controller
	 */
	public BattleControllerBase getBattleController() {
		return battleController;
	}

}
