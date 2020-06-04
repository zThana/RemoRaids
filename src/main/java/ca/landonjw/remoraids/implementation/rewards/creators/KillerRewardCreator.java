package ca.landonjw.remoraids.implementation.rewards.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

public class KillerRewardCreator implements ICreatorUI {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List toAddTo) {
        toAddTo.add(new KillerReward());
        source.getSource().get().open();
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.DIAMOND);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Killer Reward";
    }

}
