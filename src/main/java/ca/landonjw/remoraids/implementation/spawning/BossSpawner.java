package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossSpawnedEvent;
import ca.landonjw.remoraids.api.events.BossSpawningEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.boss.BossEntity;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An implementation of {@link IBossSpawner} that simply spawns a {@link IBoss}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossSpawner implements IBossSpawner {

    /** The boss to be spawned. */
    private IBoss boss;
    /** The location to spawn the boss at. */
    private IBossSpawnLocation spawnLocation;
    /** The announcement to be sent to players when the boss is spawned. */
    private ISpawnAnnouncement announcement;

    /**
     * Constructor for the boss spawner.
     *
     * @param boss          the boss to spawn
     * @param spawnLocation the location to spawn at
     * @param announcement  the announcement to send on spawn, null for no announcement
     */
    public BossSpawner(@Nonnull IBoss boss,
                       @Nonnull IBossSpawnLocation spawnLocation,
                       @Nullable ISpawnAnnouncement announcement){
        this.boss = boss;
        this.spawnLocation = spawnLocation;
        this.announcement = announcement;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> spawn(){
        BossSpawningEvent spawningEvent = new BossSpawningEvent(boss, this);
        RemoRaids.EVENT_BUS.post(spawningEvent);

        if(!spawningEvent.isCanceled()){
            EntityStatue statue = createAndSpawnStatue();
            EntityPixelmon battleEntity = createAndSpawnBattleEntity();

            if(announcement != null){
                announcement.sendAnnouncement(this);
            }

            IBossEntity bossEntity = new BossEntity(boss, statue, battleEntity);

            BossSpawnedEvent spawnedEvent = new BossSpawnedEvent(bossEntity, this);
            RemoRaids.EVENT_BUS.post(spawnedEvent);

            return Optional.of(bossEntity);
        }
        return Optional.empty();
    }

    /**
     * Creates a statue entity to serve as a decoy for the boss.
     *
     * <p>This is used in lieu of a pixelmon entity because it will persist over chunk unloads,
     * and appears to have client side performance increases when the entity is scaled.</p>
     *
     * @return the statue entity that was created and spawned
     */
    private EntityStatue createAndSpawnStatue(){
        EntityStatue statue = new EntityStatue(spawnLocation.getWorld());

        Pokemon bossPokemon = boss.getPokemon();
        statue.setPokemon(bossPokemon);

        statue.setAnimate(true);
        statue.setAnimation("Idle");

        statue.setPixelmonScale(boss.getSize());
        if(boss.getTexture().isPresent()){
            statue.setTextureType(boss.getTexture().get());
        }

        statue.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        statue.setRotation(spawnLocation.getRotation());
        spawnLocation.getWorld().spawnEntity(statue);

        return statue;
    }

    /**
     * Creates a pixelmon entity to serve as the pixelmon player's battle against.
     *
     * @return the pixelmon entity that was created and spawned
     */
    private EntityPixelmon createAndSpawnBattleEntity(){
        EntityPixelmon battleEntity = new EntityPixelmon(spawnLocation.getWorld());

        Pokemon bossPokemon = boss.getPokemon();
        battleEntity.setPokemon(bossPokemon);
        battleEntity.enablePersistence();
        battleEntity.setPixelmonScale(0);

        battleEntity.setNoAI(true);

        battleEntity.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        spawnLocation.getWorld().spawnEntity(battleEntity);

        return battleEntity;
    }

    /** {@inheritDoc} */
    @Override
    public IBossSpawnLocation getSpawnLocation(){
        return spawnLocation;
    }

    /**
     * Gets the boss to be spawned.
     *
     * @return the boss to be spawned
     */
    public IBoss getBoss() {
        return boss;
    }

    /**
     * Gets the announcement to be sent to players when the boss is spawned.
     *
     * @return the spawn announcement
     */
    public ISpawnAnnouncement getAnnouncement() {
        return announcement;
    }

}
