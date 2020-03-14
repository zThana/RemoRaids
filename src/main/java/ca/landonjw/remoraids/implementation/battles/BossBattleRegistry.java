package ca.landonjw.remoraids.implementation.battles;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BossBattleRegistry implements IBossBattleRegistry {

    /** The instance of the battle registry. */
    private static BossBattleRegistry instance;

    /** Map of all boss entity's and their corresponding battles. */
    private Map<IBossEntity, IBossBattle> registry = new HashMap<>();

    public IBossBattle registerBossBattle(@Nonnull IBossEntity boss) {
        if(registry.containsKey(boss)){
            throw new IllegalStateException("boss is already registered in the battle registry");
        }

        BossBattle battle = new BossBattle(boss, new BossBattleRules(), new ArrayList<>());
        registry.put(boss, battle);
        return battle;
    }

    public void deregisterBossBattle(IBossEntity boss) {
        if(registry.containsKey(boss)){
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
        for(IBossBattle battle : registry.values()){
            if(battle.containsPlayer(player)){
                return Optional.of(battle);
            }
        }
        return Optional.empty();
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
        if(instance == null){
            instance = new BossBattleRegistry();
        }
        return instance;
    }
}
