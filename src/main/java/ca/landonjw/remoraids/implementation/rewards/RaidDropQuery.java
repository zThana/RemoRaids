package ca.landonjw.remoraids.implementation.rewards;

import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQuery;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A custom drop query to be used for interacting with the Pixelmon drop UI.
 * This is used to distribute rewards to players after a boss battle.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RaidDropQuery extends DropItemQuery {

    /** Reward to be queried. */
    private DropRewardBase reward;
    /** Contents of the reward that have been taken by the player. */
    private List<Integer> contentsTaken = new ArrayList<>();

    /**
     * Constructor for the drop query.
     *
     * @param position   the position to drop items at. since we aren't dropping items, this is always [0,0,0]
     * @param playerUUID the uuid of player to open drop UI
     * @param reward     reward to be given
     */
    public RaidDropQuery(Vec3d position, UUID playerUUID, DropRewardBase reward) {
        super(position, playerUUID, reward.getDropItemList());
        this.reward = reward;
    }

    /**
     * Takes reward contents from the drop UI and gives it to the player.
     *
     * @param player player taking the contents
     * @param id     the id of the drop UI slot being taken from
     */
    public void takeReward(EntityPlayerMP player, int id){
        if(player.getUniqueID().equals(playerUUID)){
            if(!contentsTaken.contains(id - 1)){
                reward.getContents().get(id - 1).give(player);
                contentsTaken.add(id - 1);
            }

            if(contentsTaken.size() == reward.getContents().size()){
                DropItemQueryList.removeQuery(player);
            }
        }
    }

    /**
     * Takes all of the contents in the drop UI.
     *
     * @param player player taking all of the contents
     */
    public void takeAll(EntityPlayerMP player){
        if(player.getUniqueID().equals(playerUUID)){
            for(IRewardContent content : reward.getContents()){
                content.give(player);
            }
            DropItemQueryList.removeQuery(player);
        }
    }

}
