package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.OnlinePlayerArgumentParser;
import ca.landonjw.remoraids.api.commands.arguments.parsers.WorldArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class WorldArgument implements IRaidsArgument {

	private final WorldArgumentParser worldParser = new WorldArgumentParser();
	private final OnlinePlayerArgumentParser playerParser = new OnlinePlayerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("world", "w");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).isPresent()) {
			IBossSpawnLocation.IBossSpawnLocationBuilder builder = context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).get();

			Optional<World> maybeWorld = worldParser.parse(value);
			if (maybeWorld.isPresent()) {
				builder.world(maybeWorld.get());
				return;
			}

			Optional<EntityPlayerMP> maybePlayer = playerParser.parse(value);
			if (maybePlayer.isPresent()) {
				EntityPlayer player = maybePlayer.get();
				builder.world(player.world);
				return;
			}
		}
		throw new IllegalArgumentException("Illegal world value");
	}

}
