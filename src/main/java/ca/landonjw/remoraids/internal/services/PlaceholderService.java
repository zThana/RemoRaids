package ca.landonjw.remoraids.internal.services;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.services.placeholders.service.IPlaceholderService;
import ca.landonjw.remoraids.internal.services.placeholders.provided.*;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
		return IPlaceholderContext.builder()
				.fromParsingContext((context != null) ? context : IParsingContext.builder().build())
				.arguments(arguments)
				.build();
	}

	@Override
	public Optional<IPlaceholderParser> getParser(@Nonnull String token) {
		return Optional.ofNullable(this.parsers.get(token));
	}

	@Override
	public void registerDefaults() {
		this.register(IPlaceholderParser.builder()
				.key("integer")
				.parser((context) -> {
					Integer value = context.get(Integer.class).orElse(null);

					if(value != null){
						return Optional.of(value.toString());
					}
					return Optional.empty();
				})
				.build());

		this.register(IPlaceholderParser.builder()
				.key("string")
				.parser((context) -> {
					String value = context.get(String.class).orElse(null);

					if(value != null){
						return Optional.of(value);
					}
					return Optional.empty();
				})
				.build());

		this.register(IPlaceholderParser.builder()
				.key("reward")
				.parser((context) -> {
					IReward reward = context.get(IReward.class).orElse(null);

					if(reward != null){
						return Optional.of(reward.getDescription());
					}
					return Optional.empty();
				})
				.build());

		this.register(new PlayerPlaceholderParser());
		this.register(new RaidBossPlaceholderParser());
		this.register(new CapacityPlaceholderParser());
		this.register(new CooldownPlaceholderParser());
		this.register(new PokemonPlaceholderParser());
		this.register(new PokemonSpecPlaceholderParser());
		this.register(new ItemStackPlaceholderParser());
		this.register(new SpawnLocationParser());
	}

}
