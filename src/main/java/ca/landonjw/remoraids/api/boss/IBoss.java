package ca.landonjw.remoraids.api.boss;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * A IBoss represents the raid boss that will be used as a raid party host. In other words,
 * this is the raid pokemon that players will fight together in order to take it down.
 * <p>
 * A boss can be customized as much as a user desires, and external third party options
 * are free to use a raid boss's data for any necessary means they desire.
 *
 * @author NickImpact
 * @since 1.0.0
 */
public interface IBoss extends DataSerializable {

	/**
	 * Represents the Unique ID of this boss. Namely useful for queries for information on this particular
	 * boss and caches.
	 *
	 * @return The unique ID of this boss
	 */
	UUID getUniqueId();

	/**
	 * Attempts to locate an entity belonging to this Raid Boss. An entity will only be present
	 * for this boss after it has been spawned in, and can be lost after it is despawned. As such,
	 * this call returns Optionally to indicate that the entity will not always be available.
	 *
	 * @return The entity optionally wrapped mapping to this boss, empty otherwise
	 */
	Optional<IBossEntity> getEntity();

	/**
	 * Returns the pokemon that represents the raid boss. This boss is used to create the statue representing
	 * the raid boss, as well as the representative battle pokemon.
	 *
	 * @return The pokemon representing this raid boss
	 */
	Pokemon getPokemon();

	/**
	 * Specifies the current total of a stat. This is more of a convenience method, as the representative pokemon
	 * will also contain the incoming value.
	 *
	 * @param stat The stat type to reference
	 * @return The value of the pokemon's stat
	 */
	int getStat(StatsType stat);

	/**
	 * Applies the input value to the specified stat type. This is a direct setter, and makes no mathematical
	 * adjustments.
	 *
	 * @param stat  The stat type to apply the input value to
	 * @param value The value to use to represent the stat
	 */
	void setStat(StatsType stat, int value);

	/**
	 * Amplifies the calculated stat of the pokemon by the multiplier specified in the input.
	 * In other words, this takes the current value of the stat type of the raid boss, and multiplies
	 * it by the amplifier value.
	 *
	 * @param stat      The stat type to apply the input value to
	 * @param amplifier The value to multiply the stat by
	 */
	void amplifyStat(StatsType stat, double amplifier);

	/**
	 * Specifies the size of the raid boss. This is the scale at which the model of the raid boss
	 * will be amplified.
	 *
	 * @return The value representing the amplification to the raid boss's size
	 */
	float getSize();

	/**
	 * Applies a new scale to the raid boss's statue. Use care when setting a value, as too big a value
	 * can cause absolute chaos.
	 *
	 * @param size The size to use as the scale for the pokemon's model
	 */
	void setSize(float size);

	/**
	 * Represents a texture that is applied to the raid boss. A raid boss may or may not have a
	 * texture, and as such, this call can return an empty optional value.
	 *
	 * @return The texture of the pokemon, if it has a custom texture
	 */
	Optional<String> getTexture();

	/**
	 * Applies a custom texture to the raid boss. Assuming a texture exists by its name, this texture
	 * will be applied to the raid boss for the client. A client still requires the resource in order
	 * to see a custom texture.
	 *
	 * @param texture The texture to apply to the pokemon
	 */
	void setTexture(@NonNull String texture);

	/**
	 * Gets the settings to be used by the boss instance in battle. This contains information
	 * regarding the restraints imposed on players during battle and the rewards to be distributed
	 * upon boss death.
	 *
	 * @return the battle settings
	 */
	IBossBattleSettings getBattleSettings();

	/**
	 * Creates a new builder for a raid boss.
	 *
	 * @return A new builder allowing construction of a raid boss
	 */
	static IBossBuilder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossBuilder.class);
	}

	/**
	 * Represents the building method for creating a raid boss. Out of all the available options listed below, the
	 * only required field should be the species of the raid boss. Failure to specify this value will result in
	 * a generated exception, terminating the build.
	 */
	interface IBossBuilder extends IBuilder.Deserializable<IBoss, IBossBuilder> {

		/**
		 * Builds the raid boss from a {@link Pokemon}. This will essentially copy all
		 * characteristics of the pokemon supplied for purposes of the boss. The
		 * supplied pokemon will <strong>not</strong> be directly used or modified.
		 *
		 * @param pokemon The Pokemon to create raid boss from
		 * @return The builder after being modified by this call
		 */
		IBossBuilder pokemon(Pokemon pokemon);

		/**
		 * Builds the raid boss from a {@link PokemonSpec}. Essentially, for all build options
		 * available, this function will call each and every one of them.
		 *
		 * @param spec The PokemonSpec that'll be used to create the raid boss.
		 * @return The builder after being modified by this call
		 */
		IBossBuilder spec(PokemonSpec spec);

		/**
		 * Sets the species of the raid boss to the input species. If a spec is not defined, or one
		 * that is doesn't specify a species, this call will end up being required. If no species for
		 * the boss is ever specified, the build operations will fail exceptionally.
		 *
		 * @param species The species of the pokemon to set as the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder species(EnumSpecies species);

		/**
		 * Sets the level of the raid boss. This will often times be used for just display, but
		 * depending on reward definitions and lack of a specified moveset, this will be used for generating
		 * stats and the like.
		 *
		 * @param level The level of the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder level(int level);

		/**
		 * Sets the form of the raid boss. For instance, this can be used to create a raid boss that is a
		 * primal groudon, or a Sir Doofus III.
		 *
		 * @param form The form to have the raid boss model
		 * @return The builder after being modified by this call
		 */
		IBossBuilder form(IEnumForm form);

		/**
		 * Sets whether or not the raid boss will be shiny.
		 *
		 * @param shiny The shiny state of the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder shiny(boolean shiny);

		/**
		 * Sets the nature of the raid boss.
		 *
		 * @param nature The nature of the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder nature(EnumNature nature);

		/**
		 * Sets the ability of the raid boss. This call will allow for abilities the pokemon may not normally
		 * be able to come with.
		 *
		 * @param ability The ability to apply to the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder ability(String ability);

		/**
		 * Sets the gender of the raid boss. The gender should be a valid option. Invalid options
		 * will be ignored, and the gender will be set randomly as always.
		 *
		 * @param gender The gender to apply to the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder gender(Gender gender);

		/**
		 * Applies a stat modification to the raid boss for a specific stat. More often than not, you'll only
		 * want to apply this for health. While you can apply stat modifications elsewhere, it may make the opposing
		 * pokemon far too powerful to defeat. Use with caution!
		 *
		 * <p>
		 * You may also use this call to apply an amplifier. An amplifier essentially takes the input value
		 * for the stat, and treats it as a multiplier to the overall stat of the pokemon, unmodified. Stat modifications
		 * do not stack, so a change to a stat overwrites any previous application.
		 * </p>
		 *
		 * @param stat    The stat to apply the modification to
		 * @param input   The value to use when modifying the stat
		 * @param amplify Whether or not the input value should be a multiplier or static value.
		 * @return The builder after being modified by this call
		 */
		IBossBuilder stat(StatsType stat, int input, boolean amplify);

		/**
		 * Sets the overall size of the raid boss. This will be the scale of the model as it will appear to the
		 * client. Be generous on this value, as some pokemon with high values here can be quite massive!
		 * Use with care!
		 *
		 * @param size The size factor of the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder size(float size);

		/**
		 * Sets the held item of the raid boss.
		 *
		 * @param heldItem Item to be held by the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder heldItem(ItemStack heldItem);

		/**
		 * Allows for applying a custom texture to the raid pokemon.
		 *
		 * <p>
		 * If you aim to have no texture for this pokemon, use "". If null is placed here,
		 * the call will fail exceptionally. Blame Reforged for this design decision.
		 * </p>
		 *
		 * @param texture The texture to use for the raid boss.
		 * @return The builder after being modified by this call
		 */
		IBossBuilder texture(@NonNull String texture);

		/**
		 * Applies the given moveset to the raid boss. If the boss is created with a restricted move, the system
		 * will send a warn message to indicate the issue, and then subsequently ignore that particular move. Fails
		 * quietly if no moves are available or all were rejected.
		 *
		 * @param moveset The moveset of the raid boss
		 * @return The builder after being modified by this call
		 */
		IBossBuilder moveset(Moveset moveset);

		/**
		 * Sets the settings to be used by the boss for battles.
		 *
		 * @param battleSettings The settings to be used by the boss for battles
		 * @return The builder after being modified by this call
		 */
		IBossBuilder battleSettings(IBossBattleSettings battleSettings);

	}

}