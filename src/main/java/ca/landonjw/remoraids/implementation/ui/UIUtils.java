package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of utilities to be used for user interfaces.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class UIUtils {

    /**
     * Gets information of a boss and returns nicely formated strings for lore.
     *
     * @param boss the boss to get information from
     * @return list of formatted strings to be used for lore, to give information about a boss
     */
    public static List<String> getPokemonLore(IBossEntity boss){
        List<String> lore = new ArrayList<>();

        Pokemon pokemon = boss.getBoss().getPokemon();

        lore.add(TextFormatting.GRAY + "Species: " + TextFormatting.YELLOW + pokemon.getSpecies().name);
        lore.add(TextFormatting.GRAY + "Size: " + TextFormatting.YELLOW + (boss.getBoss().getSize() * 100) + "%");
        if(boss.getBoss().getPokemon().getFormEnum() != EnumNoForm.NoForm) {
            lore.add(TextFormatting.GRAY + "Form: " + TextFormatting.YELLOW + (boss.getBoss().getPokemon().getFormEnum().getLocalizedName()));
        }
        lore.add("");

        String statLine1 = ""
                .concat(TextFormatting.RED + "HP: " + boss.getBoss().getStat(StatsType.HP))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GOLD + "Atk: " + boss.getBoss().getStat(StatsType.Attack))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GREEN + "Def: " + boss.getBoss().getStat(StatsType.Defence));
        lore.add(statLine1);

        String statLine2 = ""
                .concat(TextFormatting.BLUE + "SpA: " + boss.getBoss().getStat(StatsType.SpecialAttack))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GREEN + "SpD: " + boss.getBoss().getStat(StatsType.SpecialDefence))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.LIGHT_PURPLE + "Spe: " + boss.getBoss().getStat(StatsType.Speed));
        lore.add(statLine2);

        lore.add("");
        lore.add(TextFormatting.GRAY + "Moves:");

        Attack[] attacks = pokemon.getMoveset().attacks;
        String attackLine1 = ""
                .concat(TextFormatting.AQUA + ((attacks[0] != null) ? attacks[0].getMove().getAttackName() : "None"))
                .concat(" - " + TextFormatting.AQUA + ((attacks[1] != null) ? attacks[1].getMove().getAttackName() : "None"));
        lore.add(attackLine1);

        String attackLine2 = ""
                .concat(TextFormatting.AQUA + ((attacks[2] != null) ? attacks[2].getMove().getAttackName() : "None"))
                .concat(" - " + TextFormatting.AQUA + ((attacks[3] != null) ? attacks[3].getMove().getAttackName() : "None"));
        lore.add(attackLine2);

        return lore;
    }

}
