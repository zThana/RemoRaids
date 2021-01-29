package ca.landonjw.remoraids.implementation.rewards.contents.factory.creators;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;

import ca.landonjw.remoraids.api.data.ISerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.PokemonContent;

public class PokemonContentDeserializer implements ISerializationStrategy<PokemonContent> {

	@Override
	public String getTypeToken() {
		return "pokemon";
	}

	@Override
	public Class<PokemonContent> getSerializedClass() {
		return PokemonContent.class;
	}

	@Override
	public JsonObject serialize(PokemonContent obj) {
		return obj.serialize().toJson();
	}

	@Override
	public PokemonContent deserialize(@Nonnull JsonObject data) {
		String specStr = data.get("spec").getAsString();
		PokemonSpec spec = PokemonSpec.from(specStr.split(" "));

		if (data.get("description") != null) {
			String description = data.get("description").getAsString();
			return new PokemonContent(spec, description);
		}
		return new PokemonContent(spec);
	}
}
