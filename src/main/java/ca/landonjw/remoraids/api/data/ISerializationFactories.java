package ca.landonjw.remoraids.api.data;

import java.util.Optional;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;

public interface ISerializationFactories {

	<T> void registerFactory(@Nonnull Class<T> clazz, @Nonnull SerializationFactory<T> factory);

	<T> Optional<SerializationFactory<T>> getFactory(@Nonnull Class<T> clazz);

	SerializationFactory<IRewardContent> getRewardContentFactory();

	SerializationFactory<IReward> getRewardFactory();

	SerializationFactory<IBattleRestraint> getRestraintFactory();

}
