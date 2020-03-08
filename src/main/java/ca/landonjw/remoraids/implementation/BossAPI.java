package ca.landonjw.remoraids.implementation;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattleRegistry;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.implementation.battles.BossBattleRegistry;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;

public class BossAPI implements IBossAPI {

    private static BossAPI instance;

    @Override
    public IBossEntityRegistry getBossEntityRegistry() {
        return BossEntityRegistry.getInstance();
    }

    @Override
    public IBossBattleRegistry getBossBattleRegistry() {
        return BossBattleRegistry.getInstance();
    }

    public static BossAPI getInstance(){
        if(instance == null){
            instance = new BossAPI();
        }
        return instance;
    }

}
