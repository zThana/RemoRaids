package ca.landonjw.remoraids.api;

import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.implementation.BossAPI;

public interface IBossAPI {

    IBossEntityRegistry getBossEntityRegistry();

    IBossBattleRegistry getBossBattleRegistry();

    static IBossAPI getInstance(){
        return BossAPI.getInstance();
    }

}
