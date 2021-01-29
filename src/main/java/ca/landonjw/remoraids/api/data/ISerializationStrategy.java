package ca.landonjw.remoraids.api.data;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

public interface ISerializationStrategy<T> {

	/**
	 * Gets the token of the type. This should be unique for the set of object types that are to be serialized.
	 * This will be included with the serialized object in order to reference this strategy later during
	 * deserialization.
	 * <p>
	 * You do not need to explicitly set this as a property upon serialization.
	 *
	 * @return the unique type token for the serialized object
	 */
	String getTypeToken();

	/**
	 * Gets the class of the objects to be serialized. This helps in delegating which objects
	 * should be routed to be serialized by this strategy. This should ALWAYS be the lowest level
	 * class in the inheritance structure that is being serialized.
	 *
	 * @return the class of the objects to be serialized.
	 */
	Class<T> getSerializedClass();

	/**
	 * Defines the logic necessary to serialize the object.
	 *
	 * @param obj object to be serialized
	 * @return the json result of the object being serialized
	 */
	JsonObject serialize(T obj);

	/**
	 * Defines the logic necessary to deserialize the data into an object.
	 *
	 * @param data the json data to be deserialized
	 * @return the resulting object deserialized
	 */
	T deserialize(@Nonnull JsonObject data);

}