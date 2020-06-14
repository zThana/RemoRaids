package ca.landonjw.remoraids.internal.services.placeholders.provided;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RaidBossPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "boss";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		IBoss boss = context.get(IBoss.class).orElse(null);
		if(boss != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if(arguments.size() == 1){
				return parseSingleArg(boss, arguments.get(0));
			}

			return Optional.of(boss.getPokemon().getSpecies().getPokemonName());
		}
		return Optional.empty();
	}

	private Optional<String> parseSingleArg(@Nonnull IBoss boss, @Nonnull String arg) {
		arg = arg.toLowerCase();

		for(StatsType type : StatsType.getStatValues()){
			if(type.name().equalsIgnoreCase(arg)){
				return Optional.of("" + boss.getStat(type));
			}
		}
		switch(arg) {
			case "size":
				return Optional.of("" + boss.getSize());
			case "gender":
				return Optional.of("" + boss.getPokemon().getGender().name());
			case "level":
				return Optional.of("" + boss.getPokemon().getLevel());
			case "form":
				IEnumForm form = boss.getPokemon().getFormEnum();
				return (form != EnumNoForm.NoForm) ? Optional.of(form.getFormSuffix()) : Optional.empty();
		}
		return Optional.empty();
	}

}
