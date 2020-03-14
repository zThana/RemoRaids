package ca.landonjw.remoraids.api.util;

/**
 * A builder follows the concepts of the typical Builder design.
 *
 * @param <T> The type of object being built
 * @param <B> A callback type to the implementing builder
 */
public interface IBuilder<T, B> {

	B from(T input);

	T build();

}
