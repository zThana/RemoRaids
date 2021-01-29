package ca.landonjw.remoraids.api.registry;

import java.util.Optional;
import java.util.function.Supplier;

import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * Represents a collection tool that maps objects to an interface or referencable typing. The goal
 * of the raid registry is to provide a common area for objects that can be referenced in a simple
 * call.
 *
 * @author NickImpact
 * @since 1.0.0
 */
public interface IRaidRegistry {

	/**
	 * Allows a system to bind a type marking with the actual intended value to be received. This allows
	 * the API to provide objects that are implemented elsewhere, so the main face of the API can simply
	 * be declaration and informative.
	 *
	 * @param type  The type we are registering to the registry
	 * @param value The intended output object for this type marking
	 * @param <T>   The common inherited type between the type marking and implementation
	 */
	<T> void register(Class<T> type, T value);

	/**
	 * Retrieves an object bound to the registry based on the input typing, should any exist. If a typing
	 * is not within the API, this call will return an empty optional.
	 *
	 * @param type The input type to match and find
	 * @param <T>  The type inherited by the type marking and implementation
	 * @return A Optionally wrapped instance bound to the input type, or an empty Optional
	 */
	<T> Optional<T> get(Class<T> type);

	/**
	 * Retrieves an object bound to the registry based on the input typing, with assumption that the typing
	 * will exist. Should the type pairing not exist, this call will throw a runtime exception to indicate
	 * the type was not found, rather than throw an error from the optional based on an improper {@link Optional#get() get}
	 * call.
	 *
	 * @param type The input type to match and find
	 * @param <T>  The type inherited by the type marking and implementation
	 * @return The bound typing
	 * @throws IllegalArgumentException If the input type had no registered instance
	 */
	default <T> T getUnchecked(Class<T> type) {
		return this.get(type).orElseThrow(() -> new IllegalArgumentException("Type not registered: " + type.getCanonicalName()));
	}

	/**
	 * Registers a builder that will be created when a user wishes to create a new instance.
	 *
	 * @param type    The type of builder being registered
	 * @param builder The implementation of the builder
	 * @param <T>     The common type pairing between the declaration and implementation
	 */
	<T extends IBuilder<?, ?>> void registerBuilderSupplier(Class<T> type, Supplier<? extends T> builder);

	/**
	 * Constructs a new builder based on the input builder type.
	 *
	 * @param type The type of builder to create
	 * @param <T>  The common type matching between declaration and implementation
	 * @return A new builder matching the input typing
	 * @throws IllegalArgumentException If no builder is available that matches that typing
	 */
	<T extends IBuilder<?, ?>> T createBuilder(Class<T> type);

	/**
	 * Registers a builder specific to the creation of a boss spawner. Unlike all other builders, these builders
	 * are referenced via a String key. This is to allow for dynamic loading during deserialization of a boss
	 * spawner.
	 *
	 * @param key     The key this spawner should be attached to. This key is what will be referenced when attempting
	 *                to deserialize the spawner.
	 * @param builder The supplier that will provide the new instance of a builder.
	 * @param <T>     An implementation of the {@link ca.landonjw.remoraids.api.spawning.IBossSpawner.IBossSpawnerBuilder
	 *                IBossSpawnerBuilder}
	 */
	<T extends IBossSpawner.IBossSpawnerBuilder> void registerSpawnerBuilderSupplier(String key, Supplier<T> builder);

	/**
	 * Using the specified key, creates a new builder from the mapping of suppliers, should the key
	 * exist.
	 *
	 * @param key The key this spawner should be attached to. This key is what will be referenced when attempting
	 *            to deserialize the spawner.
	 * @param <T> An implementation of the {@link ca.landonjw.remoraids.api.spawning.IBossSpawner.IBossSpawnerBuilder
	 *            IBossSpawnerBuilder}
	 * @return A newly created builder mapped to the specific key
	 * @throws IllegalArgumentException If no builder is available that matches that key
	 */
	<T extends IBossSpawner.IBossSpawnerBuilder> T createSpawnerBuilder(String key);

	/**
	 * This class acts as a wrapper to avoid Java Generic issues. This should really only be handled
	 * via the internals of RemoRaids, and isn't a requirement for an outside developer to use.
	 *
	 * @param <T> The type that is being wrapped into this provider
	 */
	class Provider<T> {

		/** Represents the wrapped instance */
		private T instance;

		/**
		 * Constructs a new provider wrapping the input argument
		 *
		 * @param input The instance we wish to store in this wrapper
		 */
		public Provider(T input) {
			this.instance = input;
		}

		/**
		 * Retrieves the instance stored within this wrapper.
		 *
		 * @return The stored instance
		 */
		public T get() {
			return this.instance;
		}

	}

}
