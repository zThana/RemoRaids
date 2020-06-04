package ca.landonjw.remoraids.implementation.rewards.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.rewards.ParticipationReward;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

public class ParticipationRewardCreator implements ICreatorUI<IReward> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IReward> toAddTo) {
        toAddTo.add(new ParticipationReward());
        source.getSource().get().open();
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(PixelmonItemsValuables.nugget);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Participation Reward";
    }

}
