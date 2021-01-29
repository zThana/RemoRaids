package ca.landonjw.remoraids.implementation.battles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Implementation of {@link IBossBattleSettings ).
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossBattleSettings implements IBossBattleSettings {

	/** The restraints to validate before battle. */
	private Set<IBattleRestraint> battleRestraints;
	/** The native Pixelmon battle rules. */
	private BattleRules battleRules;
	/** The rewards to be distributed after battle. */
	private Set<IReward> rewards;

	/**
	 * Constructor for a boss's rules upon entrance and during battle.
	 */
	public BossBattleSettings() {
		this.battleRestraints = new HashSet<>();
		this.rewards = new HashSet<>();
	}

	/**
	 * Constructor for a boss's rules upon entrance and during battle.
	 *
	 * @param battleRestraints the restraints to validate before battle
	 * @param battleRules      the native Pixelmon battle rules
	 */
	public BossBattleSettings(@Nullable Set<IBattleRestraint> battleRestraints, @Nullable BattleRules battleRules, @Nullable Set<IReward> rewards) {
		this.battleRestraints = (battleRestraints != null) ? battleRestraints : new HashSet<>();
		this.battleRules = battleRules;
		this.rewards = (rewards != null) ? rewards : new HashSet<>();
	}

	/** {@inheritDoc} */
	@Override
	public Optional<BattleRules> getBattleRules() {
		return Optional.ofNullable(battleRules);
	}

	/** {@inheritDoc} */
	@Override
	public void setBattleRules(@Nullable BattleRules battleRules) {
		this.battleRules = battleRules;
	}

	/** {@inheritDoc} */
	@Override
	public Set<IBattleRestraint> getBattleRestraints() {
		return battleRestraints;
	}

	@Override
	public boolean containsBattleRestraint(String id) {
		for (IBattleRestraint restraint : battleRestraints) {
			if (restraint.getId().equalsIgnoreCase(id))
				return true;
		}

		return false;
	}

	@Override
	public void removeBattleRestraint(String id) {
		IBattleRestraint remove = null;
		for (IBattleRestraint restraint : battleRestraints) {
			if (restraint.getId().equalsIgnoreCase(id)) {
				remove = restraint;
				break;
			}
		}

		if (remove != null)
			battleRestraints.remove(remove);
	}

	@Override
	public Set<IReward> getRewards() {
		return rewards;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(@NonNull EntityPlayerMP player, IBoss boss) {
		return getRejectionMessages(player, boss).isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getRejectionMessages(@NonNull EntityPlayerMP player, @Nonnull IBoss boss) {
		List<String> rejectionMessages = Lists.newArrayList();

		getBrokenBattleRestraints(player, boss).forEach((restraint) -> {
			restraint.getRejectionMessage(player, boss).ifPresent(rejectionMessages::add);
		});

		getBrokenBattleClauses(player).forEach((clause) -> {
			rejectionMessages.add(getBrokenClauseMessage(clause));
		});

		return rejectionMessages;
	}

	/**
	 * Gets a list of battle restraints that the player is not valid in.
	 * If the player is valid for all battle restraints, this will return an empty list.
	 *
	 * @param player the player to validate battle restraints on
	 * @return list of battle restraints player is not valid for, empty list if player validates all
	 */
	private List<IBattleRestraint> getBrokenBattleRestraints(@NonNull EntityPlayerMP player, @Nonnull IBoss boss) {
		List<IBattleRestraint> brokenRestraints = Lists.newArrayList();
		for (IBattleRestraint restraint : battleRestraints) {
			if (!restraint.validatePlayer(player, boss))
				brokenRestraints.add(restraint);
		}

		return brokenRestraints;
	}

	/**
	 * Gets a list of battle clauses that the player is not valid in.
	 * If the player is valid for all battle clauses, this will return an empty list.
	 *
	 * @param player the player to validate battle clauses on
	 * @return list of battle clauses player is not valid for, empty list if player validates all
	 */
	private List<BattleClause> getBrokenBattleClauses(@NonNull EntityPlayerMP player) {
		List<BattleClause> brokenClauses = Lists.newArrayList();
		if (battleRules == null)
			return brokenClauses;
		List<Pokemon> playerTeam = Pixelmon.storageManager.getParty(player).getTeam();

		for (BattleClause clause : battleRules.getClauseList()) {
			if (!clause.validateTeam(playerTeam))
				brokenClauses.add(clause);
		}

		return brokenClauses;
	}

	private String getBrokenClauseMessage(@Nonnull BattleClause clause) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(BattleClause.class, () -> clause).build();
		return service.interpret(config.get(MessageConfig.BROKEN_CLAUSE_WARNING), context);
	}

	@Override
	public JObject serialize() {
		return null;
	}
}
