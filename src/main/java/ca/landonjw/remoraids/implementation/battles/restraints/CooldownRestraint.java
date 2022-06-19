package ca.landonjw.remoraids.implementation.battles.restraints;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
 * An implementation of {@link IBattleRestraint} that puts players on a cooldown after they leave the boss battle,
 * and prevents them from rejoining until the cooldown is over.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class CooldownRestraint implements IBattleRestraint {

	/** Map of players and the last time they left battle with the boss. */
	private final Map<UUID, Instant> lastBattleTimeMap = new HashMap<>();
	/** The cooldown in seconds before plays may reenter battle with the boss, after they have left. */
	private long cooldownSeconds;

	/**
	 * Constructor for the cooldown restraint.
	 *
	 * @param cooldown cooldown value
	 * @param unit     time unit of the cooldown
	 */
	public CooldownRestraint(long cooldown, @Nonnull TimeUnit unit) {
		cooldownSeconds = unit.toSeconds(cooldown);
	}

	/** {@inheritDoc} */
	@Override
	public boolean validatePlayer(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		if (lastBattleTimeMap.containsKey(player.getUniqueID())) {
			Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player.getUniqueID()), Instant.now());
			return cooldownSeconds <= timeElapsed.getSeconds();
		}

		return true;
	}

	@Override
	public String getId() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.COOLDOWN_RESTRAINT_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		if (lastBattleTimeMap.containsKey(player.getUniqueID())) {
			Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player.getUniqueID()), Instant.now());
			if (cooldownSeconds > timeElapsed.getSeconds()) {
				Config config = RemoRaids.getMessageConfig();
				IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

				IParsingContext context = IParsingContext.builder().add(CooldownRestraint.class, () -> this).add(EntityPlayerMP.class, () -> player).add(IBoss.class, () -> boss).build();
				return Optional.of(service.interpret(config.get(MessageConfig.COOLDOWN_RESTRAINT_WARNING), context));
			}
		}
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * When a battle ends, this will add the player to the cooldown map.
	 *
	 * @param player player leaving battle
	 */
	@Override
	public void onBattleEnd(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		lastBattleTimeMap.put(player.getUniqueID(), Instant.now());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Resets the cooldown map.
	 */
	@Override
	public void onBossDespawn(@Nonnull IBoss boss) {
		lastBattleTimeMap.clear();
	}

	/**
	 * Gets the cooldown in seconds before plays may reenter battle with the boss, after they have left.
	 *
	 * @return cooldown in seconds before plays may reenter battle with the boss, after they have left.
	 */
	public long getCooldown(TimeUnit unit) {
		return unit.convert(cooldownSeconds, TimeUnit.SECONDS);
	}

	public void setCooldown(long time, TimeUnit unit) {
		this.cooldownSeconds = unit.toSeconds(time);
	}

	/**
	 * Gets the cooldown remaining before a player may reenter battle with the boss.
	 *
	 * @param player player to get cooldown for
	 * @param unit   time unit to get cooldown in
	 * @return cooldown remaining before player may reenter battle
	 */
	public Optional<Long> getCooldownRemaining(@Nonnull EntityPlayerMP player, TimeUnit unit) {
		if (lastBattleTimeMap.containsKey(player.getUniqueID())) {
			Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player.getUniqueID()), Instant.now());
			if (cooldownSeconds > timeElapsed.getSeconds()) {
				return Optional.of(unit.convert(cooldownSeconds - timeElapsed.getSeconds(), TimeUnit.SECONDS));
			}
		}
		return Optional.empty();
	}

}