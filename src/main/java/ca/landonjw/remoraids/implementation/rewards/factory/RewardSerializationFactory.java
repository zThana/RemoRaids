package ca.landonjw.remoraids.implementation.rewards.factory;

import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.rewards.factory.serializers.KillerRewardSerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.factory.serializers.ParticipationRewardSerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.factory.serializers.TopDamageRewardSerializationStrategy;

public class RewardSerializationFactory extends SerializationFactory<IReward> {

	@Override
	protected void registerDefaults() {
		this.register(new KillerRewardSerializationStrategy());
		this.register(new ParticipationRewardSerializationStrategy());
		this.register(new TopDamageRewardSerializationStrategy());
	}

}
