package ca.landonjw.remoraids.api.services.placeholders;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.util.IBuilder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents the context of a given parsing attempt. May contain one or many objects to be interacted with by a
 * {@link IPlaceholderParser}.
 *
 * <p>This can be appended to {@link IMessageService#interpret(String, IParsingContext)} in order to supply
 * additional information to be used to parse a placeholder.</p>
 *
 * @author landonjw
 * @since  1.0.0
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
    <T> Optional<T> get(@Nonnull Class<T> clazz);

    /**
     * Gets a list of all objects of the specified class, if available.
     * Modifying this list should not affect the implementation in any way.
     *
     * @param clazz the class to get object for
     * @param <T>   the type of object to get
     * @return list of objects of specified class, or {@link Optional#empty()} if none are found.
     */
    <T> Optional<List<T>> getAll(@Nonnull Class<T> clazz);

    /**
     * Gets a map of all stored classes and their collection of objects.
     * Modifying this map should not affect the implementation in any way.
     *
     * @return map of all stored classes and their collection of objects
     */
    Map<Class, List<Supplier>> getAll();

    static Builder builder() {
        return IBossAPI.getInstance().getRaidRegistry().createBuilder(Builder.class);
    }

    interface Builder extends IBuilder<IParsingContext, Builder> {

        <T> Builder add(@Nonnull Class<T> clazz, @Nonnull Supplier<T> supplier);

        <T> Builder addAll(@Nonnull Class<T> clazz, @Nonnull List<Supplier<T>> supplierList);

    }

}
