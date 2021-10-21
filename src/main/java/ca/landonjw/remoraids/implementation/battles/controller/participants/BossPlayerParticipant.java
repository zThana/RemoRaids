package ca.landonjw.remoraids.implementation.battles.controller.participants;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.ai.MoveChoice;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.tasks.HPUpdateTask;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Represents a player battling a boss.
 *
 * This will take over packet management in order to send packets to all player's in battle with the boss.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossPlayerParticipant extends PlayerParticipant {

	private IBossEntity bossEntity;

	/**
	 * Constructor for the player participant in a boss battle.
	 *
	 * This will remove any disabled moves with {@link #disableMoves()}.
	 *
	 * @param bossEntity       boss the player is battling
	 * @param player           player battling the boss
	 * @param startingPixelmon the pixelmon the player is starting with in the battle
	 * @throws NullPointerException if the boss is null
	 */
	public BossPlayerParticipant(@Nonnull IBossEntity bossEntity, @Nonnull EntityPlayerMP player, @Nonnull EntityPixelmon... startingPixelmon) {
		super(player, startingPixelmon);
		this.bossEntity = Objects.requireNonNull(bossEntity, "boss entity must not be null");

		disableMoves();
	}

	/**
	 * Disables moves that are in the boss's battle restraints from being used by the player.
	 *
	 * These moves will appear blanked out and will not be able to be clicked by the player.
	 */
	private void disableMoves() {
		List<String> disabledMoves = RemoRaids.getRestraintsConfig().get(RestraintsConfig.DISABLED_PLAYER_MOVES);
		for (PixelmonWrapper wrapper : this.controlledPokemon) {
			for (Attack attack : wrapper.getMoveset().attacks) {
				if (attack != null) {
					if (attack.isAttack(disabledMoves))
						attack.setDisabled(true, wrapper);
				}
			}
		}
	}

	/**
	 * Switches the current sent out Pokemon to a new Pokemon.
	 *
	 * This will remove any disabled moves with {@link #disableMoves()}.
	 *
	 * @param wrapper         wrapper that is currently in battle
	 * @param nextPokemonUUID the UUID of the Pokemon to send out
	 * @return the wrapper of the new Pokemon being sent out
	 */
	@Override
	public PixelmonWrapper switchPokemon(PixelmonWrapper wrapper, UUID nextPokemonUUID) {
		PixelmonWrapper nextPokemon = super.switchPokemon(wrapper, nextPokemonUUID);
		disableMoves();
		return nextPokemon;
	}

	/**
	 * Gets the player's selected move.
	 *
	 * This will remove any disabled moves with {@link #disableMoves()}.
	 *
	 * @param pokemon wrapper that is having move selected from
	 * @return the selected move
	 */
	@Override
	public MoveChoice getMove(PixelmonWrapper pokemon) {
		MoveChoice move = super.getMove(pokemon);
		disableMoves();
		return move;
	}

	/**
	 * Sends a packet displaying damage dealt to the Pokemon.
	 *
	 * This will only allow for packets to be sent if it is damage inflicted on the player's Pokemon.
	 *
	 * @param target target being damaged
	 * @param damage amount of damage inflicted
	 */
	@Override
	public void sendDamagePacket(PixelmonWrapper target, int damage) {
		if (this.controlledPokemon.contains(target)) {
			Pixelmon.network.sendTo(new HPUpdateTask(target, -damage), this.player);
		}
	}

	/**
	 * Sends a packet displaying health restored to a Pokemon.
	 *
	 * This will only allow for packets to be sent if it health restored on the player's Pokemon.
	 *
	 * @param target target being healed
	 * @param amount amount of health restored
	 */
	@Override
	public void sendHealPacket(PixelmonWrapper target, int amount) {
		if (this.controlledPokemon.contains(target)) {
			Pixelmon.network.sendTo(new HPUpdateTask(target, amount), this.player);
		}
	}

	/**
	 * Ends the current battle.
	 */
	@Override
	public void endBattle(EnumBattleEndCause cause) {
		IBossBattleRegistry battleRegistry = RemoRaids.getBossAPI().getBossBattleRegistry();
		Optional<IBossBattle> maybeBattle = battleRegistry.getBossBattle(player);
		maybeBattle.ifPresent(battle -> battle.endBattle(player));
		super.endBattle(cause);
	}

	/**
	 * Method for overwriting dynamax
	 *
	 * @return if the player can dynamax
	 */
	@Override
	public boolean canDynamax() {
//	I think we dont need this?
//		if (this.bc != null && this.bc.isRaid()) {
//			BattleParticipant bp = this.getOpponents().get(0);
//			if (bp instanceof RaidPixelmonParticipant) {
//				RaidPixelmonParticipant rpp = (RaidPixelmonParticipant)bp;
//				return rpp.canDynamax(this);
//			}
//		}
		if(!bossEntity.allowDynamax()) {
			return false;
		}

		return this.getStorage().getMegaItem().canDynamax();
	}

	/**
	 * Gets the boss that the player is battling.
	 *
	 * @return the boss that the player is battling
	 */
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

}