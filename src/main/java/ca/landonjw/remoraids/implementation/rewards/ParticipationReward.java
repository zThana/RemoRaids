package ca.landonjw.remoraids.implementation.rewards;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A reward that is given to players that participate in a boss battle.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ParticipationReward extends DropRewardBase {

    /** {@inheritDoc} */
    @Override
    public List<EntityPlayerMP> getWinnersList(IBossBattle battle) {
        List<EntityPlayerMP> winners = new ArrayList<>();
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (UUID uuid : battle.getTopDamageDealers()){
            winners.add(playerList.getPlayerByUUID(uuid));
        }
        return winners;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        Config config = RemoRaids.getMessageConfig();
        return config.get(MessageConfig.PARTICIPATION_REWARD_TITLE);
    }

}
