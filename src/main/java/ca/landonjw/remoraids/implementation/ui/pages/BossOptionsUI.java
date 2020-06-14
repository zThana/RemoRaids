package ca.landonjw.remoraids.implementation.ui.pages;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.implementation.battles.restraints.HaltedBattleRestraint;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.EditorUI;
import ca.landonjw.remoraids.implementation.ui.pages.RegistryUI;
import ca.landonjw.remoraids.internal.inventory.api.*;
import com.pixelmonmod.pixelmon.config.PixelmonBlocks;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.items.ItemShrineOrb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * A user interface that displays various options for the boss.
 *
 * Can be viewed by: Registry > Click On Any Boss
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossOptionsUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public BossOptionsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(source, player, bossEntity);
    }

    /** {@inheritDoc} */
    public void open() {
        Button teleport = Button.builder()
                .item(new ItemStack(Items.ENDER_PEARL))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Teleport")
                .onClick(() -> {
                    InventoryAPI.getInstance().closePlayerInventory(player);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleport to boss..."));

                    Entity bossStatue = bossEntity.getEntity();
                    World bossWorld = bossStatue.getEntityWorld();
                    if(player.dimension != bossWorld.provider.getDimension()){
                        PlayerList playerList = player.getServer().getPlayerList();
                        Teleporter teleporter = ((WorldServer) bossWorld).getDefaultTeleporter();
                        playerList.transferPlayerToDimension(player, bossWorld.provider.getDimension(), teleporter);
                    }
                    player.setPositionAndUpdate(bossStatue.posX, bossStatue.posY, bossStatue.posZ);
                })
                .build();

        Button edit = Button.builder()
                .item(new ItemStack(Items.WRITABLE_BOOK))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit")
                .onClick(() -> {
                    EditorUI editor = new EditorUI(this, player, bossEntity);
                    editor.open();
                })
                .build();

        Button.Builder preventBattlesBuilder = Button.builder()
                .item(new ItemStack(Blocks.TORCH))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Halt Battles")
                .lore(Arrays.asList(TextFormatting.WHITE + "This feature is currently unavailable."));

        if(RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()){
            IBossBattle battle = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get();
            if(bossEntity.getBoss().getBattleSettings().containsBattleRestraint(HaltedBattleRestraint.ID)){
                ItemStack fullOrb = new ItemStack(PixelmonItems.tresOrb);
                fullOrb.setItemDamage(ItemShrineOrb.full);

                preventBattlesBuilder = preventBattlesBuilder
                        .item(new ItemStack(Blocks.REDSTONE_TORCH))
                        .lore(Arrays.asList(TextFormatting.WHITE + "Toggled on"))
                        .onClick(() -> {
                            bossEntity.getBoss().getBattleSettings().removeBattleRestraint(HaltedBattleRestraint.ID);
                            open();
                        });
            }
            else{
                preventBattlesBuilder = preventBattlesBuilder
                        .lore(Arrays.asList(TextFormatting.WHITE + "Toggled off"))
                        .onClick(() -> {
                            bossEntity.getBoss().getBattleSettings().getBattleRestraints().add(new HaltedBattleRestraint(bossEntity.getBoss()));
                            for(EntityPlayerMP player : battle.getPlayersInBattle()){
                                player.sendMessage(new TextComponentString(TextFormatting.RED + "You have been kicked from battle due to ongoing boss editing."));
                            }
                            battle.endAllBattles();
                            open();
                        });
            }
        }
        Button preventBattles = preventBattlesBuilder.build();

        Button despawn = Button.builder()
                .item(new ItemStack(PixelmonBlocks.trashcanBlock))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Despawn")
                .onClick(() -> {
                    bossEntity.despawn();
                    InventoryAPI.getInstance().closePlayerInventory(player);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Boss Pokemon despawned."));
                })
                .build();

        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                .onClick(() -> {
                    RegistryUI registry = new RegistryUI(player);
                    registry.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 1, teleport)
                .set(2, 3, edit)
                .set(2, 5, preventBattles)
                .set(2, 7, despawn)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Options")
                .build();

        page.forceOpenPage(player);
    }

}
