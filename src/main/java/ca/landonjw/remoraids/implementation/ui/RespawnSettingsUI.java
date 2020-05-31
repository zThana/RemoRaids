package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.TimedBossSpawner;
import ca.landonjw.remoraids.implementation.spawning.TimedSpawnListener;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * A user interface that displays spawning settings of a boss.
 * Can be viewed by: Registry > Options > Edit > Respawning Settings
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RespawnSettingsUI extends BaseBossUI {

    /**
     * Constructor for the user interface.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss being edited
     */
    public RespawnSettingsUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
        super(player, bossEntity);
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        if(bossNotInBattle()){
            Button setSpawnAmount = Button.builder()
                    .item(new ItemStack(Items.PAPER))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Respawn Limit")
                    .onClick(() -> {
                        RespawnLimitEditorUI spawnAmount = new RespawnLimitEditorUI(player, bossEntity);
                        spawnAmount.open();
                    })
                    .build();

            Button.Builder spawnLocationBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.rareCandy))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Set Respawn Location")
                    .lore(Arrays.asList(TextFormatting.WHITE + "You must increase the respawn limit for this functionality."));

            Button.Builder respawnCooldownBuilder = Button.builder()
                    .item(new ItemStack(Items.CLOCK))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Set Respawn Cooldown")
                    .lore(Arrays.asList(TextFormatting.WHITE + "You must increase the respawn limit for this functionality."));

            if(TimedSpawnListener.getInstance().getTimedBossSpawner(bossEntity).isPresent()){
                TimedBossSpawner spawner = TimedSpawnListener.getInstance().getTimedBossSpawner(bossEntity).get();
                if(spawner.getRespawnLimit() > spawner.getTimesRespawned()){
                    spawnLocationBuilder = spawnLocationBuilder
                            .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Set Respawn Location")
                            .lore(null)
                            .onClick(() -> {
                                BossSpawnLocation newLocation = new BossSpawnLocation(player);
                                spawner.setSpawnLocation(newLocation);
                                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "New respawn location set."));
                            });

                    respawnCooldownBuilder = respawnCooldownBuilder
                            .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Set Respawn Cooldown")
                            .lore(null)
                            .onClick(() -> {
                                RespawnCooldownEditorUI respawnCooldownEditorUI = new RespawnCooldownEditorUI(player, bossEntity);
                                respawnCooldownEditorUI.open();
                            });
                }
            }

            Button setSpawnLocation = spawnLocationBuilder.build();
            Button setRespawnCooldown = respawnCooldownBuilder.build();

            Button back = Button.builder()
                    .item(new ItemStack(Blocks.BARRIER))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                    .onClick(() -> {
                        EditorUI editor = new EditorUI(player, bossEntity);
                        editor.open();
                    })
                    .build();

            Template template = Template.builder(5)
                    .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                    .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                    .border(0,0, 5,9, getBlueFiller())
                    .set(0, 4, getBossButton())
                    .set(2, 2, setSpawnAmount)
                    .set(2, 4, setSpawnLocation)
                    .set(2, 6, setRespawnCooldown)
                    .set(3, 4, back)
                    .build();

            Page page = Page.builder()
                    .template(template)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Respawn Settings")
                    .build();

            page.forceOpenPage(player);
        }
    }

}
