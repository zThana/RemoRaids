package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.DoubleArgumentParser;
import ca.landonjw.remoraids.api.commands.arguments.parsers.OnlinePlayerArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class RotationArgument implements IRaidsArgument {

	private final DoubleArgumentParser doubleParser = new DoubleArgumentParser();
	private final OnlinePlayerArgumentParser playerParser = new OnlinePlayerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("rotation", "rot");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).isPresent()) {
			IBossSpawnLocation.IBossSpawnLocationBuilder builder = context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).get();

			Optional<Double> maybeRotation = doubleParser.parse(value);
			if (maybeRotation.isPresent()) {
				builder.rotation(maybeRotation.get().floatValue());
				return;
			}

			Optional<EntityPlayerMP> maybePlayer = playerParser.parse(value);
			if (maybePlayer.isPresent()) {
				EntityPlayer player = maybePlayer.get();
				builder.rotation(player.rotationYaw);
				return;
			}
		}
		throw new IllegalArgumentException("Illegal rotation value");
	}

}
