package ca.landonjw.remoraids.internal.messages.services;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.messages.services.IPlaceholderService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.BattleClausePlaceholder;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.CapacityPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.CooldownPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.ItemStackPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.PlayerPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.PokemonPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.PokemonSpecPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.RaidBossPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.RespawnDataPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.SpawnLocationParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.StatPlaceholderParser;
import ca.landonjw.remoraids.internal.messages.placeholders.provided.TimePlaceholderParser;

public class PlaceholderService implements IPlaceholderService {

	private Map<String, IPlaceholderParser> parsers = Maps.newHashMap();

	@Override
	public void register(@Nonnull IPlaceholderParser parser) throws IllegalArgumentException {
		this.parsers.put(parser.getKey(), parser);
	}

	@Override
	public Optional<String> parse(@Nonnull String token, @Nullable IParsingContext context, @Nullable Collection<String> arguments) {
		return this.getParser(token).flatMap(parser -> parser.parse(this.contextualize(context, arguments)));
	}

	@Override
	public IPlaceholderContext contextualize(@Nullable IParsingContext context, @Nullable Collection<String> arguments) {
		return IPlaceholderContext.builder().fromParsingContext((context != null) ? context : IParsingContext.builder().build()).arguments(arguments).build();
	}

	@Override
	public Optional<IPlaceholderParser> getParser(@Nonnull String token) {
		return Optional.ofNullable(this.parsers.get(token));
	}

	@Override
	public void registerDefaults() {
		this.register(IPlaceholderParser.builder().key("integer").parser((context) -> {
			Integer value = context.getAssociation(Integer.class).orElse(null);

			if (value != null) {
				return Optional.of(value.toString());
			}
			return Optional.empty();
		}).build());

		this.register(IPlaceholderParser.builder().key("string").parser((context) -> {
			String value = context.getAssociation(String.class).orElse(null);

			if (value != null) {
				return Optional.of(value);
			}
			return Optional.empty();
		}).build());

		this.register(IPlaceholderParser.builder().key("reward").parser((context) -> {
			IReward reward = context.getAssociation(IReward.class).orElse(null);

			if (reward != null) {
				return Optional.of(reward.getDescription());
			}
			return Optional.empty();
		}).build());

		this.register(new PlayerPlaceholderParser());
		this.register(new RaidBossPlaceholderParser());
		this.register(new CapacityPlaceholderParser());
		this.register(new CooldownPlaceholderParser());
		this.register(new PokemonPlaceholderParser());
		this.register(new PokemonPlaceholderParser.MovesetPlaceholderParser());
		this.register(new PokemonSpecPlaceholderParser());
		this.register(new ItemStackPlaceholderParser());
		this.register(new SpawnLocationParser());
		this.register(new SpawnAnnouncement.PositionPlaceholderParser());
		this.register(new RespawnDataPlaceholderParser());
		this.register(new TimePlaceholderParser());
		this.register(new StatPlaceholderParser());
		this.register(new BattleClausePlaceholder());
	}

}
