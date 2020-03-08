package ca.landonjw.remoraids.implementation.battles;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.battles.IBossBattleRules;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Implementation of {@link IBossBattleRules).
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossBattleRules implements IBossBattleRules {

    /** The restraints to validate before battle. */
    private Set<IBattleRestraint> battleRestraints;
    /** The native Pixelmon battle rules. */
    private BattleRules battleRules;

    /**
     * Constructor for a boss's rules upon entrance and during battle.
     */
    public BossBattleRules(){
        this.battleRestraints = new HashSet<>();
    }

    /**
     * Constructor for a boss's rules upon entrance and during battle.
     *
     * @param battleRestraints the restraints to validate before battle
     * @param battleRules      the native Pixelmon battle rules
     */
    public BossBattleRules(@Nullable Set<IBattleRestraint> battleRestraints, @Nullable BattleRules battleRules){
        this.battleRestraints = (battleRestraints != null) ? battleRestraints : new HashSet<>();
        this.battleRules = battleRules;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<BattleRules> getBattleRules() {
        return Optional.ofNullable(battleRules);
    }

    /** {@inheritDoc} */
    @Override
    public Set<IBattleRestraint> getBattleRestraints() {
        return battleRestraints;
    }

    /** {@inheritDoc} */
    @Override
    public void addBattleRestraint(@Nonnull IBattleRestraint restraint) {
        battleRestraints.add(restraint);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate(EntityPlayerMP player) {
        boolean validationPassed = true;
        for(IBattleRestraint restraint : battleRestraints){
            if(!restraint.validatePlayer(player)){
                validationPassed = false;
            }
        }
        if(battleRules != null){
            List<Pokemon> playerTeam = Pixelmon.storageManager.getParty(player).getTeam();
            validationPassed = battleRules.validateTeam(playerTeam) == null;
        }
        return validationPassed;
    }

}
