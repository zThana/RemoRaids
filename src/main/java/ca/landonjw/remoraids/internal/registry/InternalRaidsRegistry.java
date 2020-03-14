package ca.landonjw.remoraids.internal.registry;

import ca.landonjw.remoraids.api.registry.IRaidRegistry;
import ca.landonjw.remoraids.api.util.IBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class InternalRaidsRegistry implements IRaidRegistry {

	private static Map<Class<?>, Provider<?>> bindings = Maps.newHashMap();
	private static Map<Class<?>, Supplier<?>> builders = Maps.newHashMap();

	@Override
	public <T> void register(Class<T> type, T value) {
		Preconditions.checkNotNull(type, "Input type was null");
		Preconditions.checkNotNull(value, "Input value type was null");
		bindings.put(type, new Provider<>(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(Class<T> type) {
		Preconditions.checkArgument(!bindings.containsKey(type), "Could not locate a matching registration for type: " + type.getCanonicalName());
		return Optional.ofNullable((T) bindings.get(type).get());
	}

	@Override
	public <T extends IBuilder> void registerBuilderSupplier(Class<T> type, Supplier<? extends T> builder) {
		Preconditions.checkArgument(!builders.containsKey(type), "Already registered a builder supplier for: " + type.getCanonicalName());
		builders.put(type, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBuilder> T createBuilder(Class<T> type) {
		Preconditions.checkNotNull(type, "Input builder type was null");
		final Supplier<?> supplier = builders.get(type);
		Preconditions.checkNotNull(supplier, "Could not find a Supplier for the provided builder type: " + type.getCanonicalName());
		return (T) supplier.get();
	}
}
