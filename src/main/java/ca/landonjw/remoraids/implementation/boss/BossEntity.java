package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.implementation.battles.BossBattleRegistry;
import ca.landonjw.remoraids.implementation.boss.engage.ActionBarEngager;
import ca.landonjw.remoraids.implementation.boss.engage.BossBarEngager;
import ca.landonjw.remoraids.implementation.boss.engage.OverlayEngager;
import ca.landonjw.remoraids.implementation.boss.engage.TitleEngager;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.world.BossInfo;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class BossEntity implements IBossEntity {

    private UUID uniqueId;

    private IBoss boss;
    private EntityStatue entity;
    private EntityPixelmon battleEntity;

    private IBossEngager bossEngager;

    public BossEntity(@Nonnull IBoss boss,
                      @Nonnull EntityStatue entity,
                      @Nonnull EntityPixelmon battleEntity){
        this.uniqueId = UUID.randomUUID();
        this.boss = Objects.requireNonNull(boss, "boss must not be null");
        this.entity = entity;
        this.battleEntity = battleEntity;
        setEngager();
        vanishBattleEntity();

        BossEntityRegistry registry = BossEntityRegistry.getInstance();
        registry.register(this);
        BossBattleRegistry.getInstance().registerBossBattle(this);
    }

    private void setEngager(){
        switch(RemoRaids.getGeneralConfig().getEngageMessageType()){
            case 1:
                bossEngager = new ActionBarEngager(
                        this,
                        RemoRaids.getGeneralConfig().getEngageRange(),
                        RemoRaids.getMessageConfig().getEngageMessage()
                );
                break;
            case 2:
                bossEngager = new BossBarEngager(
                        this,
                        RemoRaids.getGeneralConfig().getEngageRange(),
                        RemoRaids.getMessageConfig().getEngageMessage(),
                        BossInfo.Color.WHITE,
                        BossInfo.Overlay.PROGRESS
                );
                break;
            case 3:
                bossEngager = new OverlayEngager(
                        this,
                        RemoRaids.getGeneralConfig().getEngageRange(),
                        RemoRaids.getMessageConfig().getEngageMessage()
                );
                break;
            case 4:
                bossEngager = new TitleEngager(
                        this,
                        RemoRaids.getGeneralConfig().getEngageRange(),
                        RemoRaids.getMessageConfig().getEngageMessage(),
                        SPacketTitle.Type.TITLE
                );
                break;
        }
    }

    private void vanishBattleEntity(){
        Task.builder()
                .execute((task) -> {
                    if(!entity.isDead){
                        for(EntityPlayer player : battleEntity.world.playerEntities){
                            if(player.getDistance(battleEntity) < 100){
                                SPacketDestroyEntities packet = new SPacketDestroyEntities(battleEntity.getEntityId());
                                ((EntityPlayerMP) player).connection.sendPacket(packet);
                            }
                        }
                    }
                    else{
                        task.setExpired();
                    }
                })
                .interval(10)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    /** {@inheritDoc} */
    @Override
    public IBoss getBoss() {
        return boss;
    }

    /** {@inheritDoc} */
    @Override
    public EntityStatue getEntity() {
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    public EntityPixelmon getBattleEntity() {
        return battleEntity;
    }

    /** {@inheritDoc} */
    @Override
    public IBossEngager getBossEngager() {
        return bossEngager;
    }

    /** {@inheritDoc} */
    @Override
    public void despawn() {
        BossBattleRegistry battleRegistry = BossBattleRegistry.getInstance();

        if(battleRegistry.getBossBattle(this).isPresent()){
            BossDeathEvent deathEvent = new BossDeathEvent(this, battleRegistry.getBossBattle(this).get());
            RemoRaids.EVENT_BUS.post(deathEvent);
        }
        else{
            BossDeathEvent deathEvent = new BossDeathEvent(this, null);
            RemoRaids.EVENT_BUS.post(deathEvent);
        }

        battleRegistry.deregisterBossBattle(this);

        entity.setDead();
        battleEntity.setDead();
        BossEntityRegistry.getInstance().deregister(this);
    }

}
