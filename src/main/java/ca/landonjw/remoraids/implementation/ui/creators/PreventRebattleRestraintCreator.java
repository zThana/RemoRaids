package ca.landonjw.remoraids.implementation.ui.creators;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.PreventRebattleRestraint;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class PreventRebattleRestraintCreator implements ICreatorUI<IBattleRestraint> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IBattleRestraint> toAddTo) {
        PreventRebattleRestraint restraint = new PreventRebattleRestraint(source.getBossEntity());
        toAddTo.add(restraint);
        source.getSource().get().open();
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(PixelmonItemsValuables.nugget);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Prevent Rebattle Restraint";
    }

}
