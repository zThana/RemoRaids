package ca.landonjw.remoraids.implementation.rewards.contents.factory.creators;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.data.ISerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.ItemContent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemContentDeserializer implements ISerializationStrategy<ItemContent> {

	@Override
	public String getTypeToken() {
		return "item";
	}

	@Override
	public Class<ItemContent> getSerializedClass() {
		return ItemContent.class;
	}

	@Override
	public JsonObject serialize(ItemContent obj) {
		return obj.serialize().toJson();
	}

	@Override
	public ItemContent deserialize(@Nonnull JsonObject data) {
		String item = data.get("item").getAsString();
		int amount = data.get("amount").getAsInt();

		ItemStack stack = new ItemStack(Item.getByNameOrId(item), amount);

		if (data.get("description") != null) {
			String description = data.get("description").getAsString();
			return new ItemContent(stack, description);
		}
		return new ItemContent(stack);
	}
}
