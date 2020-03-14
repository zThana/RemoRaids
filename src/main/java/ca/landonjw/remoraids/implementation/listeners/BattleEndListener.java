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

public class BattleEndListener {

    @SubscribeEvent
    public void onBattleEnd(BossDeathEvent event){
        Optional<IBossBattle> battle = event.getBossBattle();
        IBossEntity boss = event.getBossEntity();

        battle.ifPresent(b -> {
            List<EntityPlayerMP> contributors = b.getTopDamageDealers().stream()
                    .map(id -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id))
                    .collect(Collectors.toList());
            List<String> output = Lists.newArrayList();
            Function<String, String> format = str -> str.replaceAll("&", "\u00a7");
            output.add(format.apply("&8&m==============&r &c[Raid Results] &8&m=============="));
            output.add(format.apply("&7Through valiant effort, the raid pokemon,"));
            output.add(format.apply("&e" + boss.getBoss().getPokemon().getSpecies().getLocalizedName() + "&7, was defeated!"));
            output.add(format.apply(""));
            output.add(format.apply("&aTop " + Math.min(3, contributors.size()) + " Damage Dealers:"));
            for(int i = 0; i < Math.min(3, contributors.size()); i++) {
                output.add(format.apply("&e" + contributors.get(i).getName() + "&7: &b" + b.getDamageDealt(contributors.get(i).getUniqueID()).get()));
            }
            output.add(format.apply("&8&m=================================="));

            for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                for(String out : output) {
                    player.sendMessage(new TextComponentString(out));
                }
            }
        });
    }

}
