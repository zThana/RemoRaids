package ca.landonjw.remoraids.implementation.ui.creators;

import java.util.Collection;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.options.KillerReward;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The {@link ICreatorUI} used for creating a new {@link KillerReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class KillerRewardCreator implements ICreatorUI<IReward> {

	/** {@inheritDoc} */
	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IReward> toAddTo) {
		toAddTo.add(new KillerReward());
		source.getSource().get().open();
	}

	/** {@inheritDoc} */
	@Override
	public ItemStack getCreatorIcon() {
		return new ItemStack(Items.DIAMOND);
	}

	/** {@inheritDoc} */
	@Override
	public String getCreatorTitle() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.KILLER_REWARD_TITLE);
	}

}
