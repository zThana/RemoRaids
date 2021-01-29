package ca.landonjw.remoraids.api;

import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.api.registry.IRaidRegistry;

/**
 * A general basis for components that this plugin will communicate with.
 *
 * @author NickImpact
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossAPI {

	/**
	 * Provides a reference to the established implementation of this API. To access the API at all,
	 * it is expected that you reference this method to receive access to it.
	 *
	 * NOTE: The API is provided during the {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent
	 * PreInitialization Event}, so you should only attempt to access this API after this event has processed
	 * for raids. Any earlier attempts will result in this call returning exceptionally.
	 *
	 * @return The current instance of the API
	 */
	static IBossAPI getInstance() {
		return BossAPIProvider.get();
	}

	/**
	 * Fetches an instance of the internal registry raids establishes in order to manage different
	 * components of the mod. Given this is a forge-based mod, this registry is designed with the idea
	 * that it'll model the functionality that the Sponge registry service provides.
	 *
	 * @return The internal registry provided by the mod
	 */
	IRaidRegistry getRaidRegistry();

	/**
	 * Represents the registry that is responsible for managing the many raid boss entities that are spawned
	 * in the world at a present moment. This is namely useful for cases where you are tracking a specific
	 * raid boss, or want to simply find the entities still tracked as raid bosses by the plugin.
	 *
	 * @return The internal registry managing the spawned in raid boss entities
	 */
	IBossEntityRegistry getBossEntityRegistry();

	/**
	 * Represents the registry of all on-going battles involving a raid boss. By default, a raid boss that is spawned in
	 * will always appear in the registry. For players, they can be found as soon as they engage a raid boss.
	 *
	 * @return The internal registry mapping the set of raid boss battles that are currently on-going
	 */
	IBossBattleRegistry getBossBattleRegistry();

}