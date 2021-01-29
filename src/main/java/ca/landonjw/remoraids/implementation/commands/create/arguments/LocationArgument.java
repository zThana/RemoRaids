package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.OnlinePlayerArgumentParser;
import ca.landonjw.remoraids.api.commands.arguments.parsers.Vec3dArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

public class LocationArgument implements IRaidsArgument {

	private final Vec3dArgumentParser vec3dParser = new Vec3dArgumentParser();
	private final OnlinePlayerArgumentParser playerParser = new OnlinePlayerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("location", "loc");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) {
		if (context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).isPresent()) {
			IBossSpawnLocation.IBossSpawnLocationBuilder builder = context.getAssociation(IBossSpawnLocation.IBossSpawnLocationBuilder.class).get();

			Optional<Vec3d> maybeLocation = vec3dParser.parse(value);
			if (maybeLocation.isPresent()) {
				setSpawnLocation(builder, maybeLocation.get());
				return;
			}

			Optional<EntityPlayerMP> maybePlayer = playerParser.parse(value);
			if (maybePlayer.isPresent()) {
				EntityPlayer player = maybePlayer.get();
				setSpawnLocation(builder, new Vec3d(player.posX, player.posY, player.posZ));
				return;
			}
		}
		throw new IllegalArgumentException("Illegal location value");
	}

	private void setSpawnLocation(IBossSpawnLocation.IBossSpawnLocationBuilder builder, Vec3d location) {
		builder.location(location);
	}

}