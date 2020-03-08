package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.api.boss.IBoss;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumStatueTextureType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class Boss implements IBoss {

    private Pokemon pokemon;
    private float size;
    private EnumStatueTextureType texture;

    public Boss(@Nonnull Pokemon pokemon){
        this.pokemon = Objects.requireNonNull(pokemon, "pokemon must not be null");
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

}
