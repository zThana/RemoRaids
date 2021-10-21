package ca.landonjw.remoraids.implementation.rewards;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import ca.landonjw.remoraids.internal.config.GeneralConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.drops.CustomDropScreen;
import com.pixelmonmod.pixelmon.api.enums.BattleEndTaskType;
import com.pixelmonmod.pixelmon.api.enums.EnumPositionTriState;
import com.pixelmonmod.pixelmon.api.events.drops.CustomDropsEvent;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.text.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * A base for a {@link IReward} to be given in boss battles within the drop UI.
 *
 * @author landonjw
 * @since 1.0.0
 */
public abstract class DropRewardBase implements IReward {

	/** A list of all of the {@link IRewardContent} to be given from the reward. */
	protected List<IRewardContent> contents = Lists.newArrayList();

	/** All reward drops that are ongoing for rewards. */ // TODO: Maybe separate this out of class
	private static Map<UUID, RewardDropQuery> ongoingRewardQueries = Maps.newHashMap();

	/**
	 * Constructor for the reward base.
	 *
	 * @param contents the contents to be given from the reward. null contents are ignored.
	 */
	public DropRewardBase(IRewardContent... contents) {
		for (IRewardContent rewardContent : contents) {
			if (rewardContent != null) {
				this.contents.add(rewardContent);
			}
		}
		Pixelmon.EVENT_BUS.register(this);
	}

	/** {@inheritDoc} **/
	@Override
	public List<IRewardContent> getContents() {
		return contents;
	}

	/** {@inheritDoc} **/
	@Override
	public void addContents(IRewardContent... contents) {
		for (IRewardContent rewardContent : contents) {
			if (rewardContent != null) {
				this.contents.add(rewardContent);
			}
		}
	}

	/** {@inheritDoc} **/
	@Override
	public void removeContents(IRewardContent... contents) {
		this.contents.removeAll(Arrays.asList(contents));
	}

	/** {@inheritDoc} **/
	@Override
	public void clearContents() {
		contents.clear();
	}

	public void distributeReward(EntityPlayerMP player) {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(EntityPlayerMP.class, () -> player).add(IReward.class, () -> this).build();
		ITextComponent rewardText = new TextComponentString(service.interpret(config.get(MessageConfig.REWARD_RECEIVED), context));

		if(RemoRaids.getGeneralConfig().get(GeneralConfig.USE_CALLBACK)) {
			TextUtils.addCallback(rewardText, (sender) -> openReward(player), true);

			player.sendMessage(rewardText);
		} else {
			Pixelmon.storageManager.getParty(player).addTaskForBattleEnd(BattleEndTaskType.QUEUE_IF_BATTLE_OTHERWISE_RUN, (bc) -> openReward(player));
		}

	}

	private void openReward(EntityPlayerMP player) {
		RewardDropQuery query = new RewardDropQuery(player.getUniqueID(), this);
		ongoingRewardQueries.put(player.getUniqueID(), query);

		CustomDropScreen.builder().setItems(contents.stream().map(IRewardContent::toItemStack).collect(Collectors.toList())).setTitle(new TextComponentString(getDescription())).setButtonText(EnumPositionTriState.RIGHT, "Take All") // TODO: Make these configurable?
				.setButtonText(EnumPositionTriState.LEFT, "Drop All").sendTo(player);
	}

	/**
	 * Checks if playing is within the {@link #ongoingRewardQueries} and figures out what drop to give them when they click an item.
	 *
	 * @param event event called when an item is clicked in a custom drop screen
	 */
	@SubscribeEvent
	public void onDropItemClick(CustomDropsEvent.ClickDrop event) {
		UUID playerUUID = event.getPlayer().getUniqueID();
		if (ongoingRewardQueries.containsKey(playerUUID)) {
			RewardDropQuery query = ongoingRewardQueries.get(playerUUID);
			query.take(event.getPlayer(), event.getIndex());
			if (query.isEmpty()) {
				ongoingRewardQueries.remove(playerUUID);
			}
		}
	}

	/**
	 * Checks if player is a custom drop button and sends the appropriate reaction.
	 *
	 * @param event called when a button is clicked in a custom drop screen
	 */
	@SubscribeEvent
	public void onDropButtonClick(CustomDropsEvent.ClickButton event) {
		UUID playerUUID = event.getPlayer().getUniqueID();
		if (ongoingRewardQueries.containsKey(playerUUID)) {
			if (event.getPosition() == EnumPositionTriState.LEFT) {
				ongoingRewardQueries.remove(playerUUID);
			} else if (event.getPosition() == EnumPositionTriState.RIGHT) {
				RewardDropQuery query = ongoingRewardQueries.get(playerUUID);
				query.takeAll(event.getPlayer());
				ongoingRewardQueries.remove(playerUUID);
			}
		}
	}

}
