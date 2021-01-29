package ca.landonjw.remoraids.api.messages.placeholders;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * Represents the context of a given parsing attempt. May contain one or many objects to be interacted with by a
 * {@link IPlaceholderParser}.
 *
 * <p>
 * This can be appended to {@link IMessageService#interpret(String, IParsingContext)} in order to supply
 * additional information to be used to parse a placeholder.
 * </p>
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IParsingContext {

	/**
	 * Gets an object of the specified class, if available. If there is more than one of the
	 * objects stored, it will grab the first one available.
	 *
	 * @param clazz the class to get object for
	 * @param <T>   the type of object to get
	 * @return first available object of specified class, or {@link Optional#empty()} if none are found.
	 */
	<T> Optional<T> getAssociation(@NonNull Class<T> clazz);

	/**
	 * Gets a list of all objects of the specified class, if available.
	 * Modifying this list should not affect the implementation in any way.
	 *
	 * @param clazz the class to get object for
	 * @param <T>   the type of object to get
	 * @return list of objects of specified class, or {@link Optional#empty()} if none are found.
	 */
	<T> Optional<List<T>> getAllAssociations(@NonNull Class<T> clazz);

	/**
	 * Gets a map of all stored classes and their collection of objects.
	 * Modifying this map should not affect the implementation in any way.
	 *
	 * @return map of all stored classes and their collection of objects
	 */
	Map<Class<?>, List<Supplier<?>>> getAllAssociations();

	static Builder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(Builder.class);
	}

	interface Builder extends IBuilder<IParsingContext, Builder> {

		<T> Builder add(@NonNull Class<T> clazz, @NonNull Supplier<T> supplier);

		<T> Builder addAll(@NonNull Class<T> clazz, @NonNull List<Supplier<T>> supplierList);

	}

}
