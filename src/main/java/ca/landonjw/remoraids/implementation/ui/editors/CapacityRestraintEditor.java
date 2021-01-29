package ca.landonjw.remoraids.implementation.ui.editors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.CapacityRestraint;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class CapacityRestraintEditor implements IEditorUI<CapacityRestraint> {

	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull CapacityRestraint capacityRestraint) {
		new Editor(source, player, source.getBossEntity(), capacityRestraint).open();
	}

	class Editor extends BaseBossUI {

		private CapacityRestraint restraint;

		/**
		 * Default constructor.
		 *
		 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
		 * @param player     the player using the user interface
		 * @param bossEntity the boss entity being edited
		 */
		public Editor(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, CapacityRestraint restraint) {
			super(source, player, bossEntity);
			this.restraint = restraint;
		}

		@Override
		public void open() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(() -> {
				source.open();
			}).build();

			Button setCapacity = Button.builder().item(new ItemStack(Items.GLOWSTONE_DUST)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Set Capacity").onClick(() -> {
				openCapacityEditor();
			}).build();

			Button setDiminishing;
			if (restraint.isDiminishing()) {
				setDiminishing = Button.builder().item(new ItemStack(Blocks.REDSTONE_TORCH)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Disable Diminishing").onClick(() -> {
					restraint.setDiminishing(false);
					open();
				}).build();
			} else {
				setDiminishing = Button.builder().item(new ItemStack(Blocks.TORCH)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Enable Diminishing").onClick(() -> {
					restraint.setDiminishing(true);
					open();
				}).build();
			}

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 2, setCapacity).set(2, 6, setDiminishing).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Capacity Restraint").build();

			page.forceOpenPage(player);
		}

		public void openCapacityEditor() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(() -> {
				open();
			}).build();

			Button decrementCapacity = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrease Capacity").onClick(() -> {
				restraint.setCapacity(restraint.getCapacity() - 1);
				openCapacityEditor();
			}).build();

			Button incrementCapacity = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increase Capacity").onClick(() -> {
				restraint.setCapacity(restraint.getCapacity() + 1);
				openCapacityEditor();
			}).build();

			Button currentCapacity = Button.builder().item(new ItemStack(Items.GLOWSTONE_DUST)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Capacity: " + restraint.getCapacity()).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 3, decrementCapacity).set(2, 4, currentCapacity).set(2, 5, incrementCapacity).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Capacity (" + restraint.getCapacity() + ")").build();

			page.forceOpenPage(player);
		}

	}

}
