package ca.landonjw.remoraids.implementation.boss;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.implementation.battles.BossBattleSettings;
import net.minecraft.util.Tuple;

/**
 * Implementation for {@link IBoss}
 *
 * @author landonjw
 * @since 1.0.0
 */
public class Boss implements IBoss {

	/** The UUID of the boss entity. */
	private UUID uniqueId;
	/** The Boss Pokemon. This should be preserved and not used in battle due to death potentially reverting forms and stats. */
	private Pokemon pokemon;
	/** The size of the boss. */
	private float size;
	/** The battle settings of the boss. */
	private IBossBattleSettings battleSettings;
	/** */
	private ItemStack heldItem;

	/**
	 * Default constructor for the boss.
	 *
	 * @param pokemon the pokemon to use as a boss
	 */
	public Boss(@Nonnull Pokemon pokemon) {
		this.uniqueId = UUID.randomUUID();
		this.pokemon = Objects.requireNonNull(pokemon, "Pokemon must not be null");
		this.size = 1;
		this.battleSettings = new BossBattleSettings();
	}

	/**
	 * The detailed constructor for the boss.
	 * This allowed for a nice builder to be utilized via {@link BossBuilder} to outline more details of a boss.
	 *
	 * @param builder the builder to use to construct boss
	 */
	private Boss(@NonNull BossBuilder builder) {
		this.uniqueId = builder.id;
		if (builder.spec != null && builder.species == null) {
			this.pokemon = Pixelmon.pokemonFactory.create(builder.spec);
		} else {
			this.pokemon = Pixelmon.pokemonFactory.create(builder.species);
			if (builder.spec != null) {
				builder.spec.apply(pokemon);
			}
		}
		applyIfNotNull(builder.form, this.pokemon::setForm);
		applyIfNotNull(builder.level, this.pokemon::setLevel);
		applyIfNotNull(builder.nature, this.pokemon::setNature);
		this.pokemon.initialize();
		applyIfNotNull(builder.ability, this.pokemon::setAbility);
		for (Map.Entry<StatsType, Tuple<Integer, Boolean>> entry : builder.stats.entrySet()) {
			if (entry.getValue().getSecond()) {
				this.amplifyStat(entry.getKey(), entry.getValue().getFirst());
			} else {
				this.setStat(entry.getKey(), entry.getValue().getFirst());
			}
		}
		applyIfNotNull(builder.gender, this.pokemon::setGender);
		applyIfNotNull(builder.shiny, this.pokemon::setShiny);
		applyIfNotNull(builder.heldItem, this.pokemon::setHeldItem);
		applyIfNotNull(builder.moveset, moveset -> {
			this.pokemon.getMoveset().attacks = moveset.attacks;
		});
		this.size = Math.max(1, builder.size);
		applyIfNotNull(builder.texture, this.pokemon::setCustomTexture);
		this.battleSettings = (builder.battleSettings != null) ? builder.battleSettings : new BossBattleSettings();
	}

	private static <T> void applyIfNotNull(T input, Consumer<T> consumer) {
		if (input != null) {
			consumer.accept(input);
		}
	}

	@Override
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Override
	public Optional<IBossEntity> getEntity() {
		return RemoRaids.getBossAPI().getBossEntityRegistry().getBossEntity(uniqueId);
	}

	/** {@inheritDoc} */
	@Override
	public Pokemon getPokemon() {
		return pokemon;
	}

	/** {@inheritDoc} */
	@Override
	public int getStat(@Nonnull StatsType stat) {
		return pokemon.getStat(stat);
	}

	/** {@inheritDoc} */
	@Override
	public void setStat(@Nonnull StatsType stat, int value) {
		if (value <= 0) {
			throw new IllegalArgumentException("stat value must be above 0");
		}

		switch (stat) {
			case HP:
				pokemon.getStats().hp = value;
				break;
			case Attack:
				pokemon.getStats().attack = value;
				break;
			case Defence:
				pokemon.getStats().defence = value;
				break;
			case SpecialAttack:
				pokemon.getStats().specialAttack = value;
				break;
			case SpecialDefence:
				pokemon.getStats().specialDefence = value;
				break;
			case Speed:
				pokemon.getStats().speed = value;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void amplifyStat(@Nonnull StatsType stat, double amplifier) {
		setStat(stat, (int) (pokemon.getStat(stat) * amplifier));
	}

	/** {@inheritDoc} */
	@Override
	public float getSize() {
		return size;
	}

	/** {@inheritDoc} */
	@Override
	public void setSize(float size) {
		if (size <= 0)
			throw new IllegalArgumentException("Size must be above 0!");

		this.size = size;
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getTexture() {
		return Optional.ofNullable(this.pokemon.getCustomTexture());
	}

	/** {@inheritDoc} */
	@Override
	public void setTexture(@Nullable String texture) {
		this.pokemon.setCustomTexture(texture);
	}

	@Override
	public IBossBattleSettings getBattleSettings() {
		return battleSettings;
	}

	/** {@inheritDoc} */
	@Override
	public JObject serialize() {
		JObject data = new JObject().add("uuid", this.uniqueId.toString()).add("species", this.pokemon.getSpecies().name()).add("level", this.pokemon.getLevel()).when(this.pokemon, pokemon -> this.getPokemon().getFormEnum() != EnumNoForm.NoForm, d -> d.add("form", this.pokemon.getForm())).add("nature", this.pokemon.getNature().name()).add("ability", this.pokemon.getAbilityName()).add("gender", this.pokemon.getGender().name()).add("shiny", this.pokemon.isShiny()).add("moves", this.pokemon.getMoveset().stream().map(a -> a.getActualMove().getAttackName()).collect(Collectors.toList()));

		JObject stats = new JObject();
		for (StatsType stat : StatsType.getStatValues()) {
			stats.add(stat.name(), this.pokemon.getStat(stat));
		}
		data.add("stats", stats);

		return data.add("size", this.size).add("texture", this.pokemon.getCustomTexture());
	}

	/**
	 * An implementation of an {@link IBossBuilder} for a {@link IBoss}.
	 *
	 * @author NickImpact
	 * @since 1.0.0
	 */
	public static class BossBuilder implements IBossBuilder {

		private UUID id = UUID.randomUUID();

		private PokemonSpec spec;
		/** The Pokemon species of the boss. May be null. */
		private EnumSpecies species;
		/** The form of the boss. May be null. */
		private IEnumForm form;
		/** The level of the boss. May be null. */
		private Integer level;
		/** If the boss is shiny. */
		private Boolean shiny;
		/** The nature of the boss. May be null. */
		private EnumNature nature;
		/** The ability of the boss. May be null. */
		private String ability;
		/** The texture of the boss. May be null. */
		private String texture;
		/** The gender of the boss. May be null. */
		private Gender gender;
		/** The moveset of the boss. May be null. */
		private Moveset moveset;
		/** The battle settings of the boss. May be null. */
		private IBossBattleSettings battleSettings;
		/** The size of the boss. */
		private float size;
		/** The held item of the boss. */
		private ItemStack heldItem;
		/** Map corresponding to each stat of the boss. Tuple represents a value, and if the value is intended as an amplification or flat value. */
		private Map<StatsType, Tuple<Integer, Boolean>> stats = Maps.newHashMap();

		@Override
		public IBossBuilder pokemon(Pokemon pokemon) {
			this.species = pokemon.getSpecies();
			this.level = pokemon.getLevel();
			this.form = pokemon.getFormEnum();
			this.shiny = pokemon.isShiny();
			this.nature = pokemon.getNature();
			this.ability = pokemon.getAbility().getName();
			this.gender = pokemon.getGender();
			this.moveset = pokemon.getMoveset().copy();
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder spec(PokemonSpec spec) {
			this.spec = spec;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder species(EnumSpecies species) {
			this.species = species;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder level(int level) {
			this.level = level;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder form(IEnumForm form) {
			this.form = form;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder shiny(boolean shiny) {
			this.shiny = shiny;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder nature(EnumNature nature) {
			this.nature = nature;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder ability(String ability) {
			this.ability = ability;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder gender(Gender gender) {
			this.gender = gender;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder stat(StatsType stat, int input, boolean amplify) {
			this.stats.put(stat, new Tuple<>(input, amplify));
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder size(float size) {
			this.size = size;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder heldItem(ItemStack heldItem) {
			this.heldItem = heldItem;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder texture(@NonNull String texture) {
			Preconditions.checkNotNull(texture, "Texture cannot be null!");
			this.texture = texture;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder moveset(Moveset moveset) {
			this.moveset = moveset;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder battleSettings(IBossBattleSettings battleSettings) {
			this.battleSettings = battleSettings;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder deserialize(JsonObject data) {
			this.id = UUID.fromString(data.get("uuid").getAsString());
			this.species = EnumSpecies.getFromNameAnyCase(data.get("species").getAsString());
			this.shiny = data.get("shiny").getAsBoolean();
			this.level = data.get("level").getAsInt();
			if (data.has("form")) {
				this.form = this.species.getFormEnum(data.get("form").getAsInt());
			}
			this.ability = data.get("ability").getAsString();
			this.nature = EnumNature.natureFromString(data.get("nature").getAsString());
			this.gender = Gender.getGender(data.get("gender").getAsString());

			this.size = data.get("size").getAsFloat();

			JsonObject stats = data.getAsJsonObject("stats");
			for (Map.Entry<String, JsonElement> entry : stats.entrySet()) {
				this.stat(StatsType.valueOf(entry.getKey()), entry.getValue().getAsInt(), false);
			}

			JsonArray moves = data.getAsJsonArray("moves");
			Moveset moveset = new Moveset();
			moves.iterator().forEachRemaining(entry -> {
				moveset.add(new Attack(AttackBase.getAttackBase(entry.getAsString()).get()));
			});
			this.moveset(moveset);

			return this;
		}

		/** {@inheritDoc} */
		@Override
		public IBossBuilder from(IBoss input) {
			this.id = input.getUniqueId();
			return this.species(input.getPokemon().getSpecies()).form(input.getPokemon().getFormEnum()).level(input.getPokemon().getLevel()).size(input.getSize()).stat(StatsType.HP, input.getStat(StatsType.HP), false).stat(StatsType.Attack, input.getStat(StatsType.Attack), false).stat(StatsType.Defence, input.getStat(StatsType.Defence), false).stat(StatsType.SpecialAttack, input.getStat(StatsType.SpecialAttack), false).stat(StatsType.SpecialDefence, input.getStat(StatsType.SpecialDefence), false).stat(StatsType.Speed, input.getStat(StatsType.Speed), false).ability(input.getPokemon().getAbilityName()).gender(input.getPokemon().getGender()).moveset(input.getPokemon().getMoveset()).nature(input.getPokemon().getNature()).shiny(input.getPokemon().isShiny()).texture(input.getTexture().orElse("")).battleSettings(input.getBattleSettings());
		}

		/** {@inheritDoc} */
		@Override
		public IBoss build() {
			return new Boss(this);
		}

	}

}
