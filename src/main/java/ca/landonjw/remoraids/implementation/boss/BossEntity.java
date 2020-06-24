package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.battles.BossBattleRegistry;
import ca.landonjw.remoraids.implementation.boss.engage.BossEngager;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.messages.channels.ActionBarChannel;
import ca.landonjw.remoraids.internal.messages.channels.BossBarChannel;
import ca.landonjw.remoraids.internal.messages.channels.OverlayChannel;
import ca.landonjw.remoraids.internal.messages.channels.TitleChannel;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.world.BossInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Implementation of {@link IBossEntity}.
 *
 * Contains one EntityStatue that serves to be scaled and appear for players, and one
 * invisible EntityPixelmon that is used as a medium for players to battle.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossEntity implements IBossEntity {

    /** The spawner this entity was created from */
    private IBossSpawner spawner;
    /** The boss entity is created from. */
    private IBoss boss;
    /** The visual representation of the boss entity. */
    private EntityStatue entity;
    /** Entity that produces participants for players to fight. */
    private EntityPixelmon battleEntity;
    /** Used for players to engage battles with the boss entity. */
    private IBossEngager bossEngager;

    /**
     * Constructor for the boss entity.
     *
     * @param spawner      The spawner responsible for creating this entity
     * @param boss         boss entity is created from
     * @param entity       visual representation of the boss entity
     * @param battleEntity the entity used to produce participants for players to fight
     */
    public BossEntity(@NonNull IBossSpawner spawner,
                      @NonNull IBoss boss,
                      @NonNull EntityStatue entity,
                      @NonNull EntityPixelmon battleEntity){
        this.spawner = Objects.requireNonNull(spawner, "spawner cannot be null");
        this.boss = Objects.requireNonNull(boss, "boss must not be null");
        this.entity = entity;
        this.battleEntity = battleEntity;
        setEngager();
        vanishBattleEntity();

        BossEntityRegistry.getInstance().register(this);
        BossBattleRegistry.getInstance().createBossBattle(this);
    }

    /**
     * Sets the engager of the entity depending on configuration values.
     */
    private void setEngager(){
        Config config = RemoRaids.getMessageConfig();
        IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

        IParsingContext context = IParsingContext.builder()
                .add(IBoss.class, this::getBoss)
                .build();
        String parsedMessage = service.interpret(config.get(MessageConfig.RAID_ENGAGE), context);
        float engageRange = RemoRaids.getGeneralConfig().get(GeneralConfig.ENGAGE_RANGE);

        switch(RemoRaids.getGeneralConfig().get(GeneralConfig.ENGAGE_MESSAGE_TYPE)){
            case 1:
                IMessageChannel actionBar = new ActionBarChannel();
                bossEngager = new BossEngager(this, actionBar, engageRange, parsedMessage);
                break;
            case 2:
                IMessageChannel bossBar = new BossBarChannel(BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
                bossEngager = new BossEngager(this, bossBar, engageRange, parsedMessage);
                break;
            case 3:
                PokemonSpec spec = new PokemonSpec();
                spec.name = boss.getPokemon().getSpecies().name;
                spec.form = boss.getPokemon().getFormEnum().getForm();
                spec.shiny = boss.getPokemon().isShiny();
                NoticeOverlay.Builder overlayBuilder = NoticeOverlay.builder()
                        .setLayout(EnumOverlayLayout.LEFT_AND_RIGHT)
                        .setPokemonSprite(spec);
                IMessageChannel overlay = new OverlayChannel(overlayBuilder);
                bossEngager = new BossEngager(this, overlay, engageRange, parsedMessage);
                break;
            case 4:
                IMessageChannel title = new TitleChannel(SPacketTitle.Type.SUBTITLE);
                bossEngager = new BossEngager(this, title, engageRange, parsedMessage);
                break;
        }
    }

    /**
     * Keeps the battle entity permanently vanished to all players in the area.
     */
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
    public IBoss getBoss() {
        return boss;
    }

    @Override
    public IBossSpawner getSpawner() {
        return this.spawner;
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
        IBossBattle battle = battleRegistry.getBossBattle(this).get();
        battleRegistry.removeBossBattle(this);
        boss.getBattleSettings().getBattleRestraints().forEach(IBattleRestraint::onBossDespawn);
        BossDeathEvent deathEvent = new BossDeathEvent(this, battle);
        RemoRaids.EVENT_BUS.post(deathEvent);

        entity.setDead();
        battleEntity.setDead();

        /* This is delayed by one tick due to drops not cancelling properly due to the pixelmon entity
         * queueing drop items on death and the boss being deregistered simultaneously, causing the
         * drop listener to be incapable of cancelling the event.
         */
        Task.builder().execute(() -> BossEntityRegistry.getInstance().deregister(this))
                .delay(1)
                .iterations(1)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossEntity that = (BossEntity) o;
        return Objects.equals(boss, that.boss) &&
                Objects.equals(entity, that.entity) &&
                Objects.equals(battleEntity, that.battleEntity) &&
                Objects.equals(bossEngager, that.bossEngager);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(boss, entity, battleEntity, bossEngager);
    }
}
