package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class OnlinePlayerArgumentParser implements IArgumentParser<EntityPlayerMP> {

	@Override
	public Optional<EntityPlayerMP> parse(@Nonnull String argument) {
		PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
		return Optional.ofNullable(playerList.getPlayerByUsername(argument));
	}

}
