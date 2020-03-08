package ca.landonjw.remoraids.internal.pokemon;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Stats;

public class PokemonUtils {

    /**
     * Generates a new Pokemon with similar characteristics to another Pokemon.
     *
     * Copies Attributes:
     * <ul>
     *     <li>Species</li>
     *     <li>Form</li>
     *     <li>Level</li>
     *     <li>Ability</li>
     *     <li>Nature</li>
     *     <li>Gender</li>
     *     <li>Shininess</li>
     *     <li>Growth</li>
     *     <li>Friendship</li>
     *     <li>Texture</li>
     *     <li>Stats</li>
     *     <li>Moveset</li>
     * </ul>
     *
     * @param pokemon Pokemon to generate a clone of.
     *
     * @return A Pokemon with very similar characteristics to another Pokemon.
     */
    public static Pokemon clonePokemon(Pokemon pokemon){
        Pokemon clonedPokemon = Pixelmon.pokemonFactory.create(pokemon.getSpecies());

        clonedPokemon.setForm(pokemon.getForm());
        clonedPokemon.setLevel(pokemon.getLevel());
        clonedPokemon.setNickname(pokemon.getNickname());

        clonedPokemon.setAbilitySlot(pokemon.getAbilitySlot());
        clonedPokemon.setNature(pokemon.getNature());
        clonedPokemon.setGender(pokemon.getGender());

        clonedPokemon.setShiny(pokemon.isShiny());
        clonedPokemon.setGrowth(pokemon.getGrowth());
        clonedPokemon.setFriendship(pokemon.getFriendship());
        clonedPokemon.setCustomTexture(pokemon.getCustomTexture());

        Stats pokemonStats = pokemon.getStats();
        Stats clonedPokemonStats = clonedPokemon.getStats();

        clonedPokemonStats.hp = pokemonStats.hp;
        clonedPokemon.setHealth(pokemon.getHealth());

        clonedPokemonStats.attack = pokemonStats.attack;
        clonedPokemonStats.defence = pokemonStats.defence;
        clonedPokemonStats.specialAttack = pokemonStats.specialAttack;
        clonedPokemonStats.specialDefence = pokemonStats.specialDefence;
        clonedPokemonStats.speed = pokemonStats.speed;

        clonedPokemon.getMoveset().clear();
        for(int i = 0; i < pokemon.getMoveset().size(); i++){
            clonedPokemon.getMoveset().add(pokemon.getMoveset().get(i));
        }

        clonedPokemon.getMoveset().healAllPP();

        return clonedPokemon;
    }

}
