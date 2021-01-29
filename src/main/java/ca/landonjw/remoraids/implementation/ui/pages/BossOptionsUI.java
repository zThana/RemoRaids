package ca.landonjw.remoraids.implementation.ui.pages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonBlocks;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.implementation.battles.restraints.HaltedBossRestraint;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * A user interface that displays various options for the boss.
 *
 * Can be viewed by: Registry > Click On Any Boss
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossOptionsUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BossOptionsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	public void open() {
		Config config = RemoRaids.getMessageConfig();

		Button teleport = Button.builder().item(new ItemStack(Items.ENDER_PEARL)).displayName(config.get(MessageConfig.UI_BOSS_OPTIONS_TELEPORT_TITLE)).onClick(() -> {
			InventoryAPI.getInstance().closePlayerInventory(player);
			player.sendMessage(new TextComponentString(config.get(MessageConfig.UI_BOSS_OPTIONS_TELEPORT_MESSAGE)));

			World bossWorld = bossEntity.getWorld();
			if (player.dimension != bossWorld.provider.getDimension()) {
				PlayerList playerList = player.getServer().getPlayerList();
				Teleporter teleporter = ((WorldServer) bossWorld).getDefaultTeleporter();
				playerList.transferPlayerToDimension(player, bossWorld.provider.getDimension(), teleporter);
			}
			Vec3d pos = bossEntity.getPosition();
			player.setPositionAndUpdate(pos.x, pos.y, pos.z);
		}).build();

		Button edit = Button.builder().item(new ItemStack(Items.WRITABLE_BOOK)).displayName(config.get(MessageConfig.UI_BOSS_OPTIONS_EDIT_TITLE)).onClick(() -> {
			EditorUI editor = new EditorUI(this, player, bossEntity);
			editor.open();
		}).build();

		Button.Builder preventBattlesBuilder = Button.builder().item(new ItemStack(Blocks.TORCH)).displayName(config.get(MessageConfig.UI_BOSS_OPTIONS_HALT_BATTLES_TITLE)).lore(config.get(MessageConfig.UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_FEATURE_UNAVAILABLE));

		String haltedRestraintId = config.get(MessageConfig.HALTED_BOSS_RESTRAINT_TITLE);
		if (RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()) {
			IBossBattle battle = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get();
			if (bossEntity.getBoss().getBattleSettings().containsBattleRestraint(haltedRestraintId)) {
				preventBattlesBuilder = preventBattlesBuilder.item(new ItemStack(Blocks.REDSTONE_TORCH)).lore(config.get(MessageConfig.UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_ON)).onClick(() -> {
					bossEntity.getBoss().getBattleSettings().removeBattleRestraint(haltedRestraintId);
					open();
				});
			} else {
				preventBattlesBuilder = preventBattlesBuilder.lore(config.get(MessageConfig.UI_BOSS_OPTIONS_HALT_BATTLES_STAGES_OFF)).onClick(() -> {
					bossEntity.getBoss().getBattleSettings().getBattleRestraints().add(new HaltedBossRestraint());
					for (EntityPlayerMP player : battle.getPlayersInBattle()) {
						player.sendMessage(new TextComponentString(config.get(MessageConfig.UI_BOSS_OPTIONS_HALT_BATTLES_KICK)));
					}
					battle.endAllBattles();
					open();
				});
			}
		}
		Button preventBattles = preventBattlesBuilder.build();

		Button despawn = Button.builder().item(new ItemStack(PixelmonBlocks.trashcanBlock)).displayName(config.get(MessageConfig.UI_BOSS_OPTIONS_DESPAWN_TITLE)).onClick(() -> {
			bossEntity.despawn();
			InventoryAPI.getInstance().closePlayerInventory(player);
			player.sendMessage(new TextComponentString(config.get(MessageConfig.UI_BOSS_OPTIONS_DESPAWN_MESSAGE)));
		}).build();

		Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
			RegistryUI registry = new RegistryUI(player);
			registry.open();
		}).build();

		Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 1, teleport).set(2, 3, edit).set(2, 5, preventBattles).set(2, 7, despawn).set(3, 4, back).build();

		Page page = Page.builder().template(template).title(config.get(MessageConfig.UI_BOSS_OPTIONS_TITLE)).build();

		page.forceOpenPage(player);
	}

}
