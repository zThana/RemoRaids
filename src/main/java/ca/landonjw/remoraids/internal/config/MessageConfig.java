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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageConfig implements ConfigKeyHolder {

    // -------------------------------------------------------------------------------------------------
    //
    //                                      Restraints Messages
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> CAPACITY_RESTRAINT_TITLE = translationKey("restraints.capacity-restraint-title", "Capacity Restraint");
    public static final ConfigKey<String> CAPACITY_RESTRAINT_WARNING = translationKey("restraints.capacity-restraint-warning", "&cThere are too many players currently battling the boss...");
    public static final ConfigKey<String> COOLDOWN_RESTRAINT_TITLE = translationKey("restraints.cooldown-restraint-title", "&cYou cannot battle the boss for another {hours} hour(s), {trimmed-minutes} minute(s) and {trimmed-seconds} second(s)...");
    public static final ConfigKey<String> COOLDOWN_RESTRAINT_WARNING = translationKey("restraints.cooldown-restraint-warning", "&cYou cannot battle the boss for another {hours} hour(s), {trimmed-minutes} minute(s) and {trimmed-seconds} second(s)...");
    public static final ConfigKey<String> NO_REBATTLE_RESTRAINT_TITLE = translationKey("restraints.no-rebattle-restraint-title", "No Rebattle Restraint");
    public static final ConfigKey<String> NO_REBATTLE_RESTRAINT_WARNING = translationKey("restraints.no-rebattle-restraint-warning", "&cYou cannot attempt to battle this raid boss again...");
    public static final ConfigKey<String> HALTED_BOSS_RESTRAINT_TITLE = translationKey("restraints.halted-boss-restraint-title", "Halted Boss Restraint");
    public static final ConfigKey<String> HALTED_BOSS_RESTRAINT_WARNING = translationKey("restraints.halted-boss-restraint-warning", "&cThis boss is not allowed to battle currently!");

    // -------------------------------------------------------------------------------------------------
    //
    //                                       Rewards Messages
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> REWARD_RECEIVED = translationKey("rewards.reward-received", "&6You have received a &a&l{reward}&r&6! Click to receive!");
    public static final ConfigKey<String> TOP_DAMAGE_REWARD_TITLE = translationKey("rewards.top-damage-reward-title", "Top Damage Reward");
    public static final ConfigKey<String> KILLER_REWARD_TITLE = translationKey("rewards.killer-reward-title", "Killer Reward");
    public static final ConfigKey<String> PARTICIPATION_REWARD_TITLE = translationKey("rewards.participation-reward-title", "Participation Reward");


    public static final ConfigKey<String> CURRENCY_RECEIVED = translationKey("rewards.reward-contents.currency-received", "&aYou have received {integer} dollars!");
    public static final ConfigKey<String> POKEMON_RECEIVED = translationKey("rewards.reward-contents.pokemon-received", "&aYou have received a {pokemon}!");
    public static final ConfigKey<String> POKEMON_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.pokemon-reward-content-title", "Pokemon: {spec}");
    public static final ConfigKey<String> ITEM_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.item-reward-content-title", "{itemstack|name} (x {itemstack|count})");
    public static final ConfigKey<String> COMMAND_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.command-reward-content-title", "Command: {string}");
    public static final ConfigKey<String> CURRENCY_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.currency-reward-content-title", "Currency: {integer}");

    // -------------------------------------------------------------------------------------------------
    //
    //                                   Battle Display Messaging
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> RESULTS_HEADER = translationKey("battle-results.header", "&8&m==============&r &c[Raid Results] &8&m==============");
    public static final ConfigKey<String> RESULTS_BODY_DESC = translationKey("battle-results.body.description", "&7Through valiant effort, the raid pokemon,\\n&e{boss}&7, was defeated!");
    public static final ConfigKey<String> RESULTS_BODY_KILLER = translationKey("battle-results.body.killer", "&cKiller: &e{player}");
    public static final ConfigKey<String> RESULTS_BODY_TOP_DAMAGE_LABEL = translationKey("battle-results.body.top-damage-label", "&aTop {integer} Damage Dealers:");
    public static final ConfigKey<String> RESULTS_BODY_TOP_DAMAGE_CONTENT = translationKey("battle-results.body.top-damage-content", "&e{player}&7: &b{integer}");
    public static final ConfigKey<String> RESULTS_FOOTER = translationKey("battle-results.footer", "&8&m==================================");

    // -------------------------------------------------------------------------------------------------
    //
    //                                   Entity Related Messaging
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> RAID_ENGAGE = translationKey("engage-message", "&a&lRelease a pokemon to engage the boss!");
    public static final ConfigKey<String> RAID_SPAWN_ANNOUNCE = translationKey("spawn-announcement", "&6&lA boss &a&l{boss} &6&lhas spawned!");

    // -------------------------------------------------------------------------------------------------
    //
    //                                       Error Messaging
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> ERROR_CHISEL_INTERACT = translationKey("errors.chisel-interaction", "&cYou may not perform this action on a raid boss!");

	// -------------------------------------------------------------------------------------------------
    //
    //                                      UI Based Messaging
    //
    // -------------------------------------------------------------------------------------------------
    public static final ConfigKey<String> UI_COMMON_NEXT_PAGE = translationKey("ui.common.next-page", "&bNext Page");
    public static final ConfigKey<String> UI_COMMON_CURR_PAGE = translationKey("ui.common.current-page", "&bPage {current} / {total}");
    public static final ConfigKey<String> UI_COMMON_PREVIOUS_PAGE = translationKey("ui.common.previous-page", "&bPrevious Page");
    public static final ConfigKey<String> UI_COMMON_EDIT_ELEMENT = translationKey("ui.common.edit-element", "&6Left click to edit {string}");
    public static final ConfigKey<String> UI_COMMON_DELETE_ELEMENT = translationKey("ui.common.delete-element", "&cMiddle click to remove {string}");
    public static final ConfigKey<String> UI_COMMON_BACK = translationKey("ui.common.back", "&cGo Back");

    public static final ConfigKey<String> UI_REGISTRY_TITLE = translationKey("ui.registry.title", "&1&lBoss Registry");
    public static final ConfigKey<String> UI_REGISTRY_BOSS_TITLE = translationKey("ui.registry.boss.title", "&b&lBoss {boss}");

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

    private static ListTranslationKey listTranslationKey(String path, List<String> def) {
        KeyFactory<List<String>> factory = ConfigurationAdapter::getStringList;
        return new ListTranslationKey(factory, path, def);
    }

    private static class ListTranslationKey extends FunctionalKey<List<String>> implements ConfigKey<List<String>> {

        ListTranslationKey(KeyFactory<List<String>> factory, String path, List<String> def) {
            super(factory, path, def);
        }

        @Override
        public List<String> get(ConfigurationAdapter adapter) {
            return super.get(adapter).stream().map(x -> x.replace("&", "\u00a7")).collect(Collectors.toList());
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
