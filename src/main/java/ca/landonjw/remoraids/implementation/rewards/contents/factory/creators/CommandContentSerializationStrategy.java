package ca.landonjw.remoraids.implementation.rewards.contents.factory.creators;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.data.ISerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.CommandContent;

public class CommandContentSerializationStrategy implements ISerializationStrategy<CommandContent> {

	@Override
	public String getTypeToken() {
		return "command";
	}

	@Override
	public Class<CommandContent> getSerializedClass() {
		return CommandContent.class;
	}

	@Override
	public JsonObject serialize(CommandContent obj) {
		return obj.serialize().toJson();
	}

	@Override
	public CommandContent deserialize(@Nonnull JsonObject data) {
		String command = data.get("command").getAsString();

		if (data.get("description") != null) {
			String description = data.get("description").getAsString();
			return new CommandContent(command, description);
		}
		return new CommandContent(command);
	}

}
