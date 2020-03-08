package ca.landonjw.remoraids.internal.config;

import ca.landonjw.remoraids.RemoRaids;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeneralConfig {

    /** The configuration data. */
    private Configuration config;

    private float engageRange = 10;
    private int engageMessageType = 1;
    private boolean announcementTeleportable = true;
    private boolean botDetection = true;
    private int despawnInterval = 6000;

    public GeneralConfig(Configuration config){
        this.config = Objects.requireNonNull(config);
        readConfig();
    }


    public void readConfig(){
        try{
            config.load();
            init();
        }
        catch(Exception e){
            RemoRaids.logger.error("An error occurred during general configuration loading.");
        }
        finally{
            if(config.hasChanged()){
                config.save();
            }
        }
    }

    /**
     * Loads all values from the configuration into variables.
     */
    private void init(){
        config.addCustomCategoryComment("General", "General settings for the plugin.");

        engageRange = config.getFloat("Engage-Range", "General", engageRange,
                (float) 0.00000001, Float.MAX_VALUE, "Sets the area around a boss in blocks in which" +
                        " a player may automatically engage the boss in battle.");

        engageMessageType = config.getInt("Engage-Message-Type", "General", 1, 1, 4,
                "The type of message to be displayed when in engage range. 1 - Action Bar | 2 - Boss Bar | 3 - Overlay | 4 - Title ");

        announcementTeleportable = config.getBoolean("Announcement-Teleportable", "General",
                announcementTeleportable, "If players can teleport to the boss by clicking on the spawn announcement");

        announcementTeleportable = config.getBoolean("Bot-Detection", "General",
                botDetection, "If the plugin should kick players that appear to be botting during battle");

        despawnInterval = config.getInt("Despawn-Interval", "General", despawnInterval, 0, Integer.MAX_VALUE,
                "The interval in ticks at which a boss will attempt to despawn itself if not in battle");
    }

    public float getEngageRange() {
        return engageRange;
    }

    public int getDespawnInterval() {
        return despawnInterval;
    }

    public int getEngageMessageType() {
        return engageMessageType;
    }

    public boolean isAnnouncementTeleportable() {
        return announcementTeleportable;
    }

    public boolean isBotDetectionEnabled() {
        return botDetection;
    }

}
