package ca.landonjw.remoraids.implementation.battles.controller;

import java.util.List;
import java.util.Objects;

import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.GlobalStatusController;
import com.pixelmonmod.pixelmon.battles.status.GlobalStatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;

import ca.landonjw.remoraids.api.boss.IBossEntity;

/**
 * Controls global status for a boss.
 * Allows for the prevention of any disabled global status from being set.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossStatusController extends GlobalStatusController {

	/** Boss to control global status for. */
	private IBossEntity boss;
	/** Rules for battle with the boss. */
	private List<StatusType> disabledStatus;

	/**
	 * Constructor for the status controller.
	 *
	 * @param boss boss to control global status for
	 * @param bc   battle controller this is being used in
	 */
	public BossStatusController(IBossEntity boss, BattleControllerBase bc, List<StatusType> disabledStatus) {
		super(bc);

		this.boss = Objects.requireNonNull(boss, "boss must not be null");
		this.disabledStatus = Objects.requireNonNull(disabledStatus, "status list must not be null");
	}

	/**
	 * Adds a global status to the battle.
	 *
	 * <p>
	 * This is invoked whenever a global status is being added to the battle.
	 * If the status being added is supposed to be disabled, this will block it from being added.
	 * </p>
	 *
	 * @param status global status to add
	 */
	@Override
	public void addGlobalStatus(GlobalStatusBase status) {
		StatusType[] arrDisabledStatus = new StatusType[disabledStatus.size()];
		disabledStatus.toArray(arrDisabledStatus);

		if (!status.type.isStatus(arrDisabledStatus)) {
			super.addGlobalStatus(status);
		}
	}

	/**
	 * Gets the boss global status is being controlled for.
	 *
	 * @return the boss global status is being controlled for
	 */
	public IBossEntity getBoss() {
		return boss;
	}
}