package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.announcements.TeleportableSpawnAnnouncement;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * A user interface to edit the spawn limit of a boss.
 * Can be viewed by: Registry > Options > Edit > Spawning Settings > Edit Spawn Limit
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RespawnLimitEditorUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public RespawnLimitEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(source, player, bossEntity);
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        if(bossNotInBattle()){
            Button back = Button.builder()
                    .item(new ItemStack(Blocks.BARRIER))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                    .onClick(() -> {
                        source.open();
                    })
                    .build();

            Template.Builder templateBuilder = Template.builder(5)
                    .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                    .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                    .border(0,0, 5,9, getBlueFiller())
                    .set(0, 4, getBossButton())
                    .set(3, 4, back);

            int spawnLimit = 0;
            if(this.bossEntity.getSpawner().getRespawnData().isPresent()){
                IBossSpawner spawner = this.bossEntity.getSpawner();
                IBossSpawner.IRespawnData respawns = spawner.getRespawnData().get();

                spawnLimit = respawns.getTotalRespawns();

                Button spawnAmount = Button.builder()
                        .item(new ItemStack(Items.PAPER))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Respawn Limit: " + respawns.getTotalRespawns())
                        .lore(Arrays.asList(TextFormatting.WHITE + "Times Respawned: " + (respawns.getTotalRespawns() - respawns.getRemainingRespawns())))
                        .build();

                Button decrementSpawns = Button.builder()
                        .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrease Spawn Limit")
                        .onClick(() -> {
                            if(respawns.getTotalRespawns() > 0){
                                respawns.setTotalRespawns(respawns.getTotalRespawns() - 1);
                            }
                            open();
                        })
                        .build();

                Button incrementSpawns = Button.builder()
                        .item(new ItemStack(PixelmonItems.tradeHolderRight))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increase Spawn Limit")
                        .onClick(() -> {
                            respawns.setTotalRespawns(respawns.getTotalRespawns() + 1);
                            open();
                        })
                        .build();

                templateBuilder = templateBuilder
                        .set(2, 3, decrementSpawns)
                        .set(2, 4, spawnAmount)
                        .set(2, 5, incrementSpawns);
            }
            else{
                Button spawnAmount = Button.builder()
                        .item(new ItemStack(Items.PAPER))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Respawn Limit: 0")
                        .lore(Arrays.asList(TextFormatting.WHITE + "Times Respawned: 0"))
                        .build();

                Button decrementSpawns = Button.builder()
                        .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrease Spawn Limit")
                        .build();

                Button incrementSpawns = Button.builder()
                        .item(new ItemStack(PixelmonItems.tradeHolderRight))
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increase Spawn Limit")
                        .onClick(() -> {
                            IBossSpawner.IRespawnData data = this.bossEntity.getSpawner().createRespawnData();
                            data.setTotalRespawns(1);
                            data.setTotalWaitPeriod(5, TimeUnit.SECONDS);
                            open();
                        })
                        .build();

                templateBuilder = templateBuilder
                        .set(2, 3, decrementSpawns)
                        .set(2, 4, spawnAmount)
                        .set(2, 5, incrementSpawns);
            }

            Template template = templateBuilder.build();

            Page page = Page.builder()
                    .template(template)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Respawn Limit (" + spawnLimit + ")")
                    .build();

            page.forceOpenPage(player);
        }
    }

}
