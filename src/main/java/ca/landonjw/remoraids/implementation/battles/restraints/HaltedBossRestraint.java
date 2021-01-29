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
 * An implementation of {@link IBattleRestraint} that prevents any battles with the boss.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class HaltedBossRestraint implements IBattleRestraint {

	/**
	 * {@inheritDoc}
	 *
	 * Always returns false.
	 */
	@Override
	public boolean validatePlayer(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		return false;
	}

	@Override
	public String getId() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.HALTED_BOSS_RESTRAINT_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(EntityPlayerMP.class, () -> player).add(IBoss.class, () -> boss).build();
		return Optional.of(service.interpret(config.get(MessageConfig.HALTED_BOSS_RESTRAINT_WARNING), context));
	}
}