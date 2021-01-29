package ca.landonjw.remoraids.implementation.ui.pages.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IBossUIRegistry;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.ButtonType;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * A user interface that displays all rewards for the boss entity.
 * Can be viewed by: Registry > Options > Edit > Battle Settings > Edit Rewards
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RewardsUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public RewardsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			Set<IReward> rewards = bossEntity.getBoss().getBattleSettings().getRewards();
			List<Button> rewardButtons = new ArrayList<>();

			for (IReward reward : rewards) {
				List<String> contentLore = new ArrayList<>();

				for (IRewardContent content : reward.getContents()) {
					contentLore.add("- " + content.getDescription());
				}
				contentLore.add(config.get(MessageConfig.UI_COMMON_EDIT_ELEMENT));
				contentLore.add(config.get(MessageConfig.UI_COMMON_DELETE_ELEMENT));

				Button rewardButton = Button.builder().item(new ItemStack(Blocks.CHEST)).displayName(TextFormatting.AQUA + reward.getDescription()).lore(contentLore).onClick((action) -> {
					if (action.getClickType() == ClickType.CLONE) {
						rewards.remove(reward);
						open();
					} else {
						IBossUIRegistry registry = IBossUIRegistry.getInstance();
						if (registry.getRewardEditor(reward.getClass()).isPresent()) {
							IEditorUI editor = registry.getRewardEditor(reward.getClass()).get();
							editor.open(this, player, reward);
						}
					}
				}).build();

				rewardButtons.add(rewardButton);
			}

			Button addReward = Button.builder().item(new ItemStack(PixelmonItems.pokemonEditor)).displayName(config.get(MessageConfig.UI_BATTLES_REWARD_SETTINGS_ADD_REWARD)).onClick(() -> {
				AddRewardUI addRewardUI = new AddRewardUI(this, player, bossEntity);
				addRewardUI.open();
			}).build();

			Button prevPage = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_COMMON_PREVIOUS_PAGE)).type(ButtonType.PreviousPage).build();

			Button nextPage = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_COMMON_NEXT_PAGE)).type(ButtonType.NextPage).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(1, 4, addReward).set(2, 1, prevPage).set(2, 7, nextPage).set(3, 4, back).build();

			Page page = Page.builder().template(template).dynamicContentArea(2, 2, 1, 5).dynamicContents(rewardButtons).title(config.get(MessageConfig.UI_BATTLES_REWARD_SETTINGS_TITLE)).build();

			page.forceOpenPage(player);
		}
	}

}
