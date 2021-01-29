package ca.landonjw.remoraids.internal.obfuscation;

import java.lang.reflect.Field;

import ca.landonjw.remoraids.RemoRaids;

public class Remapper {

	public static <T, E> void setPrivateValue(Class<? super T> clazz, T instance, E value, int fieldIndex) {
		try {
			Field f = clazz.getDeclaredFields()[fieldIndex];
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception e) {
			RemoRaids.logger.error("There was a problem setting field index {} on type {}", fieldIndex, clazz.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
