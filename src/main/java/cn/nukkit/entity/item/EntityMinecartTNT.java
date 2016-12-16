package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartTNT extends EntityMinecartEmpty {

    public static final int NETWORK_ID = 97;

    public EntityMinecartTNT(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean onUpdate(int currentTick) {
        Block downSide = this.getLocation().floor().getLevelBlock();
        if (downSide.getId() == Block.ACTIVATOR_RAIL && downSide.isPowered()) {
            explode();
            kill();
        }
        return true;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        //this.dataProperties.putInt(TYPE_TNT);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.type = EntityMinecartEmpty.NETWORK_ID;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
