package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class RewardArgument implements IRaidsArgument {

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("rewards", "r");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBossBattleSettings.class).isPresent()) {
			IBossBattleSettings settings = context.getAssociation(IBossBattleSettings.class).get();

			CompletableFuture.supplyAsync(() -> {
				return RemoRaids.getRaidsPersistence().getRewardSetDataLoader().read(value);
			}).exceptionally((e) -> {
				RemoRaids.logger.warn("Reward set file '" + value + ".json' could not be read.");
				return Optional.empty();
			}).thenAccept((maybeRewards) -> {
				maybeRewards.ifPresent(iRewards -> settings.getRewards().addAll(iRewards));
			});
		}
	}

}