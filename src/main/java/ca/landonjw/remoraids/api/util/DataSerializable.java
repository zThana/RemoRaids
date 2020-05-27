package ca.landonjw.remoraids.api.util;

import ca.landonjw.remoraids.internal.storage.gson.JObject;

public interface DataSerializable<T> {

	JObject serialize();

}
