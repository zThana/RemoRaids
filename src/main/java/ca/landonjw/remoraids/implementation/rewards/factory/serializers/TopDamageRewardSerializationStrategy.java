package ca.landonjw.remoraids.implementation.rewards.factory.serializers;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.data.ISerializationStrategy;
import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.options.TopDamageReward;

public class TopDamageRewardSerializationStrategy implements ISerializationStrategy<TopDamageReward> {

	@Override
	public String getTypeToken() {
		return "top-damage";
	}

	@Override
	public Class<TopDamageReward> getSerializedClass() {
		return TopDamageReward.class;
	}

	@Override
	public JsonObject serialize(TopDamageReward obj) {
		return obj.serialize().toJson();
	}

	@Override
	public TopDamageReward deserialize(@Nonnull JsonObject data) {
		int numReceivers = data.get("num-receivers").getAsInt();

		JsonArray jsonRewardContents = data.get("contents").getAsJsonArray();
		List<IRewardContent> rewardContents = Lists.newArrayList();

		SerializationFactory<IRewardContent> rewardContentFactory = RemoRaids.getDeserializerFactories().getRewardContentFactory();
		for (JsonElement jsonRewardContent : jsonRewardContents) {
			IRewardContent rewardContent = rewardContentFactory.deserialize(jsonRewardContent.getAsJsonObject()).orElse(null);
			if (rewardContent != null) {
				rewardContents.add(rewardContent);
			}
		}

		return new TopDamageReward(numReceivers, rewardContents.toArray(new IRewardContent[0]));
	}
}
