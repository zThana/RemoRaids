package ca.landonjw.remoraids.internal.config;

import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.customKey;
import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.listKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.battles.rules.clauses.AbilityClause;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.rules.clauses.ItemPreventClause;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.api.config.ConfigKeyHolder;
import ca.landonjw.remoraids.internal.api.config.keys.BaseConfigKey;

public class RestraintsConfig implements ConfigKeyHolder {

	public static ConfigKey<List<String>> DISABLED_BOSS_MOVES = listKey("disabled-moves.raid-boss", Lists.newArrayList("Aqua Ring", "Ingain", "Recover", "Rest", "Shore Up", "Soft-Boiled", "Synthesis"));
	public static ConfigKey<List<String>> DISABLED_PLAYER_MOVES = listKey("disabled-moves.player", Lists.newArrayList("Endeavor", "Pain Split", "Leech Seed", "Perish Song", "Whirlpool", "Constrict", "Infestation", "Fire Spin", "Natures Madness", "Super Fang", "Sheer Cold", "Fissure", "Horn Drill", "Guillotine", "Power Swap", "Guard Swap", "Heal Pulse", "Present", "Floral Healing", "Spiky Shield", "Imprison", "Transform", "Destiny Bond", "Poison Gas", "Entrainment", "Glare", "Grass Whistle", "Hypnosis", "Lovely Kiss", "Poison Powder", "Psycho Shift", "Roar", "Whirlwind", "Sing", "Skill Swap", "Spore", "Stun Spore", "Thunder Wave", "Toxic", "Will-O-Wisp", "Yawn", "Magma Storm", "Bind", "Clamp", "Sand Tomb", "Wrap"));
	public static ConfigKey<List<StatusType>> DISABLED_STATUSES = customKey(adapter -> adapter.getStringList("disabled-statuses", Lists.newArrayList("Poison", "PoisonBadly", "Burn", "Paralysis", "Freeze", "Sleep", "GrassyTerrain", "Sandstorm", "Hail", "Cursed", "Imprison")).stream().map(x -> Optional.ofNullable(StatusType.getStatusEffect(x)).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList()));

	@SuppressWarnings("unchecked")
	public static ConfigKey<List<BattleClause>> BANNED_CLAUSES = customKey(adapter -> {
		List<AbilityClause> abilities = adapter.getStringList("banned-clauses.abilities", Lists.newArrayList("Aftermath", "Imposter", "Iron Barbs", "Rough Skin")).stream().map(in -> {
			AbilityBase base = AbilityBase.getAbility(in).orElse(null);
			if (base != null) {
				RemoRaids.logger.info("\u00a7eRemoRaids \u00a77\u00bb \u00a7cLocated ability: " + in);
				return new AbilityClause(in, base.getClass());
			}

			RemoRaids.logger.warn("\u00a7eRemoRaids \u00a77\u00bb \u00a7cFailed to locate an ability: " + in);
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		List<ItemPreventClause> items = adapter.getStringList("banned-clauses.held-items", Lists.newArrayList("Rocky Helmet", "Sticky Barb")).stream().map(in -> {
			try {
				EnumHeldItems item = Arrays.stream(EnumHeldItems.values()).filter(x -> x.name().toLowerCase().equals(in.toLowerCase().replace(" ", ""))).findAny().orElse(null);
				if (item != null) {
					RemoRaids.logger.info("\u00a7eRemoRaids \u00a77\u00bb \u00a7cLocated item: " + in);
					return new ItemPreventClause(in, item);
				}

				RemoRaids.logger.warn("\u00a7eRemoRaids \u00a77\u00bb \u00a7cFailed to locate an item: " + in);
				return null;
			} catch (Exception e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

		List<BattleClause> clauses = Lists.newArrayList();
		clauses.addAll(abilities);
		clauses.addAll(items);
		return clauses;
	});

	private static final Map<String, ConfigKey<?>> KEYS;
	private static final int SIZE;

	static {
		Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();
		Field[] values = RestraintsConfig.class.getFields();
		int i = 0;

		for (Field f : values) {
			// ignore non-static fields
			if (!Modifier.isStatic(f.getModifiers()))
				continue;

			// ignore fields that aren't configkeys
			if (!ConfigKey.class.equals(f.getType()))
				continue;

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
