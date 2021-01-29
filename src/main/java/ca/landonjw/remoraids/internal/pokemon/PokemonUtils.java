package ca.landonjw.remoraids.internal.pokemon;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import net.minecraft.nbt.NBTTagCompound;

public class PokemonUtils {

	/**
	 * Generates a new Pokemon with similar characteristics to another Pokemon.
	 *
	 * @param pokemon Pokemon to generate a clone of.
	 *
	 * @return A Pokemon with very similar characteristics to another Pokemon.
	 */
	public static Pokemon clonePokemon(Pokemon pokemon) {
		Pokemon clonedPokemon = Pixelmon.pokemonFactory.create(pokemon.writeToNBT(new NBTTagCompound()));
		clonedPokemon.setHealth(pokemon.getHealth());
		clonedPokemon.getMoveset().healAllPP();

		return clonedPokemon;
	}

}
