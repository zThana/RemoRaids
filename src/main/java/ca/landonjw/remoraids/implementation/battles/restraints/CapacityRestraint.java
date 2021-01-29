package ca.landonjw.remoraids.implementation.battles.restraints;

import java.util.Optional;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * An implementation of {@link IBattleRestraint} that prevents players from battling the boss
 * if the boss reaches a specified player capacity.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class CapacityRestraint implements IBattleRestraint {

	/**
	 * The unmodified capacity of the boss.
	 * This will remain constant regardless of if the restraint is diminishing.
	 * This is included in order to reset the restraint to it's base state on despawn.
	 */
	private int cleanCapacity;
	/** The capacity of the boss. */
	private int capacity;
	/** The number of players currently in battle with the boss. */
	private int playerNum;
	/** If the capacity reduces on each player battle end. */
	private boolean diminishing;

	/**
	 * Constructor for the capacity restraint.
	 *
	 * @param capacity the capacity of the boss
	 */
	public CapacityRestraint(int capacity) {
		cleanCapacity = capacity;
		this.capacity = capacity;
	}

	/** {@inheritDoc} */
	@Override
	public boolean validatePlayer(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		return playerNum < capacity;
	}

	@Override
	public String getId() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.CAPACITY_RESTRAINT_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(CapacityRestraint.class, () -> this).add(EntityPlayerMP.class, () -> player).add(IBoss.class, () -> boss).build();
		return Optional.of(service.interpret(config.get(MessageConfig.CAPACITY_RESTRAINT_WARNING), context));
	}

	/**
	 * {@inheritDoc}
	 *
	 * When a battle starts, this will increment the player number.
	 *
	 * @param player player entering battle
	 */
	@Override
	public void onBattleStart(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		playerNum++;
	}

	/**
	 * {@inheritDoc}
	 *
	 * When a battle ends, this will decrement the player number.
	 *
	 * @param player player leaving battle
	 */
	@Override
	public void onBattleEnd(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		playerNum--;
		if (diminishing) {
			capacity--;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * This will reset the capacity of the restraint back to normal.
	 */
	@Override
	public void onBossDespawn(@Nonnull IBoss boss) {
		playerNum = 0;
		capacity = cleanCapacity;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.cleanCapacity = capacity;
		this.capacity = capacity;
	}

	public void setDiminishing(boolean diminishing) {
		this.diminishing = diminishing;
	}

	public boolean isDiminishing() {
		return diminishing;
	}

}
