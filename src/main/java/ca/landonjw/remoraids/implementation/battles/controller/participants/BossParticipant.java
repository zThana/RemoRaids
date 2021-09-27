package ca.landonjw.remoraids.implementation.battles.controller.participants;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.ai.MoveChoice;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.ParticipantType;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleAIMode;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.battles.controller.BossWrapper;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;

public class BossParticipant extends BattleParticipant {

	private IBossBattle bossBattle;

	private IBossEntity bossEntity;
	private EntityPixelmon battleEntity;
	private BossWrapper bossWrapper;

	public BossParticipant(@Nonnull IBossBattle bossBattle, @Nonnull IBossEntity bossEntity) {
		super(1);

		this.bossBattle = bossBattle;
		this.bossEntity = Objects.requireNonNull(bossEntity, "boss entity must not be null");

		this.battleEntity = bossEntity.getBattleEntity().orElseGet(() -> {
			IBossSpawner spawner = bossEntity.getSpawner();
			return spawner.fix();
		});

		bossWrapper = new BossWrapper(this, battleEntity);
		this.allPokemon = new PixelmonWrapper[] {
				bossWrapper };
		this.controlledPokemon.add(bossWrapper);
	}

	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	public BossWrapper getBossWrapper() {
		return bossWrapper;
	}

	public IBossBattle getBossBattle() {
		return bossBattle;
	}

	/**
	 * Checks if there are more Pokemon to battle.
	 *
	 * @return always returns false
	 */
	@Override
	public boolean hasMorePokemonReserve() {
		return false;
	}

	/**
	 * Checks if the boss can gain experience.
	 *
	 * @return always returns false
	 */
	@Override
	public boolean canGainXP() {
		return false;
	}

	/**
	 * Starts a battle for the boss participant.
	 *
	 * @param bc the battle controller that is initiating the battle
	 */
	@Override
	public void startBattle(BattleControllerBase bc) {
		this.bc = bc;

		for (PixelmonWrapper wrapper : this.controlledPokemon) {
			wrapper.bc = bc;

			int maxHealth = wrapper.getMaxHealth();
			int startHealth = wrapper.getHealth();
			if (startHealth > maxHealth)
				wrapper.setHealth(maxHealth);

			wrapper.clearStatus();

			wrapper.getMoveset().forEach(attack -> {
				if (attack != null)
					attack.pp = attack.getMaxPP();
			});

			wrapper.entity.battleController = bc;
		}

		EnumBattleAIMode battleAIMode = EnumBattleAIMode.Tactical;
		this.setBattleAI(battleAIMode.createAI(this));
	}

	/**
	 * Ends the battle for the participant.
	 */
	@Override
	public void endBattle(EnumBattleEndCause enumBattleEndCause) {
		this.controlledPokemon.get(0).entity.onEndBattle();
	}

	/**
	 * Gets the name of the boss Pokemon.
	 * 
	 * @return the name of the boss Pokemon.
	 */
	@Override
	public TextComponentBase getName() {
		String key = "";
		if (!this.controlledPokemon.isEmpty())
			key = "pixelmon." + (this.controlledPokemon.get(0)).entity.getLocalizedName().toLowerCase() + ".name";

		return new TextComponentTranslation(key);
	}

	/**
	 * Gets the move the boss has selected.
	 *
	 * @param wrapper the pixelmon to select move from
	 * @return the selected move
	 */
	@Override
	public MoveChoice getMove(PixelmonWrapper wrapper) {
		if (this.bc == null) {
			return null;
		} else if (!wrapper.getMoveset().isEmpty()) {
			wrapper.getMoveset().healAllPP();
			return this.getBattleAI().getNextMove(wrapper);
		} else {
			this.bc.setFlee(wrapper.getPokemonUUID());
			return null;
		}
	}

	/**
	 * Switches Pokemon. Always returns null because a boss never has a Pokemon to switch to.
	 *
	 * @param wrapper         current sent out pixelmon
	 * @param nextPokemonUUID UUID of the pokemon to switch in
	 * @return always returns null
	 */
	@Override
	public PixelmonWrapper switchPokemon(PixelmonWrapper wrapper, UUID nextPokemonUUID) {
		return null;
	}

	/**
	 * Checks if the boss Pokemon is valid.
	 *
	 * @return true if the Pokemon is valid, false if the Pokemon is not valid
	 */
	@Override
	public boolean checkPokemon() {
		boolean allGood = true;
		Iterator<PixelmonWrapper> iter = this.controlledPokemon.iterator();

		while (iter.hasNext()) {
			PixelmonWrapper pw = (PixelmonWrapper) iter.next();
			if (pw.getMoveset().isEmpty()) {
				pw.entity.getPokemonData().getMoveset().clear();
				pw.entity.getPokemonData().getMoveset().addAll(pw.entity.getPokemonData().getBaseStats().loadMoveset(100));
				if (pw.getMoveset().isEmpty()) {
					if (PixelmonConfig.printErrors)
						Pixelmon.LOGGER.info("Couldn't load " + pw.entity.getLocalizedName() + "'s moves.");

					allGood = false;
				}
			}
		}

		return allGood;
	}

	/**
	 * Does nothing but is required by the parent abstract class.
	 *
	 * @param pixelmonWrapper pixelmon to update
	 */
	@Override
	public void updatePokemon(PixelmonWrapper pixelmonWrapper) {
		/* crickets */}

	/**
	 * Gets the entity of the boss Pokemon.
	 *
	 * @return the entity of the boss Pokemon.
	 */
	@Override
	public EntityLivingBase getEntity() {
		return battleEntity;
	}

	/**
	 * Does nothing but is required by the parent abstract class.
	 */
	@Override
	public void updateOtherPokemon() {
		/* crickets */}

	/**
	 * Gets the type of participant the boss is.
	 * Since WildPokemon is the most accurate type, we distinguish the boss as it.
	 *
	 * @return always returns the participant type WildPokemon
	 */
	@Override
	public ParticipantType getType() {
		return ParticipantType.WildPokemon;
	}

	/**
	 * Does nothing but is required by the parent abstract class
	 *
	 * @param index the index of the next Pokemon
	 */
	@Override
	public void getNextPokemon(int index) {
		/* crickets */}

	/**
	 * Gets the next Pokemon's UUID. Since the boss only has one Pokemon, returns null.
	 *
	 * @return always returns null
	 */
	@Override
	public UUID getNextPokemonUUID() {
		return null;
	}

	@Override
	public boolean canDynamax() {
		return false;
	}
}
