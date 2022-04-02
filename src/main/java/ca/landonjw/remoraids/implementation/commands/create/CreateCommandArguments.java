package ca.landonjw.remoraids.implementation.commands.create;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.implementation.commands.create.arguments.AllowDynamaxArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.HeldItemArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.ShowOverlayArgument;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.CapacityRestraintArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.CooldownRestraintArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.FullStatsArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.LocationArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.MovesArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.NoRebattleRestraintArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.RespawnAmountArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.RespawnTimeArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.RewardArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.RotationArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.SingleStatArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.SizeArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.SpeciesClauseRestraintArgument;
import ca.landonjw.remoraids.implementation.commands.create.arguments.WorldArgument;

public enum CreateCommandArguments {
	LOCATION(new LocationArgument()),
	WORLD(new WorldArgument()),
	ROTATION(new RotationArgument()),
	RESPAWN_AMOUNT(new RespawnAmountArgument()),
	RESPAWN_TIME(new RespawnTimeArgument()),
	// PERSISTENT(new PersistentArgument()), //TODO: This is implemented, but disabled for the 1.1.0 release.
	SIZE(new SizeArgument()),
	MOVES(new MovesArgument()),
	STATS(new FullStatsArgument()),
	HP(new SingleStatArgument(StatsType.HP)),
	ATTACK(new SingleStatArgument(StatsType.Attack)),
	DEFENCE(new SingleStatArgument(StatsType.Defence)),
	SPECIAL_ATTACK(new SingleStatArgument(StatsType.SpecialAttack)),
	SPECIAL_DEFENCE(new SingleStatArgument(StatsType.SpecialDefence)),
	SPEED(new SingleStatArgument(StatsType.Speed)),
	REWARD(new RewardArgument()),
	CAPACITY_RESTRAINT(new CapacityRestraintArgument()),
	COOLDOWN_RESTRAINT(new CooldownRestraintArgument()),
	NO_REBATTLE_RESTRAINT(new NoRebattleRestraintArgument()),
	SPECIES_CLAUSE_RESTRAINT(new SpeciesClauseRestraintArgument()),
	HELD_ITEM(new HeldItemArgument()),
	SHOW_OVERLAY(new ShowOverlayArgument()),
	ALLOW_DYNAMAX(new AllowDynamaxArgument())
	;

	private IRaidsArgument argument;

	CreateCommandArguments(@Nonnull IRaidsArgument argument) {
		this.argument = argument;
	}

	public IRaidsArgument getArgument() {
		return argument;
	}
}
