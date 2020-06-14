package ca.landonjw.remoraids.internal.services;

import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.services.placeholders.service.IPlaceholderService;
import ca.landonjw.remoraids.internal.services.placeholders.provided.CapacityPlaceholderParser;
import ca.landonjw.remoraids.internal.services.placeholders.provided.CooldownPlaceholderParser;
import ca.landonjw.remoraids.internal.services.placeholders.provided.RaidBossPlaceholderParser;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;

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
				.key("player")
				.parser(context -> {
					EntityPlayerMP player = context.get(EntityPlayerMP.class)
							.orElse(null);

					if(player == null) {
						return Optional.empty();
					}

					return Optional.of(player.getName());
				})
				.build()
		);
		this.register(new CapacityPlaceholderParser());
		this.register(new CooldownPlaceholderParser());
		this.register(new RaidBossPlaceholderParser());
	}

}
