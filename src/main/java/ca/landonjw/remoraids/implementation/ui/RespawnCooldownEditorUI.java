package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
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
 * A user interface to modify the respawn cooldown of the boss entity.
 * Can be viewed by: Registry > Options > Edit > Respawning Settings > Set Respawn Cooldown
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RespawnCooldownEditorUI extends BaseBossUI {

    /** The ordinal of currently selected time unit. */
    private int currentTimeUnitOrdinal = -1;
    /** All time units that are valid and selectable in the user interface. */
    private final TimeUnit[] validTimeUnits = {TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS};

    /** Number of seconds in a day. */
    private final long SECONDS_IN_DAY = 86400;
    /** Number of seconds in an hour. */
    private final long SECONDS_IN_HOUR = 3600;
    /** Number of seconds in a minute. */
    private final long SECONDS_IN_MINUTE = 60;

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public RespawnCooldownEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
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

            Button.Builder decrementTimeUnitBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.LtradeHolderLeft));
            Button.Builder incrementTimeUnitBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.tradeHolderRight));
            Button.Builder timeUnitBuilder = Button.builder()
                    .item(new ItemStack(Items.CLOCK));

            Button.Builder decrementTimeValueBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.LtradeHolderLeft));
            Button.Builder incrementTimeValueBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.tradeHolderRight));
            Button.Builder timeValueBuilder = Button.builder()
                    .item(new ItemStack(PixelmonItems.hourglassSilver));

            if(this.bossEntity.getSpawner().getRespawnData().isPresent()){
                IBossSpawner spawner = this.bossEntity.getSpawner();
                IBossSpawner.IRespawnData respawns = spawner.getRespawnData().get();

                if(currentTimeUnitOrdinal == -1){
                    long cooldownSeconds = respawns.getTotalWaitPeriod(TimeUnit.SECONDS);

                    if(cooldownSeconds % SECONDS_IN_DAY == 0){
                        currentTimeUnitOrdinal = 3;
                    }
                    else if(cooldownSeconds % SECONDS_IN_HOUR == 0){
                        currentTimeUnitOrdinal = 2;
                    }
                    else if(cooldownSeconds % SECONDS_IN_MINUTE == 0){
                        currentTimeUnitOrdinal = 1;
                    }
                    else{
                        currentTimeUnitOrdinal = 0;
                    }
                }

                String strPrevUnit = validTimeUnits[(currentTimeUnitOrdinal - 1 + validTimeUnits.length) % validTimeUnits.length].name().toLowerCase();
                strPrevUnit = strPrevUnit.substring(0,1).toUpperCase().concat(strPrevUnit.substring(1));

                String strCurrentUnit = validTimeUnits[currentTimeUnitOrdinal].name().toLowerCase();
                strCurrentUnit = strCurrentUnit.substring(0,1).toUpperCase().concat(strCurrentUnit.substring(1));

                String strNextUnit = validTimeUnits[(currentTimeUnitOrdinal + 1) % validTimeUnits.length].name().toLowerCase();
                strNextUnit = strNextUnit.substring(0,1).toUpperCase().concat(strNextUnit.substring(1));


                decrementTimeUnitBuilder = decrementTimeUnitBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD +
                                "Change Time Unit (" + strPrevUnit + ")")
                        .onClick(() -> {
                            TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
                            currentTimeUnitOrdinal = (currentTimeUnitOrdinal - 1 + validTimeUnits.length) % validTimeUnits.length;
                            respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
                            open();
                        });
                incrementTimeUnitBuilder = incrementTimeUnitBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD +
                                "Change Time Unit (" + strNextUnit + ")")
                        .onClick(() -> {
                            TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
                            currentTimeUnitOrdinal = (currentTimeUnitOrdinal + 1) % validTimeUnits.length;
                            respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
                            open();
                        });

                timeUnitBuilder = timeUnitBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Time Unit: " + strCurrentUnit);

                decrementTimeValueBuilder = decrementTimeValueBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrement Cooldown Time")
                        .onClick(() -> {
                            TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
                            respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(currentUnit) - 1, currentUnit);
                            open();
                        });
                incrementTimeValueBuilder = incrementTimeValueBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increment Cooldown Time")
                        .onClick(() -> {
                            TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
                            respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(currentUnit) + 1, currentUnit);
                            open();
                        });
                timeValueBuilder = timeValueBuilder
                        .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Cooldown Time: " + respawns.getTotalWaitPeriod(validTimeUnits[currentTimeUnitOrdinal]));
            }
            else{
                decrementTimeUnitBuilder = decrementTimeUnitBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Change Time Unit")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));

                timeUnitBuilder = timeUnitBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Time Unit: Unavailable")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));

                incrementTimeUnitBuilder = incrementTimeUnitBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Change Time Unit")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));

                decrementTimeValueBuilder = decrementTimeValueBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Decrement Cooldown Time")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));

                timeValueBuilder = timeValueBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Cooldown: Unavailable")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));

                incrementTimeValueBuilder = incrementTimeValueBuilder
                        .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Increment Cooldown Time")
                        .lore(Arrays.asList(TextFormatting.WHITE + "This feature is not currently available."));
            }

            Button decrementTimeUnit = decrementTimeUnitBuilder.build();
            Button timeUnit = timeUnitBuilder.build();
            Button incrementTimeUnit = incrementTimeUnitBuilder.build();

            Button decrementTimeValue = decrementTimeValueBuilder.build();
            Button timeValue = timeValueBuilder.build();
            Button incrementTimeValue = incrementTimeValueBuilder.build();

            Template template = Template.builder(5)
                    .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                    .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                    .border(0,0, 5,9, getBlueFiller())
                    .set(0, 4, getBossButton())
                    .set(2, 1, decrementTimeUnit)
                    .set(2, 2, timeUnit)
                    .set(2, 3, incrementTimeUnit)
                    .set(2, 5, decrementTimeValue)
                    .set(2, 6, timeValue)
                    .set(2, 7, incrementTimeValue)
                    .set(3, 4, back)
                    .build();

            Page page = Page.builder()
                    .template(template)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Respawn Cooldown")
                    .build();

            page.forceOpenPage(player);
        }
    }

}
