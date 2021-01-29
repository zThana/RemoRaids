package ca.landonjw.remoraids.implementation.ui.pages.general;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * A user interface to edit the size of a boss.
 * Can be viewed by: Registry > Options > Edit > General Settings > Edit Size
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossSizeEditorUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BossSizeEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	public void open() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(IBoss.class, () -> bossEntity.getBoss()).build();

		Button decrementSize = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_BOSS_SIZE_EDITOR_DECREASE)).onClick(() -> {
			if (bossEntity.getBoss().getSize() - 0.25 > 0) {
				bossEntity.getBoss().setSize(bossEntity.getBoss().getSize() - 0.25f);
			}
			open();
		}).build();

		Button incrementSize = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_BOSS_SIZE_EDITOR_INCREASE)).onClick(() -> {
			bossEntity.getBoss().setSize(bossEntity.getBoss().getSize() + 0.25f);
			open();
		}).build();

		Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
			source.open();
		}).build();

		Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(2, 3, decrementSize).set(2, 4, getBossButton()).set(2, 5, incrementSize).set(3, 4, back).build();

		Page page = Page.builder().template(template).title(service.interpret(config.get(MessageConfig.UI_BOSS_SIZE_EDITOR_TITLE), context)).build();

		page.forceOpenPage(player);
	}

}
