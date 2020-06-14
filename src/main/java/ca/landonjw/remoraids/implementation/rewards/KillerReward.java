package ca.landonjw.remoraids.implementation.rewards;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A reward that is given to the player that kills a {@link IBossEntity}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class KillerReward extends DropRewardBase {

    /**
     * Constructor for the killer reward.
     *
     * @param contents the contents of the reward
     */
    public KillerReward(IRewardContent... contents){
        super(contents);
    }

    /** {@inheritDoc} **/
    @Override
    public List<EntityPlayerMP> getWinnersList(IBossBattle battle) {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        List<EntityPlayerMP> winners = new ArrayList<>();
        battle.getKiller().ifPresent((killer) -> winners.add(playerList.getPlayerByUUID(killer)));
        return winners;
    }

    /** {@inheritDoc} **/
    @Override
    public String getDescription() {
        Config config = RemoRaids.getMessageConfig();
        return config.get(MessageConfig.KILLER_REWARD_TITLE);
    }

}
