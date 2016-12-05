package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.*;
import cn.nukkit.Player;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.object.BasicGenerator;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.utils.DyeColor;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    
    public final Map<Integer, Boolean> overridable = new HashMap<Integer, Boolean>() {
        {
            put(Block.AIR, true);
            put(Block.SAPLING, true);
            put(Block.LOG, true);
            put(Block.LEAVES, true);
            put(Block.SNOW_LAYER, true);
            put(Block.LOG2, true);
            put(Block.LEAVES2, true);
        }
    };

    public int getType() {
        return 0;
    }

    public int getTrunkBlock() {
        return Block.LOG;
    }

    public int getLeafBlock() {
        return Block.LEAVES;
    }

    public int getTreeHeight() {
        return 7;
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        growTree(level, x, y, z, random, 0);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, int type) {
        ObjectTree tree;
        int i = 0;
        int j = 0;
        boolean flag = false;
        switch (type) {
            case BlockSapling.SPRUCE:
                if (random.nextBoundedInt(39) == 0) {
                    tree = new ObjectSpruceTree();
                } else {
                    tree = new ObjectSpruceTree();
                }
                break;
            case BlockSapling.BIRCH:
                if (random.nextBoundedInt(39) == 0) {
                    tree = new ObjectTallBirchTree();
                } else {
                    tree = new ObjectBirchTree();
                }
                break;
            case BlockSapling.JUNGLE:
                tree = new ObjectJungleTree();
                break;
            case BlockSapling.OAK:
            default:
                tree = new ObjectOakTree();
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Item item = e.getItem();

        if (!(item instanceof ItemDye) || ((ItemDye) item).getDyeColor() != DyeColor.WHITE) {
            return;
        }

        Level level = b.getLevel();

        if (b instanceof BlockSapling) {
            BlockSapling sapling = (BlockSapling) b;

            BlockVector3 pos = new BlockVector3(b.getFloorX(), b.getFloorY(), b.getFloorZ());

            int i = 0;
            int j = 0;
            boolean flag = false;

            BasicGenerator worldgenerator = null;

            switch (sapling.getDamage()) {
                case BlockSapling.JUNGLE:
                    jungle:
                    for (i = 0; i >= -1; --i) {
                        for (j = 0; j >= -1; --j) {
                            if (this.isTwoByTwoOfType(level, pos, i, j, BlockSapling.DARK_OAK)) {
                                worldgenerator = new JungleBigTree(10, 20, new BlockWood(BlockWood.JUNGLE), new BlockLeaves(BlockLeaves.JUNGLE));
                                flag = true;
                                break jungle;
                            }
                        }
                    }

                    if (!flag) {
                        i = 0;
                        j = 0;
                        worldgenerator = new JungleTree(4 + new NukkitRandom().nextBoundedInt(7));
                    }
                    break;
                case BlockSapling.ACACIA:
                    worldgenerator = new SavannaTree();
                    break;
                case BlockSapling.DARK_OAK:

                    spruce:
                    for (i = 0; i >= -1; --i) {
                        for (j = 0; j >= -1; --j) {
                            if (this.isTwoByTwoOfType(level, pos, i, j, BlockSapling.DARK_OAK)) {
                                worldgenerator = new DarkOakTree();
                                flag = true;
                                break spruce;
                            }
                        }
                    }

                    if (!flag) {
                        return;
                    }
                    break;
                default:
                    return;
            }

            e.setCancelled();
            BlockAir air = new BlockAir();

            if (flag) {
                level.setBlock(b.add(i, 0, j), air, true, false);
                level.setBlock(b.add(i + 1, 0, j), air, true, false);
                level.setBlock(b.add(i, 0, j + 1), air, true, false);
                level.setBlock(b.add(i + 1, 0, j + 1), air, true, false);
            } else {
                level.setBlock(b, air, true, false);
            }

            if (!worldgenerator.generate(level, new NukkitRandom(), b.add(i, 0, j))) {
                if (flag) {
                    level.setBlock(b.add(i, 0, j), b, true, false);
                    level.setBlock(b.add(i + 1, 0, j), b, true, false);
                    level.setBlock(b.add(i, 0, j + 1), b, true, false);
                    level.setBlock(b.add(i + 1, 0, j + 1), b, true, false);
                } else {
                    level.setBlock(b, b, true, false);
                }
            } else {
                item.count--;
                p.getInventory().setItemInHand(item);
            }
        } else if (b.getId() == Block.BROWN_MUSHROOM || b.getId() == Block.RED_MUSHROOM) {
            e.setCancelled();
            BigMushroom mushroom = new BigMushroom(b.getId() == Block.BROWN_MUSHROOM ? 0 : 1);

            level.setBlock(b, new BlockAir(), true, false);

            if (!mushroom.generate(level, new NukkitRandom(), b)) {
                level.setBlock(b, b, true, false);
            } else {
                item.count--;
                p.getInventory().setItemInHand(item);
            }
        }
    }

    public boolean canPlaceObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable.containsKey(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            double yOff = yy - (y + this.getTreeHeight());
            int mid = (int) (1 - yOff / 2);
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {

                        level.setBlockIdAt(xx, yy, zz, this.getLeafBlock());
                        level.setBlockDataAt(xx, yy, zz, this.getType());
                    }
                }
            }
        }
    }

    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockIdAt(x, y - 1, z, Block.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable.containsKey(blockId)) {
                level.setBlockIdAt(x, y + yy, z, this.getTrunkBlock());
                level.setBlockDataAt(x, y + yy, z, this.getType());
            }
        }
    }
    
    private boolean isTwoByTwoOfType(Level level, BlockVector3 pos, int x, int z, int type) {
        return this.isTypeAt(level, pos.add(x, 0, z), type) && this.isTypeAt(level, pos.add(x + 1, 0, z), type) && this.isTypeAt(level, pos.add(x, 0, z + 1), type) && this.isTypeAt(level, pos.add(x + 1, 0, z + 1), type);
    }

    private boolean isTypeAt(Level level, BlockVector3 pos, int type) {
        return level.getBlockDataAt(pos.x, pos.y, pos.z) == type;
    }
}
