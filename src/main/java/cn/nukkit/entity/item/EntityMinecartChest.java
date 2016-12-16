package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created by NycuRO on 2016/12/7.
 * Project Apollo
 */
public class EntityMinecartChest extends EntityMinecartEmpty {

    public static final int NETWORK_ID = 98;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMinecartChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        //this.dataProperties.putInt(TYPE_CHEST);
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
