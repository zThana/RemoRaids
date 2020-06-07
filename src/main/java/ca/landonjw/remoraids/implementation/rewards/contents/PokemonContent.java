package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A Pokemon that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class PokemonContent extends RewardContentBase {


    /** The Pokemon to be rewarded. */
    private PokemonSpec spec;
    /** The sprite to be used for item. Created in constructor from supplied spec. */
    private ItemStack pokemonSprite;

    /**
     * Default constructor for the pokemon content.
     *
     * @param  spec the pokemon to be rewarded
     * @throws NullPointerException if spec is null
     */
    public PokemonContent(@Nonnull PokemonSpec spec){
        super("Pokemon: " + spec.name);
        this.spec = spec;
        pokemonSprite = ItemPixelmonSprite.getPhoto(Pixelmon.pokemonFactory.create(spec));
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param spec              the pokemon to be rewarded
     * @param description custom description for the reward content
     * @throws NullPointerException if spec is null
     */
    public PokemonContent(@Nonnull PokemonSpec spec, @Nullable String description){
        super(description == null ? "Pokemon: " + spec.name : description);
        this.spec = Objects.requireNonNull(spec);
        pokemonSprite = ItemPixelmonSprite.getPhoto(Pixelmon.pokemonFactory.create(spec));
    }

    /** {@inheritDoc} **/
    @Override
    public void give(EntityPlayerMP player) {
        Pokemon pokemon = Pixelmon.pokemonFactory.create(spec);
        Pixelmon.storageManager.getParty(player).add(pokemon);
        player.sendMessage(new TextComponentString(TextFormatting.GREEN + "You have received a " + pokemon.getDisplayName() + "!"));
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack item = pokemonSprite.copy();
        item.setStackDisplayName(getDescription());

        return item;
    }

}
