package ca.landonjw.remoraids.internal.config;

import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.booleanKey;
import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.customKey;
import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.intKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.api.config.ConfigKeyHolder;
import ca.landonjw.remoraids.internal.api.config.keys.BaseConfigKey;

public class GeneralConfig implements ConfigKeyHolder {

	public static final ConfigKey<Boolean> ANNOUNCEMENTS_ENABLED = booleanKey("announcements.enabled", true);
	public static final ConfigKey<Boolean> ANNOUNCEMENTS_DISPLAY_BATTLE_SUMMARY_TO_SERVER = booleanKey("announcements.display-battle-summary-to-server", true);
	public static final ConfigKey<Boolean> ANNOUNCEMENTS_ALLOW_TP = booleanKey("announcements.allow-teleport", false);

	public static final ConfigKey<Boolean> OVERLAY_ENABLED = booleanKey("battle-overlay.enabled", false);

	public static final ConfigKey<Boolean> USE_CALLBACK = booleanKey("callback.use", true);

	public static final ConfigKey<Integer> ENGAGE_MESSAGE_TYPE = intKey("engaging.message-type", 1);
	public static final ConfigKey<Float> ENGAGE_RANGE = customKey(adapter -> {
		double in = adapter.getDouble("engaging.range", 10.0);
		return (float) in;
	});

	public static final ConfigKey<Integer> KILLER_REWARD_PRIORITY = intKey("reward-priority.killer-reward", 3);
	public static final ConfigKey<Integer> TOP_DAMAGE_REWARD_PRIORITY = intKey("reward-priority.top-damage-reward", 2);
	public static final ConfigKey<Integer> PARTICIPATION_REWARD_PRIORITY = intKey("reward-priority.participation-reward", 1);

	private static final Map<String, ConfigKey<?>> KEYS;
	private static final int SIZE;

	static {
		Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();
		Field[] values = GeneralConfig.class.getFields();
		int i = 0;

		for (Field f : values) {
			// ignore non-static fields
			if (!Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			// ignore fields that aren't configkeys
			if (!ConfigKey.class.equals(f.getType())) {
				continue;
			}

			try {
				// get the key instance
				BaseConfigKey<?> key = (BaseConfigKey<?>) f.get(null);
				// set the ordinal value of the key.
				key.ordinal = i++;
				// add the key to the return map
				keys.put(f.getName(), key);
			} catch (Exception e) {
				throw new RuntimeException("Exception processing field: " + f, e);
			}
		}

		KEYS = ImmutableMap.copyOf(keys);
		SIZE = i;
	}

	/**
	 * Gets a map of the keys defined in this class.
	 *
	 * <p>
	 * The string key in the map is the {@link Field#getName() field name}
	 * corresponding to each key.
	 * </p>
	 *
	 * @return the defined keys
	 */
	@Override
	public Map<String, ConfigKey<?>> getKeys() {
		return KEYS;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

}
