package ca.landonjw.remoraids.implementation.ui.editors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.rewards.options.TopDamageReward;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.battle.RewardContentsUI;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class TopDamageRewardEditor implements IEditorUI<TopDamageReward> {

	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull TopDamageReward topDamageReward) {
		new Editor(source, player, source.getBossEntity(), topDamageReward).open();
	}

	class Editor extends BaseBossUI {

		private TopDamageReward reward;

		/**
		 * Default constructor.
		 *
		 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
		 * @param player     the player using the user interface
		 * @param bossEntity the boss entity being edited
		 */
		public Editor(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull TopDamageReward reward) {
			super(source, player, bossEntity);
			this.reward = reward;
		}

		@Override
		public void open() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(() -> {
				source.open();
			}).build();

			Button editContents = Button.builder().item(new ItemStack(PixelmonItemsValuables.nugget)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Reward Contents").onClick(() -> {
				RewardContentsUI contentsUI = new RewardContentsUI(this, player, bossEntity, reward);
				contentsUI.open();
			}).build();

			Button setReceiverLimit = Button.builder().item(new ItemStack(Items.PAPER)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Receiver Limit").onClick(this::openReceiverLimitEditor).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 2, setReceiverLimit).set(2, 6, editContents).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Reward Settings").build();

			page.forceOpenPage(player);
		}

		private void openReceiverLimitEditor() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(this::open).build();

			Button receiverLimit = Button.builder().item(new ItemStack(Items.PAPER)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Receiver Limit: " + reward.getNumberReceivers()).build();

			Button decrementLimit = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrease Receiver Limit").onClick(() -> {
				if (reward.getNumberReceivers() > 1) {
					reward.setNumberReceivers(reward.getNumberReceivers() - 1);
					openReceiverLimitEditor();
				}
			}).build();

			Button incrementLimit = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increase Receiver Limit").onClick(() -> {
				reward.setNumberReceivers(reward.getNumberReceivers() + 1);
				openReceiverLimitEditor();
			}).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 3, decrementLimit).set(2, 4, receiverLimit).set(2, 5, incrementLimit).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Receiver Limit (" + reward.getNumberReceivers() + ")").build();

			page.forceOpenPage(player);
		}

	}

}
