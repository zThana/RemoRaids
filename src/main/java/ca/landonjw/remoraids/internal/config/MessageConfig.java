package ca.landonjw.remoraids.internal.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.api.config.ConfigKeyHolder;
import ca.landonjw.remoraids.internal.api.config.ConfigurationAdapter;
import ca.landonjw.remoraids.internal.api.config.KeyFactory;
import ca.landonjw.remoraids.internal.api.config.keys.BaseConfigKey;
import ca.landonjw.remoraids.internal.api.config.keys.FunctionalKey;

import static ca.landonjw.remoraids.internal.api.config.ConfigKeyTypes.booleanKey;

public class MessageConfig implements ConfigKeyHolder {

	// -------------------------------------------------------------------------------------------------
	//
	// Restraints Messages
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> CAPACITY_RESTRAINT_TITLE = translationKey("restraints.capacity-restraint-title", "&bCapacity Restraint");
	public static final ConfigKey<String> CAPACITY_RESTRAINT_WARNING = translationKey("restraints.capacity-restraint-warning", "&cThere are too many players currently battling the boss...");
	public static final ConfigKey<String> COOLDOWN_RESTRAINT_TITLE = translationKey("restraints.cooldown-restraint-title", "&bCooldown Constraint");
	public static final ConfigKey<String> COOLDOWN_RESTRAINT_WARNING = translationKey("restraints.cooldown-restraint-warning", "&cYou cannot battle the boss for another {cooldown|hours} hour(s), {cooldown|trimmed-minutes} minute(s) and {cooldown|trimmed-seconds} second(s)...");
	public static final ConfigKey<String> NO_REBATTLE_RESTRAINT_TITLE = translationKey("restraints.no-rebattle-restraint-title", "&bNo Rebattle Restraint");
	public static final ConfigKey<String> NO_REBATTLE_RESTRAINT_WARNING = translationKey("restraints.no-rebattle-restraint-warning", "&cYou cannot attempt to battle this raid boss again...");
	public static final ConfigKey<String> HALTED_BOSS_RESTRAINT_TITLE = translationKey("restraints.halted-boss-restraint-title", "&bHalted Boss Restraint");
	public static final ConfigKey<String> HALTED_BOSS_RESTRAINT_WARNING = translationKey("restraints.halted-boss-restraint-warning", "&cThis boss is not allowed to battle currently!");
	public static final ConfigKey<String> SPECIES_CLAUSE_RESTRAINT_TITLE = translationKey("restraints.species-clause-restraint-title", "&bSpecies Clause (Restraint)");
	public static final ConfigKey<String> SPECIES_CLAUSE_RESTRAINT_WARNING = translationKey("restraints.species-clause-restraint-warning", "&cYou cannot have multiple of the same species in your team!");
	public static final ConfigKey<String> BROKEN_CLAUSE_WARNING = translationKey("restraints.broken-clause-warning", "&cYou are breaking the banned clause: {battleclause}");

	// -------------------------------------------------------------------------------------------------
	//
	// Rewards Messages
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> TOP_DAMAGE_REWARD_TITLE = translationKey("rewards.top-damage-reward-title", "&aTop Damage Reward");
	public static final ConfigKey<String> KILLER_REWARD_TITLE = translationKey("rewards.killer-reward-title", "&aKiller Reward");
	public static final ConfigKey<String> PARTICIPATION_REWARD_TITLE = translationKey("rewards.participation-reward-title", "&aParticipation Reward");

	public static final ConfigKey<String> REWARD_RECEIVED = translationKey("rewards.reward-received", "&6You have received a &a&l{reward}&r&6! Click to receive!");
	public static final ConfigKey<String> CURRENCY_RECEIVED = translationKey("rewards.reward-contents.currency-received", "&aYou have received {integer} dollars!");
	public static final ConfigKey<String> POKEMON_RECEIVED = translationKey("rewards.reward-contents.pokemon-received", "&aYou have received a {pokemon}!");

	public static final ConfigKey<String> POKEMON_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.pokemon-reward-content-title", "Pokemon: {spec}");
	public static final ConfigKey<String> ITEM_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.item-reward-content-title", "{itemstack|name} (x {itemstack|count})");
	public static final ConfigKey<String> COMMAND_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.command-reward-content-title", "Command: {string}");
	public static final ConfigKey<String> CURRENCY_REWARD_CONTENT_TITLE = translationKey("rewards.reward-contents.currency-reward-content-title", "Currency: {integer}");

	// -------------------------------------------------------------------------------------------------
	//
	// Battle Display Messaging
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<List<String>> RESULTS_HEADER = listTranslationKey("battle-results.header", Lists.newArrayList("&8&m==============&r &c[Raid Results] &8&m==============", "&7Through valiant effort, the raid pokemon,", "&e{boss}&7, was defeated!", "", "&cKiller: &e{player}", "", "&aTop {integer} Damage Dealers:"));

	public static final ConfigKey<String> RESULTS_BODY_TOP_DAMAGE_CONTENT = translationKey("battle-results.body.top-damage-content", "&e{player}&7: &b{integer}");
	public static final ConfigKey<String> RESULTS_FOOTER = translationKey("battle-results.footer", "&8&m==================================");

	// -------------------------------------------------------------------------------------------------
	//
	// Battle Overlay Message
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<List<String>> OVERLAY_TEXT = listTranslationKey("battle-overlay.message", Lists.newArrayList("RemoRaids", "Default Overlay"));

	// -------------------------------------------------------------------------------------------------
	//
	// Entity Related Messaging
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> RAID_ENGAGE = translationKey("engage-message", "&a&lRelease a pokemon to engage the boss!");
	public static final ConfigKey<String> RAID_SPAWN_ANNOUNCE = translationKey("spawn-announcement", "&6&lA boss &a&l{boss} &6&lhas spawned!");

	// -------------------------------------------------------------------------------------------------
	//
	// Error Messaging
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> ERROR_CHISEL_INTERACT = translationKey("errors.chisel-interaction", "&cYou may not perform this action on a raid boss!");
	public static final ConfigKey<String> BOSS_IN_BATTLE_MODIFICATION = translationKey("errors.boss-in-battle-modification", "&cYou cannot modify this while boss is in battle!");

	// -------------------------------------------------------------------------------------------------
	//
	// UI Based Messaging
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_COMMON_NEXT_PAGE = translationKey("ui.common.next-page", "&bNext Page");
	public static final ConfigKey<String> UI_COMMON_CURR_PAGE = translationKey("ui.common.current-page", "&bPage {current} / {total}");
	public static final ConfigKey<String> UI_COMMON_PREVIOUS_PAGE = translationKey("ui.common.previous-page", "&bPrevious Page");
	public static final ConfigKey<String> UI_COMMON_EDIT_ELEMENT = translationKey("ui.common.edit-element", "&6Left click to edit");
	public static final ConfigKey<String> UI_COMMON_DELETE_ELEMENT = translationKey("ui.common.delete-element", "&cMiddle click to remove");
	public static final ConfigKey<String> UI_COMMON_BACK = translationKey("ui.common.back", "&cGo Back");

	public static final ConfigKey<String> UI_REGISTRY_TITLE = translationKey("ui.registry.title", "&1&lBoss Registry");

	// -------------------------------------------------------------------------------------------------
	//
	// Reward Contents UI
	//
	// -------------------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_ADD_REWARD_CONTENT_ITEM = translationKey("ui.add-reward-content.item.title", "&aAdd an Item");
	public static final ConfigKey<String> UI_ADD_REWARD_CONTENT_POKEMON = translationKey("ui.add-reward-content.pokemon.title", "&aAdd a Pokemon");
	public static final ConfigKey<String> UI_ADD_REWARD_CONTENT_COMMAND = translationKey("ui.add-reward-content.command.title", "&aAdd a Command");
	public static final ConfigKey<String> UI_ADD_REWARD_CONTENT_CURRENCY = translationKey("ui.add-reward-content.currency.title", "&aAdd some Currency");

	public static final ConfigKey<String> UI_POKEMON_GENERAL_TITLE = translationKey("ui.pokemon.general.title", "&e{pokemon|species}");
	public static final ConfigKey<List<String>> UI_POKEMON_GENERAL_LORE = listTranslationKey("ui.pokemon.general.lore", Lists.newArrayList("&7Level: &a{pokemon|level}", "&7Form: &a{pokemon|form}", "", "&7Ability: &a{pokemon|ability}", "&7Nature: &a{pokemon|nature}", "&7Gender: &a{pokemon|gender}", "&7Texture: &a{pokemon|texture}", "", "&cHP: {pokemon|hp} &7/ &6Atk: {pokemon|attack} &7/ &aDef: {pokemon|defence}", "&9SpAtk: {pokemon|spatk} &7/ &aSpDef: {pokemon|spdef} &7/ &dSpeed: {pokemon|speed}", "", "&7Moves:", "{pokemon|moveset}"));
	public static final ConfigKey<String> UI_RAID_BOSS_TITLE = translationKey("ui.pokemon.raid-boss.title", "&eBoss {pokemon|species}");
	public static final ConfigKey<List<String>> UI_RAID_BOSS_LORE = listTranslationKey("ui.pokemon.raid-boss.lore", Lists.newArrayList("&7Level: &a{pokemon|level}", "&7Form: &a{pokemon|form}", "&7Size: &a{boss|size}", "", "&7Ability: &a{pokemon|ability}", "&7Nature: &a{pokemon|nature}", "&7Gender: &a{pokemon|gender}", "&7Texture: &a{pokemon|texture}", "", "&cHP: {pokemon|hp} &7/ &6Atk: {pokemon|attack} &7/ &aDef: {pokemon|defence}", "&9SpAtk: {pokemon|spatk} &7/ &aSpDef: {pokemon|spdef} &7/ &dSpeed: {pokemon|speed}", "", "&7Moves:", "{pokemon|moveset}"));
	public static final ConfigKey<String> UI_POKEMON_NO_TEXTURE = translationKey("ui.pokemon.no-texture", "N/A");
	public static final ConfigKey<String> UI_POKEMON_MOVESET = translationKey("ui.pokemon.moveset", "&b{moveset|1}&7/&b{moveset|2}&7/&b{moveset|3}&7/&b{moveset|4}");

	// --------------------------------------------------------------------------------------
	//
	// Boss Options UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BOSS_OPTIONS_TITLE = translationKey("ui.boss-options.title", "&9&lOptions");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_TELEPORT_TITLE = translationKey("ui.boss-options.teleport.title", "&bTeleport");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_TELEPORT_MESSAGE = translationKey("ui.boss-options.teleport.message", "&aTeleporting to boss...");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_EDIT_TITLE = translationKey("ui.boss-options.edit", "&bEdit");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_HALT_BATTLES_TITLE = translationKey("ui.boss-options.halt-battles.title", "&cHalt Battles");
	public static final ConfigKey<List<String>> UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_FEATURE_UNAVAILABLE = listTranslationKey("ui.boss-options.halt-battles.stages.feature-unavailable", Lists.newArrayList("&7This feature is currently unavailable..."));
	public static final ConfigKey<List<String>> UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_ON = listTranslationKey("ui.boss-options.halt-battles.stages.feature-unavailable", Lists.newArrayList("&fToggled &aon"));
	public static final ConfigKey<List<String>> UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_OFF = listTranslationKey("ui.boss-options.halt-battles.stages.feature-unavailable", Lists.newArrayList("&fToggled &coff"));
	public static final ConfigKey<String> UI_BOSS_OPTIONS_HALT_BATTLES_KICK = translationKey("ui.boss-options.halt-battles.kick-from-battle", "&cYou have been kicked from battle due to ongoing boss editing...");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_DESPAWN_TITLE = translationKey("ui.boss-options.despawn.title", "&cDespawn");
	public static final ConfigKey<String> UI_BOSS_OPTIONS_DESPAWN_MESSAGE = translationKey("ui.boss-options.despawn.message", "&aBoss pokemon despawned!");

	// --------------------------------------------------------------------------------------
	//
	// Editor UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_EDITOR_TITLE = translationKey("ui.editor.title", "&1&lEditor");
	public static final ConfigKey<String> UI_EDITOR_RESPAWN_SETTINGS = translationKey("ui.editor.respawn-settings", "&bRespawn Settings");
	public static final ConfigKey<String> UI_EDITOR_GENERAL_SETTINGS = translationKey("ui.editor.general-settings", "&bGeneral Settings");
	public static final ConfigKey<String> UI_EDITOR_BATTLE_SETTINGS = translationKey("ui.editor.battle-settings", "&bBattle Settings");

	// --------------------------------------------------------------------------------------
	//
	// Respawn Settings UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_TITLE = translationKey("ui.respawn-settings.title", "&1&lRespawn Settings");
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_LIMIT_INCREASE_NECESSARY = translationKey("ui.respawn-settings.limit-increase-necessary", "&cYou must increase the respawn limit for this functionality");
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_EDIT_LIMIT = translationKey("ui.respawn-settings.edit-respawn-limit", "&bEdit Respawn Limit");
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_SET_LOCATION_TITLE = translationKey("ui.respawn-settings.set-respawn-location.title", "&bSet Respawn Location");
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_SET_LOCATION_MESSAGE = translationKey("ui.respawn-settings.set-respawn-location.message", "&aNew respawn location set.");
	public static final ConfigKey<String> UI_RESPAWN_SETTINGS_EDIT_COOLDOWN = translationKey("ui.respawn-settings.edit-respawn-cooldown", "&bEdit Respawn Cooldown");

	// --------------------------------------------------------------------------------------
	//
	// Respawn Limit Editor UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_RESPAWN_LIMIT_EDITOR_TITLE = translationKey("ui.respawn-limit-editor.title", "&a&lEdit Respawn Limit ({respawnvalue|total})");
	public static final ConfigKey<String> UI_RESPAWN_LIMIT_EDITOR_CURRENT_LIMIT = translationKey("ui.respawn-limit-editor.current-display.current-limit", "&bRespawn Limit: {respawnvalue|total}");
	public static final ConfigKey<String> UI_RESPAWN_LIMIT_EDITOR_REMAINING_RESPAWNS = translationKey("ui.respawn-limit-editor.remaining-respawns", "&fRemaining Respawns: {respawnvalue|remaining}");
	public static final ConfigKey<String> UI_RESPAWN_LIMIT_EDITOR_INCREASE = translationKey("ui.respawn-limit-editor.increase", "&bIncrease Spawn Limit");
	public static final ConfigKey<String> UI_RESPAWN_LIMIT_EDITOR_DECREASE = translationKey("ui.respawn-limit-editor.decrease", "&bDecrease Spawn Limit");

	// --------------------------------------------------------------------------------------
	//
	// Respawn Cooldown Editor UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_TITLE = translationKey("ui.respawn-cooldown-editor.title", "&1&lEdit Respawn Cooldown ({time|value} {time|unit})");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_INCREASE_UNIT = translationKey("ui.respawn-cooldown-editor.increase-unit", "&bNext Time Unit");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_CURRENT_UNIT = translationKey("ui.respawn-cooldown-editor.current-unit", "&bTime Unit: {time|unit}");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_DECREASE_UNIT = translationKey("ui.respawn-cooldown-editor.decrease-unit", "&bPrevious Time Unit");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_INCREASE_VALUE = translationKey("ui.respawn-cooldown-editor.increase-value", "&bIncrement Time Value");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_CURRENT_VALUE = translationKey("ui.respawn-cooldown-editor.current-value", "&bCooldown Time: {time|value}");
	public static final ConfigKey<String> UI_RESPAWN_COOLDOWN_EDITOR_DECREASE_VALUE = translationKey("ui.respawn-cooldown-editor.decrease-value", "&bDecrement Time Value");

	// --------------------------------------------------------------------------------------
	//
	// Respawn Cooldown Editor UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_GENERAL_SETTINGS_TITLE = translationKey("ui.general-settings.title", "&1&lGeneral Settings");
	public static final ConfigKey<String> UI_GENERAL_EDIT_STATS = translationKey("ui.general-settings.edit-stats", "&bEdit Stats");
	public static final ConfigKey<String> UI_GENERAL_EDIT_SIZE = translationKey("ui.general-settings.edit-size", "&bEdit Size");

	// --------------------------------------------------------------------------------------
	//
	// Boss Stat Editor
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BOSS_STAT_EDITOR_TITLE = translationKey("ui.boss-stat-editor.title", "&1&lEdit {stat} ({stat|value})");
	public static final ConfigKey<String> UI_BOSS_STAT_EDITOR_INCREASE = translationKey("ui.boss-stat-editor.increase", "&bIncrease Stat");
	public static final ConfigKey<String> UI_BOSS_STAT_EDITOR_DECREASE = translationKey("ui.boss-stat-editor.decrease", "&bDecrease Stat");

	// --------------------------------------------------------------------------------------
	//
	// Battle Stat Selection
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLE_STAT_SELECTION_TITLE = translationKey("ui.battle-stat-selection.title", "&1&lStat Selection");
	public static final ConfigKey<String> UI_BATTLE_STAT_SELECTION_EDIT_STAT = translationKey("ui.battle-stat-selection.edit-stat", "&bEdit {stat}");

	// --------------------------------------------------------------------------------------
	//
	// Boss Size Editor
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BOSS_SIZE_EDITOR_TITLE = translationKey("ui.boss-size-editor.title", "&1&lEdit Size ({boss|size})");
	public static final ConfigKey<String> UI_BOSS_SIZE_EDITOR_INCREASE = translationKey("ui.boss-size-editor.increase", "&bIncrease Size");
	public static final ConfigKey<String> UI_BOSS_SIZE_EDITOR_DECREASE = translationKey("ui.boss-size-editor.decrease", "&bDecrease Size");

	// --------------------------------------------------------------------------------------
	//
	// Add Restraint UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_ADD_RESTRAINT_TITLE = translationKey("ui.battles.add-restraint.title", "&9&lAdd Restraint");

	// --------------------------------------------------------------------------------------
	//
	// Add Reward Content UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_ADD_REWARD_CONTENT_TITLE = translationKey("ui.battles.add-reward-content.title", "&9&lAdd Reward Content");

	// --------------------------------------------------------------------------------------
	//
	// Add Reward UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_ADD_REWARD_TITLE = translationKey("ui.battles.add-reward.title", "&9&lAdd Reward");

	// --------------------------------------------------------------------------------------
	//
	// Battle Settings UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_BATTLE_SETTINGS_TITLE = translationKey("ui.battles.battle-settings.title", "&9&lBattle Settings");
	public static final ConfigKey<String> UI_BATTLES_BATTLE_SETTINGS_EDIT_RESTRAINTS = translationKey("ui.battles.battle-settings.edit-restraints", "&bEdit Restraints");
	public static final ConfigKey<String> UI_BATTLES_BATTLE_SETTINGS_EDIT_REWARDS = translationKey("ui.battles.battle-settings.edit-rewards", "&bEdit Rewards");

	// --------------------------------------------------------------------------------------
	//
	// Restraint Settings UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_RESTRAINT_SETTINGS_TITLE = translationKey("ui.battles.restraint-settings.title", "&9&lRestraint Settings");
	public static final ConfigKey<String> UI_BATTLES_RESTRAINT_SETTINGS_ADD_RESTRAINT = translationKey("ui.battles.restraint-settings.add-restraint", "&bAdd Restraint");

	// --------------------------------------------------------------------------------------
	//
	// Reward Content Settings UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_REWARD_CONTENT_SETTINGS_TITLE = translationKey("ui.battles.reward-content-settings.title", "&9&lReward Content Settings");
	public static final ConfigKey<String> UI_BATTLES_REWARD_CONTENT_SETTINGS_ADD_REWARD_CONTENT = translationKey("ui.battles.reward-content-settings.add-reward-content", "&bAdd Reward Content");

	// --------------------------------------------------------------------------------------
	//
	// Reward Settings UI
	//
	// --------------------------------------------------------------------------------------
	public static final ConfigKey<String> UI_BATTLES_REWARD_SETTINGS_TITLE = translationKey("ui.battles.reward-settings.title", "&9&lReward Settings");
	public static final ConfigKey<String> UI_BATTLES_REWARD_SETTINGS_ADD_REWARD = translationKey("ui.battles.reward-settings.add-reward-content", "&bAdd Reward");

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
