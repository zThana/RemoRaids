package ca.landonjw.remoraids.implementation.boss.engage;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.internal.tasks.Task;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class BossEngager implements IBossEngager {

	private IMessageChannel messageChannel;
	private IBossEntity bossEntity;
	private float engageRange;
	private String message;

	public BossEngager(@Nonnull IBossEntity bossEntity, @Nonnull IMessageChannel messageChannel, float engageRange, @Nonnull String message) {
		this.messageChannel = messageChannel;
		this.bossEntity = bossEntity;
		this.engageRange = engageRange;
		this.message = message;
		startMessageTask();
	}

	@Override
	public float getRange() {
		return engageRange;
	}

	@Override
	public boolean isPlayerInRange(EntityPlayerMP player) {
		if (bossEntity.getEntity().isPresent()) {
			EntityStatue bossStatue = bossEntity.getEntity().get();

			boolean inRange = false;

			if (!bossStatue.isDead) {
				if (player.world.equals(bossStatue.world)) {
					double xDiff = bossStatue.getEntityBoundingBox().maxX - bossStatue.getEntityBoundingBox().minX;
					double zDiff = bossStatue.getEntityBoundingBox().maxZ - bossStatue.getEntityBoundingBox().minZ;
					double largerDiff = (xDiff > zDiff) ? xDiff : zDiff;
					inRange = player.getDistance(bossStatue) <= engageRange + largerDiff;
				}
			}
			return inRange;
		}
		return false;
	}

	@Override
	public void sendEngageMessage() {
		if (bossEntity.getEntity().isPresent()) {
			IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
			EntityStatue bossStatue = bossEntity.getEntity().get();

			for (EntityPlayer player : bossStatue.world.playerEntities) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				if (isPlayerInRange(playerMP) && BattleRegistry.getBattle(playerMP) == null) {
					IParsingContext context = IParsingContext.builder().add(IBossEntity.class, () -> bossEntity).add(EntityPlayerMP.class, () -> playerMP).build();
					messageChannel.sendMessage(playerMP, service.interpret(message, context));
				}
			}
		}
	}

	private void startMessageTask() {
		Task.builder().execute((task) -> {
			if (getBossEntity().getEntity().isPresent()) {
				sendEngageMessage();
			} else {
				task.setExpired();
			}
		}).interval(60).build();
	}

	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessageChannel(IMessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

}
