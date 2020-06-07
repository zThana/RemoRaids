package ca.landonjw.remoraids.implementation.ui.creators;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import ca.landonjw.remoraids.implementation.rewards.ParticipationReward;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The {@link ICreatorUI} used for creating a new {@link ParticipationReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ParticipationRewardCreator implements ICreatorUI<IReward> {

    /** {@inheritDoc} */
    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IReward> toAddTo) {
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
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Participation Reward";
    }

}
