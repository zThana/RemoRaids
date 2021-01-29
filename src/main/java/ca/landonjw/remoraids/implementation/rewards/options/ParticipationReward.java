package ca.landonjw.remoraids.implementation.rewards.options;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.data.SerializationFactory;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.implementation.rewards.DropRewardBase;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * A reward that is given to players that participate in a boss battle.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class ParticipationReward extends DropRewardBase {

	public ParticipationReward(IRewardContent... contents) {
		super(contents);
	}

	/** {@inheritDoc} */
	@Override
	public List<EntityPlayerMP> getWinnersList(IBossBattle battle) {
		List<EntityPlayerMP> winners = new ArrayList<>();
		PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

		for (UUID uuid : battle.getTopDamageDealers()) {
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

	@Override
	public int getPriority() {
		Config config = RemoRaids.getGeneralConfig();
		return config.get(GeneralConfig.PARTICIPATION_REWARD_PRIORITY);
	}

	@Override
	public JObject serialize() {
		JObject data = new JObject();

		JsonArray rewardContents = new JsonArray();
		SerializationFactory<IRewardContent> contentsFactory = RemoRaids.getDeserializerFactories().getRewardContentFactory();
		for (IRewardContent content : contents) {
			rewardContents.add(contentsFactory.serialize(content).get());
		}
		data.add("contents", rewardContents);
		return data;
	}

}
