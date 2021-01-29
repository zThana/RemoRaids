package ca.landonjw.remoraids.implementation.ui.creators;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.options.ParticipationReward;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * The {@link ICreatorUI} used for creating a new {@link ParticipationReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class ParticipationRewardCreator implements ICreatorUI<IReward> {

	/** {@inheritDoc} */
	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IReward> toAddTo) {
		toAddTo.add(new ParticipationReward());
		source.getSource().get().open();
	}

	/** {@inheritDoc} */
	@Override
	public ItemStack getCreatorIcon() {
		return new ItemStack(PixelmonItemsValuables.nugget);
	}

	/** {@inheritDoc} */
	@Override
	public String getCreatorTitle() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.PARTICIPATION_REWARD_TITLE);
	}

}
