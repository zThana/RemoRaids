package ca.landonjw.remoraids.internal.storage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.util.gson.JObject;

public class RewardSetDataLoader {

	private final File directory = new File("config/remoraids/data/rewards");

	public RewardSetDataLoader() {
		initialize();
	}

	private void initialize() {
		if (!this.directory.exists()) {
			this.directory.mkdirs();
		}
	}

	public Optional<List<IReward>> read(String fileName) {
		JsonObject rewardSet;
		try {
			FileReader reader = new FileReader(new File(directory, fileName + ".json"));
			rewardSet = JObject.PRETTY.fromJson(reader, JsonObject.class);
			reader.close();
		} catch (IOException e) {
			RemoRaids.logger.warn("Reward set file '" + fileName + ".json' not found.");
			return Optional.empty();
		}

		JsonArray jsonRewards = rewardSet.getAsJsonArray("rewards");
		List<IReward> rewards = Lists.newArrayList();
		SerializationFactory<IReward> rewardFactory = RemoRaids.getDeserializerFactories().getRewardFactory();
		for (JsonElement jsonReward : jsonRewards) {
			rewardFactory.deserialize(jsonReward.getAsJsonObject()).ifPresent(rewards::add);
		}
		return Optional.of(rewards);
	}

}
