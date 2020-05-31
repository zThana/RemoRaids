package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.BossAPI;
import com.pixelmonmod.pixelmon.api.events.StatueEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for interactions with a {@link IBossEntity}'s statue entity.
 * It will prevent any misuse of boss statues with a chisel.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class StatueInteractListener {

    /**
     * Invoked when a statue is destroyed.
     * This will prevent a boss statue from being destroyed and send a message to the player that the action isn't allowed.
     *
     * @param event event caused by statue destruction
     */
    @SubscribeEvent
    public void onStatueDestroy(StatueEvent.DestroyStatue event){
        for(IBossEntity entity : BossAPI.getInstance().getBossEntityRegistry().getAllBossEntities()){
            if(entity.getEntity().getUniqueID().equals(event.statue.getUniqueID())){
                event.setCanceled(true);
                event.player.sendMessage(new TextComponentString(TextFormatting.RED + "You may not perform this action on a raid boss!"));
            }
        }
    }

    /**
     * Invoked when a statue is modified.
     * This will prevent a boss statue from being modified and send a message to the player that the action isn't allowed.
     *
     * @param event event caused by statue modification
     */
    @SubscribeEvent
    public void onStatueModify(StatueEvent.ModifyStatue event){
        for(IBossEntity entity : BossAPI.getInstance().getBossEntityRegistry().getAllBossEntities()){
            if(entity.getEntity().getUniqueID().equals(event.getStatue().getUniqueID())){
                event.setCanceled(true);
                event.player.sendMessage(new TextComponentString(TextFormatting.RED + "You may not perform this action on a raid boss!"));
            }
        }
    }

}