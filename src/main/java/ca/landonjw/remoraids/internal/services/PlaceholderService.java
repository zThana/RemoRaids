package ca.landonjw.remoraids.internal.services;

import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.services.placeholders.service.IPlaceholderService;
import ca.landonjw.remoraids.internal.services.placeholders.provided.RaidBossPlaceholderParser;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PlaceholderService implements IPlaceholderService {

	private Map<String, IPlaceholderParser> parsers = Maps.newHashMap();

	@Override
	public void register(@NonNull IPlaceholderParser parser) throws IllegalArgumentException {
		this.parsers.put(parser.getKey(), parser);
	}

	@Override
	public Optional<String> parse(@NonNull String token, @Nullable Supplier<Object> association, @Nullable Collection<String> arguments) {
		return this.getParser(token).flatMap(parser -> parser.parse(this.contextualize(association, arguments)));
	}

	@Override
	public IPlaceholderContext contextualize(@Nullable Supplier<Object> association, @Nullable Collection<String> arguments) {
		return IPlaceholderContext.builder()
				.setAssociatedObject(association)
				.arguments(arguments)
				.build();
	}

	@Override
	public Optional<IPlaceholderParser> getParser(@NonNull String token) {
		return Optional.ofNullable(this.parsers.get(token));
	}

	@Override
	public void registerDefaults() {
		this.register(IPlaceholderParser.builder()
				.key("player")
				.parser(context -> {
					EntityPlayerMP player = (EntityPlayerMP) context.getAssociatedObject()
							.filter(x -> x instanceof EntityPlayerMP)
							.orElse(null);

					if(player == null) {
						return Optional.empty();
					}

					return Optional.of(player.getName());
				})
				.build()
		);
		this.register(new RaidBossPlaceholderParser());
	}

}
