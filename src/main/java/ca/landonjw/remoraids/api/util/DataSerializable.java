package ca.landonjw.remoraids.api.util;

import ca.landonjw.remoraids.api.util.gson.JObject;

/**
 * A DataSerializable object represents something that can be directly output to JSON data. The intention of
 * this design is to satisfy a marker of sorts, specifying that some implementation is intended to be serialized
 * into JSON at some point.
 */
public interface DataSerializable {

	/**
	 * Serializes an object down to a raw set of JSON data. The return type of a {@link JObject JObject} allows
	 * for the dynamic building of the output JSON, giving finer controls and the assistance to avoid adapters for
	 * abstract objects.
	 *
	 * @return A composed set of data that will be easily converted to JSON when writing is desired.
	 */
	JObject serialize();

}
