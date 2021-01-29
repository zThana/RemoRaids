package ca.landonjw.remoraids.implementation.battles.restraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

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
 * An implementation of {@link IBattleRestraint} that prevents the player from fighting a boss with several multiple of the same species (Species Clause).
 *
 * @author zThana
 * @since 1.0.0
 */
public class SpeciesClauseRestraint implements IBattleRestraint {

	/**
	 * {@inheritDoc}
	 *
	 * Always returns false.
	 */
	@Override
	public boolean validatePlayer(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		List<String> checked = new ArrayList<String>();
		for (Pokemon p : Pixelmon.storageManager.getParty(player).getTeam()) {
			if (checked.contains(p.getSpecies().name())) {
				return false;
			} else
				checked.add(p.getSpecies().name());
		}

		return true;
	}

	@Override
	public String getId() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.SPECIES_CLAUSE_RESTRAINT_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player, @Nonnull IBoss boss) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(EntityPlayerMP.class, () -> player).add(IBoss.class, () -> boss).build();
		return Optional.of(service.interpret(config.get(MessageConfig.SPECIES_CLAUSE_RESTRAINT_WARNING), context));
	}
}