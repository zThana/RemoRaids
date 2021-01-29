package ca.landonjw.remoraids.implementation.ui.creators;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.NoRebattleRestraint;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class NoRebattleRestraintCreator implements ICreatorUI<IBattleRestraint> {

	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IBattleRestraint> toAddTo) {
		NoRebattleRestraint restraint = new NoRebattleRestraint();
		toAddTo.add(restraint);
		source.getSource().get().open();
	}

	@Override
	public ItemStack getCreatorIcon() {
		return new ItemStack(PixelmonItemsValuables.nugget);
	}

	@Override
	public String getCreatorTitle() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.NO_REBATTLE_RESTRAINT_TITLE);
	}

}
