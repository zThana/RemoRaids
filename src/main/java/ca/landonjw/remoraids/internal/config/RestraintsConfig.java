package ca.landonjw.remoraids.internal.config;

import ca.landonjw.remoraids.RemoRaids;
import com.pixelmonmod.pixelmon.battles.rules.clauses.AbilityClause;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.rules.clauses.ItemPreventClause;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.Aftermath;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.Imposter;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.IronBarbs;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.RoughSkin;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestraintsConfig {

    private Configuration config;

    private String[] disabledBossMoves = new String[]{
            "Aqua Ring",
            "Ingain",
            "Recover",
            "Rest",
            "Shore Up",
            "Soft-Boiled",
            "Synthesis"
    };

    private String[] disabledPlayerMoves = new String[]{
            "Endeavor",
            "Pain Split",
            "Leech Seed",
            "Perish Song",
            "Whirlpool",
            "Constrict",
            "Infestation",
            "Fire Spin",
            "Natures Madness",
            "Super Fang",
            "Sheer Cold",
            "Fissure",
            "Horn Drill",
            "Guillotine",
            "Power Swap",
            "Guard Swap",
            "Heal Pulse",
            "Present",
            "Floral Healing",
            "Spiky Shield",
            "Imprison",
            "Transform",
            "Destiny Bond",
            "Poison Gas",
            "Entrainment",
            "Glare",
            "Grass Whistle",
            "Hypnosis",
            "Lovely Kiss",
            "Poison Powder",
            "Psycho Shift",
            "Roar",
            "Whirlwind",
            "Sing",
            "Skill Swap",
            "Spore",
            "Stun Spore",
            "Thunder Wave",
            "Toxic",
            "Will-O-Wisp",
            "Yawn",
            "Magma Storm",
            "Bind",
            "Clamp",
            "Sand Tomb",
            "Wrap"
    };

    private String[] disabledStatus = new String[]{
            "Poison",
            "PoisonBadly",
            "Burn",
            "Paralysis",
            "Freeze",
            "Sleep",
            "GrassyTerrain",
            "Sandstorm",
            "Hail",
            "Cursed",
            "Imprison"
    };

    private boolean banAftermath = true;
    private boolean banImposter = true;
    private boolean banIronBarbs = true;
    private boolean banRockyHelmet = true;
    private boolean banRoughSkin = true;
    private boolean banStickyBarb = true;

    public RestraintsConfig(@Nonnull Configuration config){
        this.config = config;
        readConfig();
    }

    public void readConfig(){
        try{
            config.load();
            init();
        }
        catch(Exception e){
            RemoRaids.logger.error("An error occurred during restraint configuration loading.");
        }
        finally{
            if(config.hasChanged()){
                config.save();
            }
        }
    }

    private void init(){
        config.addCustomCategoryComment("Player-Restraints", "Sets the restraints " +
                "on players during battle with the boss.");

        disabledPlayerMoves = config.getStringList("Disabled-Player-Moves", "Player-Restraints",
                disabledPlayerMoves, "Moves that are disabled from being used by the player during battle.");

        disabledStatus = config.getStringList("Disabled-Status", "Player-Restraints",
                disabledStatus, "Status that is disabled from being inflicted on the boss during battle.");

        banAftermath = config.getBoolean("Ban-Aftermath", "Player-Restraints",
                banAftermath, "If aftermath should be banned from being used in boss battles.");

        banImposter = config.getBoolean("Ban-Imposter", "Player-Restraints",
                banImposter, "If imposter should be banned from being used in boss battles.");

        banIronBarbs = config.getBoolean("Ban-Iron-Barbs", "Player-Restraints",
                banIronBarbs, "If iron barbs should be banned from being used in boss battles.");

        banRockyHelmet = config.getBoolean("Ban-Rocky-Helmet", "Player-Restraints",
                banIronBarbs, "If rocky helmet should be banned from being used in boss battles.");

        banRoughSkin = config.getBoolean("Ban-Rough-Skin", "Player-Restraints",
                banRoughSkin, "If rough skin should be banned from being used in boss battles.");

        banStickyBarb = config.getBoolean("Ban-Sticky-Barb", "Player-Restraints",
                banStickyBarb, "If sticky barb should be banned from being used in boss battles.");

        config.addCustomCategoryComment("Player-Restraints", "Sets the restraints " +
                "on bosses during battle.");

        disabledBossMoves = config.getStringList("Disabled-Boss-Moves", "Boss-Restraints",
                disabledBossMoves, "Moves that are disabled from being used by a boss.");
    }

    public List<String> getDisabledBossMoves(){
        return Arrays.asList(disabledBossMoves);
    }

    public List<String> getDisabledPlayerMoves(){
        return Arrays.asList(disabledPlayerMoves);
    }

    public List<StatusType> getDisabledStatus(){
        List<StatusType> disabledStatusList = new ArrayList<>();
        for(String strStatus : disabledStatus){
            StatusType status = StatusType.getStatusEffect(strStatus);
            if(status != null){
                disabledStatusList.add(status);
            }
        }
        return disabledStatusList;
    }

    public List<BattleClause> getBannedClauses(){
        List<BattleClause> bannedClauses = new ArrayList<>();
        if(banAftermath){
            bannedClauses.add(new AbilityClause("Aftermath", Aftermath.class));
        }
        if(banImposter){
            bannedClauses.add(new AbilityClause("Imposter", Imposter.class));
        }
        if(banIronBarbs){
            bannedClauses.add(new AbilityClause("Iron Barbs", IronBarbs.class));
        }
        if(banRockyHelmet){
            bannedClauses.add(new ItemPreventClause("Rocky Helmet", EnumHeldItems.rockyHelmet));
        }
        if(banRoughSkin){
            bannedClauses.add(new AbilityClause("Rough Skin", RoughSkin.class));
        }
        if(banStickyBarb){
            bannedClauses.add(new ItemPreventClause("Sticky Barb", EnumHeldItems.stickyBarb));
        }
        return bannedClauses;
    }

}
