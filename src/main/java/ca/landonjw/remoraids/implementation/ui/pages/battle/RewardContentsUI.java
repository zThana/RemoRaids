package ca.landonjw.remoraids.implementation.ui.pages.battle;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * A user interface to view all reward contents contained in a given {@link IReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RewardContentsUI extends BaseBossUI {

	/** Reward to view all reward contents of */
	private IReward reward;

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 * @param reward     reward to view all reward contents of
	 */
	public RewardContentsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull IReward reward) {
		super(source, player, bossEntity);
		this.reward = reward;
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

			List<Button> rewardContentButtons = new ArrayList<>();

			for (IRewardContent content : reward.getContents()) {
				Button rewardContentButton = Button.builder().item(content.toItemStack()).displayName(TextFormatting.AQUA + content.getDescription()).onClick(() -> {
					IBossUIRegistry registry = IBossUIRegistry.getInstance();
					if (registry.getRewardContentEditor(content.getClass()).isPresent()) {
						IEditorUI editor = registry.getRewardContentEditor(content.getClass()).get();
						editor.open(this, player, content);
					}
				}).build();

				rewardContentButtons.add(rewardContentButton);
			}

			Button addRewardContent = Button.builder().item(new ItemStack(PixelmonItems.pokemonEditor)).displayName(config.get(MessageConfig.UI_BATTLES_REWARD_CONTENT_SETTINGS_ADD_REWARD_CONTENT)).onClick(() -> {
				AddRewardContentUI addContentUI = new AddRewardContentUI(this, player, bossEntity, reward);
				addContentUI.open();
			}).build();

			Button prevPage = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_COMMON_PREVIOUS_PAGE)).type(ButtonType.PreviousPage).build();

			Button nextPage = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_COMMON_NEXT_PAGE)).type(ButtonType.NextPage).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(1, 4, addRewardContent).set(2, 1, prevPage).set(2, 7, nextPage).set(3, 4, back).build();

			Page page = Page.builder().template(template).dynamicContentArea(2, 2, 1, 5).dynamicContents(rewardContentButtons).title(config.get(MessageConfig.UI_BATTLES_REWARD_CONTENT_SETTINGS_TITLE)).build();

			page.forceOpenPage(player);
		}
	}

}
