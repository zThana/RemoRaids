package ca.landonjw.remoraids.internal.messages.placeholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class ParsingContext implements IParsingContext {

	private Map<Class<?>, List<Supplier<?>>> contextObjects;

	ParsingContext(Map<Class<?>, List<Supplier<?>>> contextObjects) {
		this.contextObjects = contextObjects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> getAssociation(@NonNull Class<T> clazz) {
		if (contextObjects.containsKey(clazz) && !contextObjects.get(clazz).isEmpty()) {
			if (clazz.isInstance(contextObjects.get(clazz).get(0).get())) {
				return Optional.of((T) contextObjects.get(clazz).get(0).get());
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<List<T>> getAllAssociations(@NonNull Class<T> clazz) {
		if (contextObjects.containsKey(clazz)) {
			// Create new list and populate it. This is to prevent any modification of the internal state.
			List<T> results = Lists.newArrayList();
			// Exclude any null results
			contextObjects.get(clazz).stream().filter((element) -> element != null && element.get() != null).forEach((element) -> results.add((T) element.get()));
			return Optional.of(results);
		}
		return Optional.empty();
	}

	@Override
	public Map<Class<?>, List<Supplier<?>>> getAllAssociations() {
		// Create new hash map and populate it. This is to prevent any modification of the internal state.
		return ImmutableMap.copyOf(this.contextObjects);
	}

	public static class ParsingContextBuilder implements IParsingContext.Builder {

		private Map<Class<?>, List<Supplier<?>>> contextObjects = new HashMap<>();

		@Override
		public <T> Builder add(@NonNull Class<T> clazz, @NonNull Supplier<T> supplier) {
			if (supplier.get() != null) {
				if (contextObjects.containsKey(clazz)) {
					contextObjects.get(clazz).add(supplier);
				} else {
					contextObjects.put(clazz, Lists.newArrayList(supplier));
				}
			}
			return this;
		}

		@Override
		public <T> Builder addAll(@NonNull Class<T> clazz, @NonNull List<Supplier<T>> supplierList) {
			if (contextObjects.containsKey(clazz)) {
				contextObjects.get(clazz).addAll(supplierList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
			} else {
				contextObjects.put(clazz, supplierList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
			}
			return this;
		}

		@Override
		public Builder from(IParsingContext input) {
			this.contextObjects = input.getAllAssociations();
			return this;
		}

		@Override
		public ParsingContext build() {
			return new ParsingContext(contextObjects);
		}

	}

}
