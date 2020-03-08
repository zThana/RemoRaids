package ca.landonjw.remoraids.api.boss;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumStatueTextureType;

import java.util.Optional;

public interface IBoss {

    Pokemon getPokemon();

    int getStat(StatsType stat);

    void setStat(StatsType stat, int value);

    void amplifyStat(StatsType stat, double amplifier);

    float getSize();

    void setSize(float size);

    Optional<EnumStatueTextureType> getTexture();

    void setTexture(EnumStatueTextureType texture);

}