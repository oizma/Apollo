package cn.nukkit.entity.passive;

import cn.nukkit.entity.ai.CreatureFleeAI;
import cn.nukkit.entity.ai.CreatureWanderAI;
import cn.nukkit.entity.ai.FollowItemAI;
import cn.nukkit.entity.ai.MobAIUnion;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Author: NycuRO
 * Apollo Project
 */
public class EntityMushroom extends EntityAnimal {

    public static final int NETWORK_ID = 16;

    public EntityMushroom(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setAI(new MobAIUnion(new CreatureFleeAI(this, 32), new FollowItemAI(this, Item.WHEAT, 49, 32), new CreatureWanderAI(this)));
    }

    @Override
    public float getWidth() {
        return 1.45f;
    }

    @Override
    public float getHeight(){
        return 1.12f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.2f;
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(10);
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF)};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}
