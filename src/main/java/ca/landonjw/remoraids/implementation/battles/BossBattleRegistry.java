package ca.landonjw.remoraids.implementation.battles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Implementation for {@link IBossBattleRegistry}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossBattleRegistry implements IBossBattleRegistry {

	/** The instance of the battle registry. */
	private static BossBattleRegistry instance;

	/** Map of all boss entity's and their corresponding battles. */
	private Map<IBossEntity, IBossBattle> registry = new HashMap<>();

	/**
	 * Creates a boss battle for the boss and adds it to the registry.
	 *
	 * @param boss the boss to create battle for
	 * @return the battle that is created
	 */
	public IBossBattle createBossBattle(@Nonnull IBossEntity boss) {
		if (registry.containsKey(boss)) {
			throw new IllegalStateException("boss is already registered in the battle registry");
		}

		BossBattle battle = new BossBattle(boss);
		registry.put(boss, battle);
		return battle;
	}

	/**
	 * Removes a boss battle from the registry.
	 *
	 * @param boss the boss to remove battle for
	 */
	public void removeBossBattle(@Nullable IBossEntity boss) {
		if (registry.containsKey(boss)) {
			IBossBattle battle = registry.get(boss);
			battle.endAllBattles();
			registry.remove(boss);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Optional<IBossBattle> getBossBattle(@Nonnull IBossEntity boss) {
		return Optional.ofNullable(registry.get(boss));
	}

	/** {@inheritDoc} */
	@Override
	public Optional<IBossBattle> getBossBattle(@Nonnull EntityPlayerMP player) {
		for (IBossBattle battle : registry.values()) {
			if (battle.containsPlayer(player)) {
				return Optional.of(battle);
			}
		}
		return Optional.empty();
	}

	/** {@inheritDoc} */
	@Override
	public List<IBossBattle> getAllBossBattles() {
		return new ArrayList<>(this.registry.values());
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPlayerInBattle(@Nonnull EntityPlayerMP player) {
		return getBossBattle(player).isPresent();
	}

	/**
	 * Gets the battle registry instance.
	 *
	 * @return the battle registry instance
	 */
	public static BossBattleRegistry getInstance() {
		if (instance == null) {
			instance = new BossBattleRegistry();
		}
		return instance;
	}

}
