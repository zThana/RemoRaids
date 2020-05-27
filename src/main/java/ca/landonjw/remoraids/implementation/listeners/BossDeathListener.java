package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Listens for the death of a boss Pokemon, and displays a battle summary.
 *
 * @author NickImpact
 * @since  1.0.0
 */
public class BossDeathListener {

    /**
     * Invoked when a {@link IBossEntity} dies.
     * This will create a summary that displays the killer and highest damage dealers, if available to all players on the server.
     *
     * @param event event caused by a boss death
     */
    @SubscribeEvent
    public void onBossDeath(BossDeathEvent event){
        //Checks if the boss died to a player, so that the summary isn't shown if forcibly despawned.
        if(event.getKiller().isPresent()){
            Optional<IBossBattle> battle = event.getBossBattle();
            IBossEntity boss = event.getBossEntity();

            battle.ifPresent(b -> {
                //Create an list of players in order of highest damage dealt in the battle
                List<EntityPlayerMP> contributors = b.getTopDamageDealers().stream()
                        .map(id -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id))
                        .collect(Collectors.toList());

                //Create output to send to players
                List<String> output = Lists.newArrayList();
                Function<String, String> format = str -> str.replaceAll("&", "\u00a7");
                output.add(format.apply("&8&m==============&r &c[Raid Results] &8&m=============="));
                output.add(format.apply("&7Through valiant effort, the raid pokemon,"));
                output.add(format.apply("&e" + boss.getBoss().getPokemon().getSpecies().getLocalizedName() + "&7, was defeated!"));
                output.add(format.apply(""));

                b.getKiller().ifPresent(killer -> {
                    String playerName = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(killer).getName();
                    output.add(format.apply("&cKiller: &e" + playerName));
                    output.add("");
                });

                output.add(format.apply("&aTop " + Math.min(3, contributors.size()) + " Damage Dealers:"));
                for(int i = 0; i < Math.min(3, contributors.size()); i++) {
                    output.add(format.apply("&e" + contributors.get(i).getName() + "&7: &b" + b.getDamageDealt(contributors.get(i).getUniqueID()).get()));
                }
                output.add(format.apply("&8&m=================================="));

                //Send summary to all players
                for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    for(String out : output) {
                        player.sendMessage(new TextComponentString(out));
                    }
                }
            });
        }
    }

}
