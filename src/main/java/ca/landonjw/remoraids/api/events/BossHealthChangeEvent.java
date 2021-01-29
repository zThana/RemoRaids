package ca.landonjw.remoraids.api.events;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a boss' health is being changed due to battle.
 *
 * The amount of health change can be modified during this time.
 * If cancelled, the boss' health will not change.
 *
 * @author landonjw
 * @since 1.0.0
 */
@Cancelable
public class BossHealthChangeEvent extends Event {

	/** The battle the health is being changed in. */
	private final IBossBattle battle;
	/** The boss entity having it's health changed. */
	private final IBossEntity bossEntity;
	/** The player damaging the boss. May be null if health change is not caused by a player. */
	private final EntityPlayerMP source;
	/** The difference in health. Positive for healing, negative for damage. */
	private int difference;

	/**
	 * Constructor for the event.
	 *
	 * @param battle     the battle health is being changed in
	 * @param bossEntity the boss entity having health changed
	 * @param source     the source of the damage, null if not a player
	 * @param difference difference in health, positive for healing, negative for damage
	 */
	public BossHealthChangeEvent(@Nonnull IBossBattle battle, @Nonnull IBossEntity bossEntity, @Nullable EntityPlayerMP source, int difference) {
		this.battle = battle;
		this.bossEntity = bossEntity;
		this.source = source;
		this.difference = difference;
	}

	/**
	 * Gets the battle the health is being changed in.
	 *
	 * @return the battle health is being changed in
	 */
	public IBossBattle getBattle() {
		return battle;
	}

	/**
	 * Gets the boss entity having it's health changed.
	 *
	 * @return the boss entity having it's health changed
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/**
	 * Gets the player source of the damage. This may be an empty optional
	 * in the case that the health is being modified by a third party and not necessarily
	 * a player.
	 *
	 * @return the player source of the damage, if present
	 */
	public Optional<EntityPlayerMP> getSource() {
		return Optional.ofNullable(source);
	}

	/**
	 * Gets the difference in health.
	 * This will be negative if the boss is being damaged, and positive if the boss is being healed.
	 *
	 * @return the difference in health
	 */
	public int getDifference() {
		return difference;
	}

	/**
	 * Sets the difference in health.
	 *
	 * @param difference the difference in health, negative to damage, positive to heal
	 */
	public void setDifference(int difference) {
		this.difference = difference;
	}

}
