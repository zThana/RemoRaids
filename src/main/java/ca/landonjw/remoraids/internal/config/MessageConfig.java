package ca.landonjw.remoraids.internal.config;

import ca.landonjw.remoraids.RemoRaids;
import net.minecraftforge.common.config.Configuration;

import java.util.Objects;

public class MessageConfig {

    private Configuration config;

    //General Messages
    private String spawnAnnouncement = "&6&lA boss &a&l{boss-species} &6&lhas spawned!";
    private String engageMessage = "&a&lRelease a pokemon to engage the boss!";
    private String capacityRestraintDenied = "&4&lThere are too many players currently battling the boss!";
    private String cooldownRestraintDenied = "&4&lYou cannot battle the boss for another {minutes} minute(s) and {trimmed-seconds} second(s)!";
    private String preventRebattleRestraintDenied = "&4&lYou cannot rebattle this boss!";

    public MessageConfig(Configuration config){
        this.config = Objects.requireNonNull(config);
        readConfig();
    }

    private void readConfig(){
        try{
            config.load();
            init();
        }
        catch(Exception e){
            RemoRaids.logger.error("An error occurred during message configuration loading.");
        }
        finally{
            if(config.hasChanged()){
                config.save();
            }
        }
    }

    public void init() {
        config.addCustomCategoryComment("Messages", "Offers customization of any" +
                " message included in RemoRaids.");

        spawnAnnouncement = config.getString("Spawn-Announcement", "Messages", spawnAnnouncement,
                "Message sent to all players when a boss is spawned.");

        engageMessage = config.getString("Engage-Message", "Messages", engageMessage,
                "Message sent when a player is within engage range of a boss.");

        capacityRestraintDenied = config.getString("Battle-Denied-Capacity", "Messages", capacityRestraintDenied,
                "Message sent when a player is denied entrance to a battle because the capacity is full");

        cooldownRestraintDenied = config.getString("Battle-Denied-Cooldown", "Messages", cooldownRestraintDenied,
                "Message sent when a player is denied entrance to a battle because they are on cooldown."
                        + "Placeholders: {seconds}, {trimmed-seconds}, {minutes}, {trimmed-minutes}, {hours}");

        preventRebattleRestraintDenied = config.getString("Battle-Denied-No-Rebattle", "Messages", preventRebattleRestraintDenied,
                "Message sent when a player is denied entrance to a battle because they have already battled it.");
    }

    private String formatCodes(String message){
        return message.replaceAll("&", "\u00a7");
    }

    public String getEngageMessage() {
        return formatCodes(engageMessage);
    }

    public String getSpawnAnnouncement() {
        return formatCodes(spawnAnnouncement);
    }

    public String getCapacityRestraintDenied() {
        return formatCodes(capacityRestraintDenied);
    }

    public String getCooldownRestraintDenied() {
        return formatCodes(cooldownRestraintDenied);
    }

    public String getPreventRebattleRestraintDenied() {
        return formatCodes(preventRebattleRestraintDenied);
    }

}
