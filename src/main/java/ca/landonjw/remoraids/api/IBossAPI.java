package ca.landonjw.remoraids.api;

import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.api.registry.IRaidRegistry;

public interface IBossAPI {

    IRaidRegistry getRaidRegistry();

    IBossEntityRegistry getBossEntityRegistry();

    IBossBattleRegistry getBossBattleRegistry();

    static IBossAPI getInstance(){
        return BossAPIProvider.get();
    }

}
