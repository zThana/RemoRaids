package ca.landonjw.remoraids.implementation;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.api.registry.IRaidRegistry;
import ca.landonjw.remoraids.implementation.battles.BossBattleRegistry;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import ca.landonjw.remoraids.internal.registry.InternalRaidsRegistry;

public class BossAPI implements IBossAPI {

	private final IRaidRegistry registry = new InternalRaidsRegistry();

	@Override
	public IRaidRegistry getRaidRegistry() {
		return this.registry;
	}

	@Override
	public IBossEntityRegistry getBossEntityRegistry() {
		return BossEntityRegistry.getInstance();
	}

	@Override
	public IBossBattleRegistry getBossBattleRegistry() {
		return BossBattleRegistry.getInstance();
	}

}
