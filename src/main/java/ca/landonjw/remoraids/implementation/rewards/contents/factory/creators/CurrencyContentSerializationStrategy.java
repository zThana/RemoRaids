package ca.landonjw.remoraids.implementation.rewards.contents.factory.creators;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.data.ISerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.CurrencyContent;

public class CurrencyContentSerializationStrategy implements ISerializationStrategy<CurrencyContent> {

	@Override
	public String getTypeToken() {
		return "currency";
	}

	@Override
	public Class<CurrencyContent> getSerializedClass() {
		return CurrencyContent.class;
	}

	@Override
	public JsonObject serialize(CurrencyContent obj) {
		return obj.serialize().toJson();
	}

	@Override
	public CurrencyContent deserialize(@Nonnull JsonObject data) {
		int amount = data.get("amount").getAsInt();

		if (data.get("description") != null) {
			String description = data.get("description").getAsString();
			return new CurrencyContent(amount, description);
		}
		return new CurrencyContent(amount);
	}

}
