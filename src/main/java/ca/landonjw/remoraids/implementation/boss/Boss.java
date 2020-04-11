package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.api.boss.IBoss;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import net.minecraft.util.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class Boss implements IBoss {

    private Pokemon pokemon;
    private float size;
    private String texture;

    public Boss(@Nonnull Pokemon pokemon){
        this.pokemon = Objects.requireNonNull(pokemon, "Pokemon must not be null");
        this.size = 1;
        this.texture = "";
    }

    private Boss(@NonNull BossBuilder builder) {
        this.pokemon = Pixelmon.pokemonFactory.create(builder.species);
        applyIfNotNull(builder.form, this.pokemon::setForm);
        applyIfNotNull(builder.level, this.pokemon::setLevel);
        applyIfNotNull(builder.nature, this.pokemon::setNature);
        this.pokemon.initialize();
        applyIfNotNull(builder.ability, this.pokemon::setAbility);
        for(Map.Entry<StatsType, Tuple<Integer, Boolean>> entry : builder.stats.entrySet()) {
            if(entry.getValue().getSecond()) {
                this.amplifyStat(entry.getKey(), entry.getValue().getFirst());
            } else {
                this.setStat(entry.getKey(), entry.getValue().getFirst());
            }
        }
        applyIfNotNull(builder.gender, this.pokemon::setGender);
        this.pokemon.setShiny(builder.shiny);
        applyIfNotNull(builder.moveset, moveset -> {
        	this.pokemon.getMoveset().attacks = moveset.attacks;
        });

        this.texture = builder.texture;
        this.size = Math.max(1, builder.size);
    }

    private static <T> void applyIfNotNull(T input, Consumer<T> consumer) {
        if(input != null) {
            consumer.accept(input);
        }
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
        if(value <= 0){
            throw new IllegalArgumentException("stat value must be above 0");
        }

        switch(stat){
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
        if(size <= 0){
            throw new IllegalArgumentException("size must be above 0");
        }
        this.size = size;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getTexture() {
        return Optional.ofNullable(texture);
    }

    /** {@inheritDoc} */
    @Override
    public void setTexture(@Nullable String texture) {
        this.texture = texture;
    }

    public static class BossBuilder implements IBossBuilder {

        private EnumSpecies species;
        private IEnumForm form;
        private Integer level;

        private boolean shiny;

        private EnumNature nature;
        private String ability;
        private String texture;
        private Gender gender;
        private Moveset moveset;

        private float size;
        private Map<StatsType, Tuple<Integer, Boolean>> stats = Maps.newHashMap();

        @Override
        public IBossBuilder spec(PokemonSpec spec) {
        	applyIfNotNull(spec.name, s -> this.species = EnumSpecies.getFromNameAnyCase(s));
			applyIfNotNull(spec.level, lvl -> this.level = lvl);
			applyIfNotNull(spec.form, f -> {
				Preconditions.checkNotNull(this.species, "The species must be configured for a form to be passed along");
				this.form = this.species.getFormEnum((int) f);
			});
			applyIfNotNull(spec.shiny, b -> this.shiny = b);
			applyIfNotNull(spec.nature, n -> this.nature = EnumNature.getNatureFromIndex((int) n));
			applyIfNotNull(spec.ability, a -> this.ability = a);
			applyIfNotNull(spec.gender, g -> this.gender = Gender.getGender(g));
            return this;
        }

        @Override
        public IBossBuilder species(EnumSpecies species) {
            this.species = species;
            return this;
        }

        @Override
        public IBossBuilder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public IBossBuilder form(IEnumForm form) {
            this.form = form;
            return this;
        }

	    @Override
	    public IBossBuilder shiny(boolean shiny) {
        	this.shiny = shiny;
		    return this;
	    }

	    @Override
        public IBossBuilder nature(EnumNature nature) {
            this.nature = nature;
            return this;
        }

        @Override
        public IBossBuilder ability(String ability) {
            this.ability = ability;
            return this;
        }

	    @Override
	    public IBossBuilder gender(Gender gender) {
        	this.gender = gender;
		    return this;
	    }

	    @Override
        public IBossBuilder stat(StatsType stat, int input, boolean amplify) {
            this.stats.put(stat, new Tuple<>(input, amplify));
            return this;
        }

        @Override
        public IBossBuilder size(float size) {
            this.size = size;
            return this;
        }

        @Override
        public IBossBuilder texture(@NonNull String texture) {
        	Preconditions.checkNotNull(texture, "Texture cannot be null!");
            this.texture = texture;
            return this;
        }

	    @Override
	    public IBossBuilder moveset(Moveset moveset) {
        	this.moveset = moveset;
		    return this;
	    }

	    @Override
        public IBossBuilder from(IBoss input) {
            return this.species(input.getPokemon().getSpecies())
		            .form(input.getPokemon().getFormEnum())
		            .level(input.getPokemon().getLevel())
		            .size(input.getSize())
		            .stat(StatsType.HP, input.getStat(StatsType.HP), false)
		            .stat(StatsType.Attack, input.getStat(StatsType.Attack), false)
		            .stat(StatsType.Defence, input.getStat(StatsType.Defence), false)
		            .stat(StatsType.SpecialAttack, input.getStat(StatsType.SpecialAttack), false)
		            .stat(StatsType.SpecialDefence, input.getStat(StatsType.SpecialDefence), false)
		            .stat(StatsType.Speed, input.getStat(StatsType.Speed), false)
		            .ability(input.getPokemon().getAbilityName())
		            .gender(input.getPokemon().getGender())
		            .moveset(input.getPokemon().getMoveset())
		            .nature(input.getPokemon().getNature())
		            .shiny(input.getPokemon().isShiny())
		            .texture(input.getTexture().orElse(""))
            ;
        }

        @Override
        public IBoss build() {
        	Preconditions.checkNotNull(this.species, "A raid boss must have a species specified to be created!");
            return new Boss(this);
        }

    }

}
