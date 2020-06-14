package ca.landonjw.remoraids.implementation.battles;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.rewards.IReward;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import net.minecraft.entity.player.EntityPlayerMP;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Implementation of {@link IBossBattleSettings ).
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossBattleSettings implements IBossBattleSettings {

    /** The restraints to validate before battle. */
    private Set<IBattleRestraint> battleRestraints;
    /** The native Pixelmon battle rules. */
    private BattleRules battleRules;
    /** The rewards to be distributed after battle. */
    private Set<IReward> rewards;

    /**
     * Constructor for a boss's rules upon entrance and during battle.
     */
    public BossBattleSettings(){
        this.battleRestraints = new HashSet<>();
        this.rewards = new HashSet<>();
    }

    /**
     * Constructor for a boss's rules upon entrance and during battle.
     *
     * @param battleRestraints the restraints to validate before battle
     * @param battleRules      the native Pixelmon battle rules
     */
    public BossBattleSettings(@Nullable Set<IBattleRestraint> battleRestraints, @Nullable BattleRules battleRules, @Nullable Set<IReward> rewards){
        this.battleRestraints = (battleRestraints != null) ? battleRestraints : new HashSet<>();
        this.battleRules = battleRules;
        this.rewards = (rewards != null) ? rewards : new HashSet<>();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<BattleRules> getBattleRules() {
        return Optional.ofNullable(battleRules);
    }

    /** {@inheritDoc} */
    @Override
    public void setBattleRules(@Nullable BattleRules battleRules) {
        this.battleRules = battleRules;
    }

    /** {@inheritDoc} */
    @Override
    public Set<IBattleRestraint> getBattleRestraints() {
        return battleRestraints;
    }

    @Override
    public boolean containsBattleRestraint(String id) {
        for(IBattleRestraint restraint : battleRestraints){
            if(restraint.getId().equalsIgnoreCase(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeBattleRestraint(String id) {
        IBattleRestraint remove = null;
        for(IBattleRestraint restraint : battleRestraints){
            if(restraint.getId().equalsIgnoreCase(id)){
                remove = restraint;
                break;
            }
        }
        if(remove != null){
            battleRestraints.remove(remove);
        }
    }

    @Override
    public Set<IReward> getRewards() {
        return rewards;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate(@NonNull EntityPlayerMP player) {
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

    /** {@inheritDoc} */
    @Override
    public List<String> getRejectionMessages(@NonNull EntityPlayerMP player) {
        List<String> rejectionMessages = new ArrayList<>();
        for(IBattleRestraint restraint : battleRestraints){
            if(!restraint.validatePlayer(player)){
                restraint.getRejectionMessage(player).ifPresent(rejectionMessages::add);
            }
        }
        return rejectionMessages;
    }

}
