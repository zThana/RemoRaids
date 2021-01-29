package ca.landonjw.remoraids.internal.registry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import ca.landonjw.remoraids.api.registry.IRaidRegistry;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.util.IBuilder;

public class InternalRaidsRegistry implements IRaidRegistry {

	private Map<Class<?>, Provider<?>> bindings = Maps.newHashMap();
	private Map<Class<?>, Supplier<? extends IBuilder<?, ?>>> builders = Maps.newHashMap();

	private Map<String, Supplier<? extends IBuilder<?, ?>>> spawnerBuilders = Maps.newHashMap();

	@Override
	public <T> void register(Class<T> type, T value) {
		Preconditions.checkNotNull(type, "Input type was null");
		Preconditions.checkNotNull(value, "Input value type was null");
		bindings.put(type, new Provider<>(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(Class<T> type) {
		Preconditions.checkArgument(bindings.containsKey(type), "Could not locate a matching registration for type: " + type.getCanonicalName());
		return Optional.ofNullable((T) bindings.get(type).get());
	}

	@Override
	public <T extends IBossSpawner.IBossSpawnerBuilder> void registerSpawnerBuilderSupplier(String key, Supplier<T> builder) {
		this.spawnerBuilders.put(key, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBossSpawner.IBossSpawnerBuilder> T createSpawnerBuilder(String key) {
		Preconditions.checkNotNull(key, "Input builder key was null");
		final Supplier<? extends IBuilder<?, ?>> supplier = spawnerBuilders.get(key);
		if (supplier == null) {
			throw new IllegalArgumentException("No supplier available for key: " + key);
		}
		return (T) supplier.get();
	}

	@Override
	public <T extends IBuilder<?, ?>> void registerBuilderSupplier(Class<T> type, Supplier<? extends T> builder) {
		Preconditions.checkArgument(!builders.containsKey(type), "Already registered a builder supplier for: " + type.getCanonicalName());
		builders.put(type, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBuilder<?, ?>> T createBuilder(Class<T> type) {
		Preconditions.checkNotNull(type, "Input builder type was null");
		final Supplier<? extends IBuilder<?, ?>> supplier = builders.get(type);
		if (supplier == null) {
			throw new IllegalArgumentException("No supplier available for type: " + type.getCanonicalName());
		}
		return (T) supplier.get();
	}
}
