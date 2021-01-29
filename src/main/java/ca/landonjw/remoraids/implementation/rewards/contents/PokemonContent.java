package ca.landonjw.remoraids.implementation.rewards.contents;

import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

/**
 * A Pokemon that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class PokemonContent implements IRewardContent {

	/** The description of the reward. */
	private String description;
	/** The Pokemon to be rewarded. */
	private PokemonSpec spec;
	/** The sprite to be used for item. Created in constructor from supplied spec. */
	private ItemStack pokemonSprite;

	/**
	 * Default constructor for the pokemon content.
	 *
	 * @param spec the pokemon to be rewarded
	 * @throws NullPointerException if spec is null
	 */
	public PokemonContent(@Nonnull PokemonSpec spec) {
		this.spec = spec;
		this.pokemonSprite = ItemPixelmonSprite.getPhoto(Pixelmon.pokemonFactory.create(spec));
	}

	/**
	 * Constructor that allows for user to supply a custom description.
	 *
	 * @param spec        the pokemon to be rewarded
	 * @param description custom description for the reward content
	 * @throws NullPointerException if spec is null
	 */
	public PokemonContent(@Nonnull PokemonSpec spec, @Nullable String description) {
		this(spec);
		this.description = description;
	}

	/** {@inheritDoc} **/
	@Override
	public void give(EntityPlayerMP player) {
		Pokemon pokemon = Pixelmon.pokemonFactory.create(spec);
		Pixelmon.storageManager.getParty(player).add(pokemon);

		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(Pokemon.class, () -> pokemon).build();
		player.sendMessage(new TextComponentString(service.interpret(config.get(MessageConfig.POKEMON_RECEIVED), context)));
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(PokemonSpec.class, () -> spec).build();
		if (description == null) {
			return service.interpret(config.get(MessageConfig.POKEMON_REWARD_CONTENT_TITLE), context);
		} else {
			return service.interpret(description, context);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack toItemStack() {
		ItemStack item = pokemonSprite.copy();
		item.setStackDisplayName(getDescription());

		return item;
	}

	@Override
	public JObject serialize() {
		String[] specArgs = spec.args;
		StringJoiner specBuilder = new StringJoiner(" ");
		for (String arg : specArgs) {
			specBuilder.add(arg);
		}

		JObject data = new JObject().add("spec", specBuilder.toString());
		if (description != null) {
			data.add("description", description);
		}
		return data;
	}
}
