package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossSpawnedEvent;
import ca.landonjw.remoraids.api.events.BossSpawningEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.implementation.boss.BossEntity;
import ca.landonjw.remoraids.internal.pokemon.PokemonUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.client.models.smd.AnimationType;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    /** Associated Respawn Data */
    private IRespawnData respawns;

    /**
     * Constructor for the boss spawner.
     *
     * @param boss          the boss to spawn
     * @param spawnLocation the location to spawn at
     * @param announcement  the announcement to send on spawn, null for no announcement
     */
    public BossSpawner(@Nonnull IBoss boss,
                       @Nonnull IBossSpawnLocation spawnLocation,
                       @Nullable ISpawnAnnouncement announcement,
                       @Nullable IRespawnData respawns){
        this.boss = boss;
        this.spawnLocation = spawnLocation;
        this.announcement = announcement;
        this.respawns = respawns;
    }

    @Override
    public String getKey() {
        return "default";
    }

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> spawn(){
        BossSpawningEvent spawningEvent = new BossSpawningEvent(this.getBoss(), this);
        RemoRaids.EVENT_BUS.post(spawningEvent);

        if(!spawningEvent.isCanceled()){
            EntityStatue statue = createAndSpawnStatue();
            EntityPixelmon battleEntity = createAndSpawnBattleEntity();
            setStatueAnimation(statue, battleEntity);

            if(announcement != null){
                announcement.sendAnnouncement(this);
            }

            IBossEntity bossEntity = new BossEntity(this, this.getBoss(), statue, battleEntity);

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

        Pokemon bossPokemon = this.getBoss().getPokemon();
        statue.setPokemon(bossPokemon);
        statue.setPixelmonScale(this.getBoss().getSize());

        if(this.getBoss().getTexture().isPresent()){
            NBTTagCompound nbt = new NBTTagCompound();
            statue.writeToNBT(nbt);
            nbt.setString("CustomTexture", this.getBoss().getTexture().get());
            statue.readFromNBT(nbt);
        }

        statue.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        statue.setRotation(spawnLocation.getRotation());
        spawnLocation.getWorld().spawnEntity(statue);

        return statue;
    }

    /**
     * Sets the animation for the boss statue.
     * If the boss is midair and is capable of flying, the flying animation will be used.
     * Otherwise, the idle animation is used.
     *
     * @param statue the statue to apply the animation to
     * @param pixelmon the pixelmon to search for flying capabilities
     */
    private void setStatueAnimation(EntityStatue statue, EntityPixelmon pixelmon){
        BlockPos spawnPos = new BlockPos(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

        if(!spawnLocation.getWorld().getBlockState(spawnPos.down()).getMaterial().isSolid() && pixelmon.canFly()){
            statue.setIsFlying(true);
            statue.setAnimation(AnimationType.FLY);
        }
        else{
            statue.setAnimation(AnimationType.IDLE);
        }
        statue.setAnimate(true);
    }

    /**
     * Creates a pixelmon entity to serve as the pixelmon player's battle against.
     *
     * @return the pixelmon entity that was created and spawned
     */
    private EntityPixelmon createAndSpawnBattleEntity(){
        EntityPixelmon battleEntity = new EntityPixelmon(spawnLocation.getWorld());

        Pokemon bossPokemon = this.getBoss().getPokemon();
        battleEntity.setPokemon(PokemonUtils.clonePokemon(bossPokemon));
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

    /** {@inheritDoc} */
    @Override
    public void setSpawnLocation(IBossSpawnLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /** {@inheritDoc} */
    @Override
    public IBoss getBoss() {
        return this.boss;
    }

    /** {@inheritDoc} */
    @Override
    public ISpawnAnnouncement getAnnouncement() {
        return announcement;
    }

    @Override
    public Optional<IRespawnData> getRespawnData() {
        return Optional.ofNullable(this.respawns);
    }

    @Override
    public IRespawnData createRespawnData() {
        return this.respawns = IRespawnData.builder().build();
    }

    /** {@inheritDoc} */
    @Override
    public JObject serialize() {
        return new JObject()
                .add("key", this.getKey())
                .add("boss", this.boss.serialize())
                .add("announcement", this.announcement.serialize())
                .add("location", this.spawnLocation.serialize())
                .when(this.respawns, Objects::nonNull, o -> o.add("respawning", this.respawns.serialize()));
    }

    public static class BossSpawnerBuilder implements IBossSpawnerBuilder {

        private IBoss boss;
        private IBossSpawnLocation location;
        private ISpawnAnnouncement announcement;
        private IRespawnData data;

        @Override
        public IBossSpawnerBuilder boss(IBoss boss) {
            this.boss = boss;
            return this;
        }

        @Override
        public IBossSpawnerBuilder location(IBossSpawnLocation location) {
            this.location = location;
            return this;
        }

        @Override
        public IBossSpawnerBuilder announcement(ISpawnAnnouncement announcement) {
            this.announcement = announcement;
            return this;
        }

        @Override
        public IBossSpawnerBuilder respawns(IRespawnData data) {
            this.data = data;
            return this;
        }

        @Override
        public IBossSpawnerBuilder from(IBossSpawner input) {
            return this;
        }

        @Override
        public IBossSpawner build() {
            return new BossSpawner(
                    this.boss,
                    this.location,
                    this.announcement,
                    this.data
            );
        }
    }
}
