package ca.landonjw.remoraids.internal.config;

import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.api.config.ConfigKeyHolder;
import ca.landonjw.remoraids.internal.api.config.ConfigurationAdapter;
import ca.landonjw.remoraids.internal.api.config.KeyFactory;
import ca.landonjw.remoraids.internal.api.config.keys.BaseConfigKey;
import ca.landonjw.remoraids.internal.api.config.keys.FunctionalKey;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class MessageConfig implements ConfigKeyHolder {

    public static final ConfigKey<String> RAID_CAPACITY_REACHED = translationKey("restraints.capacity-reached", "&cThere are too many players currently battling the boss...");
    public static final ConfigKey<String> RAID_COOLDOWN = translationKey("restraints.cooldown", "&cYou cannot battle the boss for another {minute} minute(s) and {trimmed-seconds} second(s)...");
    public static final ConfigKey<String> RAID_NO_REBATLLE = translationKey("restraints.no-rebattle", "&cYou cannot attempt to battle this raid boss again...");

    public static final ConfigKey<String> RAID_ENGAGE = translationKey("engage-message", "&a&lRelease a pokemon to engage the boss!");
    public static final ConfigKey<String> RAID_SPAWN_ANNOUNCE = translationKey("spawn-announcement", "&6&lA boss &a&l{boss-species} &6&lhas spawned!");

    private static TranslationKey translationKey(String path, String def) {
        KeyFactory<String> factory = ConfigurationAdapter::getString;
        return new TranslationKey(factory, path, def);
    }

    private static class TranslationKey extends FunctionalKey<String> implements ConfigKey<String> {

        TranslationKey(KeyFactory<String> factory, String path, String def) {
            super(factory, path, def);
        }

        @Override
        public String get(ConfigurationAdapter adapter) {
            return super.get(adapter).replace("&", "\u00a7");
        }
    }

    private static final Map<String, ConfigKey<?>> KEYS;
    private static final int SIZE;

    static {
        Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();
        Field[] values = MessageConfig.class.getFields();
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
     * <p>The string key in the map is the {@link Field#getName() field name}
     * corresponding to each key.</p>
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
