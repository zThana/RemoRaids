package ca.landonjw.remoraids.implementation.rewards;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A reward that is given to players that do the highest damage in a boss battle.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class HighestDamageReward extends DropRewardBase {

    /** The number of players to receive this reward. Setting this as x would be top x damage dealers. */
    private int numberReceivers;

    /**
     * Constructor for the highest damage reward.
     *
     * @param numberReceivers the number of players to receive the reward.
     * @param contents        the contents of the reward.
     */
    public HighestDamageReward(int numberReceivers, IRewardContent... contents){
        super(contents);
        this.numberReceivers = numberReceivers;
    }

    /** {@inheritDoc} **/
    @Override
    public List<EntityPlayerMP> getWinnersList(IBossBattle battle) {
        List<EntityPlayerMP> winners = new ArrayList<>();
        List<UUID> topDamageDealers = battle.getTopDamageDealers();

        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        for(int i = 0; i < numberReceivers; i++){
            EntityPlayerMP player = playerList.getPlayerByUUID(topDamageDealers.get(i));
            winners.add(player);
        }

        return winners;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Highest Damage Reward";
    }

}
