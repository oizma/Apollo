package cn.nukkit.level.generator.object.tree;

import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.object.tree.DarkOakTree;
import cn.nukkit.level.generator.object.tree.JungleBigTree;
import cn.nukkit.level.generator.object.tree.JungleTree;
import cn.nukkit.level.generator.object.tree.SavannaTree;
import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 26. 10. 2016.
 */
public class EventListener implements Listener {

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

    private boolean isTwoByTwoOfType(Level level, BlockVector3 pos, int x, int z, int type) {
        return this.isTypeAt(level, pos.add(x, 0, z), type) && this.isTypeAt(level, pos.add(x + 1, 0, z), type) && this.isTypeAt(level, pos.add(x, 0, z + 1), type) && this.isTypeAt(level, pos.add(x + 1, 0, z + 1), type);
    }

    private boolean isTypeAt(Level level, BlockVector3 pos, int type) {
        return level.getBlockDataAt(pos.x, pos.y, pos.z) == type;
    }
}
