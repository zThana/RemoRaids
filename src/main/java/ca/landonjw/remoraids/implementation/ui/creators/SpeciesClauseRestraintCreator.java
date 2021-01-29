package ca.landonjw.remoraids.implementation.ui.creators;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.SpeciesClauseRestraint;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class SpeciesClauseRestraintCreator implements ICreatorUI<IBattleRestraint> {

	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IBattleRestraint> toAddTo) {
		SpeciesClauseRestraint restraint = new SpeciesClauseRestraint();
		toAddTo.add(restraint);
		source.getSource().get().open();
	}

	@Override
	public ItemStack getCreatorIcon() {
		return new ItemStack(PixelmonItemsValuables.cometShard);
	}

	@Override
	public String getCreatorTitle() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.SPECIES_CLAUSE_RESTRAINT_TITLE);
	}
}