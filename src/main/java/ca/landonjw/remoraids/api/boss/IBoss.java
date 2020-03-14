package ca.landonjw.remoraids.api.boss;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.IBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumStatueTextureType;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;

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

    static IBossBuilder builder() {
        return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossBuilder.class);
    }

    /**
     *
     */
    interface IBossBuilder extends IBuilder<IBoss, IBossBuilder> {

        IBossBuilder species(EnumSpecies species);

        IBossBuilder level(int level);

        IBossBuilder form(IEnumForm form);

        IBossBuilder nature(EnumNature nature);

        IBossBuilder ability(String ability);

        IBossBuilder stat(StatsType stat, int input, boolean amplify);

        IBossBuilder size(float size);

        IBossBuilder texture(EnumStatueTextureType texture);

        IBoss build();

    }

}