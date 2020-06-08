package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;

import javax.annotation.Nonnull;
import java.util.*;

public class BossEntityRegistry implements IBossEntityRegistry {

    private static BossEntityRegistry instance;

    private Map<UUID, IBossEntity> bossEntities = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> getBossEntity(@Nonnull UUID bossUniqueId) {
        return Optional.ofNullable(bossEntities.get(bossUniqueId));
    }

    /** {@inheritDoc} */
    @Override
    public Set<IBossEntity> getAllBossEntities() {
        return new HashSet<>(bossEntities.values());
    }

    void register(@Nonnull BossEntity entity){
        if(bossEntities.containsKey(entity.getBoss().getUniqueId())){
            throw new IllegalStateException("boss already has a registered entity!");
        }
        bossEntities.put(entity.getBoss().getUniqueId(), entity);
    }

    void deregister(@Nonnull BossEntity entity){
        bossEntities.remove(entity.getBoss().getUniqueId());
    }

    public static BossEntityRegistry getInstance(){
        if(instance == null){
            instance = new BossEntityRegistry();
        }
        return instance;
    }

}