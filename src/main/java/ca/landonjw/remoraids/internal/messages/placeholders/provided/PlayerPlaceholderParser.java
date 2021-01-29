package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "player";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		EntityPlayerMP player = context.getAssociation(EntityPlayerMP.class).orElse(null);
		if (player != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				switch (arguments.get(0).toLowerCase()) {
				case "name":
					return Optional.of(player.getName());
				case "displayname":
					return Optional.of(player.getDisplayName().getFormattedText());
				case "uuid":
					return Optional.of(player.getUniqueID().toString());
				case "world":
					return Optional.of(player.getEntityWorld().getWorldInfo().getWorldName());
				case "x":
					return Optional.of("" + Math.round(player.posX));
				case "y":
					return Optional.of("" + Math.round(player.posY));
				case "z":
					return Optional.of("" + Math.round(player.posZ));
				case "level":
					return Optional.of("" + player.experienceLevel);
				}
			}
			return Optional.of(player.getName());
		}
		return Optional.empty();
	}

}
