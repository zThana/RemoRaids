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
import ca.landonjw.remoraids.implementation.rewards.options.ParticipationReward;

public class ParticipationRewardSerializationStrategy implements ISerializationStrategy<ParticipationReward> {

	@Override
	public String getTypeToken() {
		return "participation";
	}

	@Override
	public Class<ParticipationReward> getSerializedClass() {
		return ParticipationReward.class;
	}

	@Override
	public JsonObject serialize(ParticipationReward obj) {
		return obj.serialize().toJson();
	}

	@Override
	public ParticipationReward deserialize(@Nonnull JsonObject data) {
		JsonArray jsonRewardContents = data.get("contents").getAsJsonArray();
		List<IRewardContent> rewardContents = Lists.newArrayList();

		SerializationFactory<IRewardContent> rewardContentFactory = RemoRaids.getDeserializerFactories().getRewardContentFactory();
		for (JsonElement jsonRewardContent : jsonRewardContents) {
			IRewardContent rewardContent = rewardContentFactory.deserialize(jsonRewardContent.getAsJsonObject()).orElse(null);
			if (rewardContent != null) {
				rewardContents.add(rewardContent);
			}
		}

		return new ParticipationReward(rewardContents.toArray(new IRewardContent[0]));
	}

}
