package ca.landonjw.remoraids.implementation.ui.pages.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class RestraintsUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public RestraintsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	@SuppressWarnings({
			"unchecked", "rawtypes" })
	@Override
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			Set<IBattleRestraint> restraints = bossEntity.getBoss().getBattleSettings().getBattleRestraints();
			List<Button> restraintButtons = new ArrayList<>();

			for (IBattleRestraint restraint : restraints) {
				List<String> lore = Lists.newArrayList();
				lore.add(config.get(MessageConfig.UI_COMMON_EDIT_ELEMENT));
				lore.add(config.get(MessageConfig.UI_COMMON_DELETE_ELEMENT));

				Button restraintButton = Button.builder().item(new ItemStack(Items.FILLED_MAP)).displayName(TextFormatting.AQUA + restraint.getId()).lore(lore).onClick((action) -> {
					if (action.getClickType() == ClickType.CLONE) {
						restraints.remove(restraint);
						open();
					} else {
						IBossUIRegistry registry = IBossUIRegistry.getInstance();
						if (registry.getRestraintEditor(restraint.getClass()).isPresent()) {
							IEditorUI editor = registry.getRestraintEditor(restraint.getClass()).get();
							editor.open(this, player, restraint);
						}
					}
				}).build();

				restraintButtons.add(restraintButton);
			}

			Button addRestraint = Button.builder().item(new ItemStack(PixelmonItems.pokemonEditor)).displayName(config.get(MessageConfig.UI_BATTLES_RESTRAINT_SETTINGS_ADD_RESTRAINT)).onClick(() -> {
				AddRestraintUI addRestraintUI = new AddRestraintUI(this, player, bossEntity);
				addRestraintUI.open();
			}).build();

			Button prevPage = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_COMMON_PREVIOUS_PAGE)).type(ButtonType.PreviousPage).build();

			Button nextPage = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_COMMON_NEXT_PAGE)).type(ButtonType.NextPage).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(1, 4, addRestraint).set(2, 1, prevPage).set(2, 7, nextPage).set(3, 4, back).build();

			Page page = Page.builder().template(template).dynamicContentArea(2, 2, 1, 5).dynamicContents(restraintButtons).title(config.get(MessageConfig.UI_BATTLES_RESTRAINT_SETTINGS_TITLE)).build();

			page.forceOpenPage(player);
		}
	}

}
