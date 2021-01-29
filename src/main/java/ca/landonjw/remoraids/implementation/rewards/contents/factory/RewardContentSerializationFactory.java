package ca.landonjw.remoraids.implementation.rewards.contents.factory;

import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.contents.factory.creators.CommandContentSerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.factory.creators.CurrencyContentSerializationStrategy;
import ca.landonjw.remoraids.implementation.rewards.contents.factory.creators.ItemContentDeserializer;
import ca.landonjw.remoraids.implementation.rewards.contents.factory.creators.PokemonContentDeserializer;

public class RewardContentSerializationFactory extends SerializationFactory<IRewardContent> {

	@Override
	protected void registerDefaults() {
		this.register(new CommandContentSerializationStrategy());
		this.register(new CurrencyContentSerializationStrategy());
		this.register(new ItemContentDeserializer());
		this.register(new PokemonContentDeserializer());
	}

}
