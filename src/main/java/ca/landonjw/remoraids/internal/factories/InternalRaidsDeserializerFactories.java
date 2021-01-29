package ca.landonjw.remoraids.internal.factories;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.data.ISerializationFactories;
import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;

public class InternalRaidsDeserializerFactories implements ISerializationFactories {

	private Map<Class<?>, SerializationFactory<?>> factoryMap = Maps.newHashMap();

	@Override
	public <T> void registerFactory(@Nonnull Class<T> clazz, @Nonnull SerializationFactory<T> factory) {
		if (factoryMap.containsKey(clazz))
			throw new IllegalArgumentException("factory class already registered!");
		factoryMap.put(clazz, factory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<SerializationFactory<T>> getFactory(@Nonnull Class<T> clazz) {
		try {
			return Optional.ofNullable((SerializationFactory<T>) factoryMap.get(clazz));
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}

	@Override
	public SerializationFactory<IRewardContent> getRewardContentFactory() {
		return getFactory(IRewardContent.class).orElseThrow(() -> new IllegalStateException("default factories have not initialized yet!"));
	}

	@Override
	public SerializationFactory<IReward> getRewardFactory() {
		return getFactory(IReward.class).orElseThrow(() -> new IllegalStateException("default factories have not initialized yet!"));
	}

	@Override
	public SerializationFactory<IBattleRestraint> getRestraintFactory() {
		return getFactory(IBattleRestraint.class).orElseThrow(() -> new IllegalStateException("default factories have not initialized yet!"));
	}

}
