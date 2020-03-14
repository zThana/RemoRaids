package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.api.boss.IBoss;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumStatueTextureType;
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
    private EnumStatueTextureType texture;

    public Boss(@Nonnull Pokemon pokemon){
        this.pokemon = Objects.requireNonNull(pokemon, "pokemon must not be null");
    }

    private Boss(@NonNull BossBuilder builder) {
        this.pokemon = Pixelmon.pokemonFactory.create(builder.species);
        this.applyIfNull(builder.form, this.pokemon::setForm);
        this.applyIfNull(builder.level, this.pokemon::setLevel);
        this.applyIfNull(builder.nature, this.pokemon::setNature);
        this.pokemon.initialize();
        this.applyIfNull(builder.ability, this.pokemon::setAbility);
        for(Map.Entry<StatsType, Tuple<Integer, Boolean>> entry : builder.stats.entrySet()) {
            if(entry.getValue().getSecond()) {
                this.amplifyStat(entry.getKey(), entry.getValue().getFirst());
            } else {
                this.setStat(entry.getKey(), entry.getValue().getFirst());
            }
        }

        this.texture = builder.texture;
        this.size = Math.max(1, builder.size);
    }

    private <T> void applyIfNull(T input, Consumer<T> consumer) {
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
        if(value > Short.MAX_VALUE){
            throw new IllegalArgumentException("stat value will cause short overflow");
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
    public Optional<EnumStatueTextureType> getTexture() {
        return Optional.ofNullable(texture);
    }

    /** {@inheritDoc} */
    @Override
    public void setTexture(@Nullable EnumStatueTextureType texture) {
        this.texture = texture;
    }

    public static class BossBuilder implements IBossBuilder {

        private EnumSpecies species;
        private IEnumForm form;
        private Integer level;

        private EnumNature nature;
        private String ability;
        private EnumStatueTextureType texture;
        private float size;
        private Map<StatsType, Tuple<Integer, Boolean>> stats = Maps.newHashMap();

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
        public IBossBuilder texture(EnumStatueTextureType texture) {
            this.texture = texture;
            return this;
        }

        @Override
        public IBossBuilder from(IBoss input) {
            return this;
        }

        @Override
        public IBoss build() {
            return new Boss(this);
        }

    }

}
