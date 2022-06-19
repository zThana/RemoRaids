package ca.landonjw.remoraids.implementation.battles.restraints;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

public class NoRebattleRestraint implements IBattleRestraint {

	private final Set<UUID> restrainedPlayers = new HashSet<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validatePlayer(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		return !restrainedPlayers.contains(player.getUniqueID());
	}

	@Override
	public String getId() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.NO_REBATTLE_RESTRAINT_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(EntityPlayerMP.class, () -> player).add(IBoss.class, () -> boss).build();
		return Optional.of(service.interpret(config.get(MessageConfig.NO_REBATTLE_RESTRAINT_WARNING), context));
	}

	/**
	 * {@inheritDoc}
	 *
	 * Adds player to list that prevents them from reentering battle again.
	 *
	 * @param player player leaving battle
	 */
	@Override
	public void onBattleEnd(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		restrainedPlayers.add(player.getUniqueID());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Clears the prevented players list.
	 */
	@Override
	public void onBossDespawn(@Nonnull IBoss boss) {
		restrainedPlayers.clear();
	}
}