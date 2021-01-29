package ca.landonjw.remoraids.api.data;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class SerializationFactory<T> {

	private final Map<String, ISerializationStrategy<? extends T>> typeTokenToStrategy = Maps.newHashMap();
	private final Map<Class<? extends T>, ISerializationStrategy<? extends T>> clazzToStrategy = Maps.newHashMap();

	public SerializationFactory() {
		registerDefaults();
	}

	/**
	 * Used for registering default strategies before the object is public-facing.
	 */
	protected void registerDefaults() {
	}

	;

	@SuppressWarnings("unchecked")
	public <S extends T> Optional<S> deserialize(@Nonnull JsonObject data) {
		String typeToken = data.get("type").getAsString();
		if (!typeTokenToStrategy.containsKey(typeToken))
			return Optional.empty();
		try {
			return Optional.of((S) typeTokenToStrategy.get(typeToken).deserialize(data));
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public <S extends T> Optional<JsonObject> serialize(S obj) {
		if (!clazzToStrategy.containsKey(obj.getClass()))
			return Optional.empty();
		try {
			JsonObject json = ((ISerializationStrategy) clazzToStrategy.get(obj.getClass())).serialize(obj);
			json.add("type", new JsonPrimitive(clazzToStrategy.get(obj.getClass()).getTypeToken()));
			return Optional.of(json);
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}

	public void register(@Nonnull ISerializationStrategy<? extends T> strategy) {
		if (typeTokenToStrategy.containsKey(strategy.getTypeToken())) {
			throw new IllegalArgumentException("type token has already been registered to");
		}
		if (clazzToStrategy.containsKey(strategy.getSerializedClass())) {
			throw new IllegalArgumentException("class has already been registered to");
		}
		typeTokenToStrategy.put(strategy.getTypeToken(), strategy);
		clazzToStrategy.put(strategy.getSerializedClass(), strategy);
	}

}