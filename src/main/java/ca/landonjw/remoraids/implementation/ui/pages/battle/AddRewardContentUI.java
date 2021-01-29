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
import ca.landonjw.remoraids.api.ui.ICreatorUI;
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

/**
 * A user interface to select a new reward content to create.
 * This will go through all reward content creators registered in the {@link IBossUIRegistry}
 * and add them.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class AddRewardContentUI extends BaseBossUI {

	/** reward to add new reward content to */
	private IReward reward;

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 * @param reward     reward to add new reward content to
	 */
	public AddRewardContentUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull IReward reward) {
		super(source, player, bossEntity);
		this.reward = reward;
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		Config config = RemoRaids.getMessageConfig();

		Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
			source.open();
		}).build();

		List<Button> creatorButtons = new ArrayList<>();
		List<IRewardContent> rewardContentsList = reward.getContents();
		for (ICreatorUI<IRewardContent> creator : IBossUIRegistry.getInstance().getRewardContentCreators()) {
			Button creatorButton = Button.builder().item(creator.getCreatorIcon()).displayName(creator.getCreatorTitle()).onClick(() -> creator.open(this, player, rewardContentsList)).build();

			creatorButtons.add(creatorButton);
		}

		Button prevPage = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_COMMON_PREVIOUS_PAGE)).type(ButtonType.PreviousPage).build();

		Button nextPage = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_COMMON_NEXT_PAGE)).type(ButtonType.NextPage).build();

		Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 1, prevPage).set(2, 7, nextPage).set(3, 4, back).build();

		Page page = Page.builder().template(template).dynamicContentArea(2, 2, 1, 5).dynamicContents(creatorButtons).title(config.get(MessageConfig.UI_BATTLES_ADD_REWARD_CONTENT_TITLE)).build();

		page.forceOpenPage(player);
	}

}
