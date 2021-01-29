package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class WorldArgumentParser implements IArgumentParser<World> {

	@Override
	public Optional<World> parse(@Nonnull String argument) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		for (WorldServer world : server.worlds) {
			if (world.getWorldInfo().getWorldName().equals(argument)) {
				return Optional.of(world);
			}
		}
		return Optional.empty();
	}

}
