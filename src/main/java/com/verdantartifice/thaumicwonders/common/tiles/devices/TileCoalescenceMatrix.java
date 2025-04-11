package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.google.common.collect.Lists;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockCoalescenceMatrix;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileCoalescenceMatrix extends TileTW implements ITickable, IInteractWithCaster {
    protected static final int PROGRESS_PER_CHARGE = 50;
    protected static final int MAX_CHARGE = 10;
    protected static final int PLAY_EFFECTS = 4;
    protected static final int MAX_SPAWN_TIME = 320;

    protected int tickCounter = 0;
    protected int progress = 0;

    protected boolean isSpawning = false;
    protected int spawnTime = 0;
    protected int nextPillar = 0;

    private List<BlockPos> pillarList;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.isSpawning = compound.getBoolean("isSpawning");
        this.progress = compound.getShort("progress");
        this.spawnTime = compound.getShort("spawnTime");
        this.nextPillar = compound.getShort("pillar");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setBoolean("isSpawning", this.isSpawning);
        compound.setShort("progress", (short) this.progress);
        compound.setShort("spawnTime", (short) this.spawnTime);
        compound.setShort("pillar", (short) this.nextPillar);
        return compound;
    }

    @Override
    public void update() {
        this.tickCounter++;
        if (!this.world.isRemote && this.tickCounter % 20 == 0) {
            if (this.canMakeProgress()) {
                this.drainRifts();
            }
        } else if (this.isSpawning) {
            this.doSpawnCountdown();
        }
    }

    protected void doSpawnCountdown() {
        if (this.spawnTime < MAX_SPAWN_TIME) {
            if (this.world.isRemote) {
                List<BlockPos> pillars = this.getPillarList();
                //Main Effect
                int sparkingPillars = Math.min(this.nextPillar + 1, pillars.size());
                for (int i = 0; i < sparkingPillars; i++) {
                    BlockPos pillarPos = pillars.get(i);
                    performZap(pillarPos.up(2), this.getPos().up(), Aspect.ELDRITCH);
                }

                //TODO: Redo the effects using TC's jacobs sound + zap and the fx dispatcher. See Thaumic Tinkerer's enchanter
                //Pillar Arcs
                if ((this.spawnTime + 5) % 30 == 0) {
                    BlockPos pillar1 = pillars.get(this.world.rand.nextInt(pillars.size()));
                    BlockPos pillar2 = pillars.get(this.world.rand.nextInt(pillars.size()));
                    performZap(pillar1.up(2), pillar2.up(2), Aspect.FLUX);
                }
            } else {
                this.spawnTime++;
                if (this.spawnTime % (MAX_SPAWN_TIME / 8) == 0) {
                    this.world.playSound(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5, SoundsTC.jacobs, SoundCategory.BLOCKS, 0.5f, 1.0f, true);
                    this.nextPillar++;
                }
                this.syncTile(false);
                this.markDirty();
            }
        } else {
            spawnAvatar();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void performZap(BlockPos start, BlockPos end, Aspect aspect) {
        Color color = new Color(aspect.getColor());
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        FXDispatcher.INSTANCE.arcBolt(
                start.getX() + 0.5, start.getY(), start.getZ() + 0.5,
                end.getX() + 0.5, end.getY(), end.getZ() + 0.5,
                r, g, b, 0.6F);
    }

    protected void spawnAvatar() {
        if (!this.world.isRemote) {
            this.world.setBlockToAir(this.getPos());
            this.world.createExplosion(null, this.getPos().getX() + 0.5D, this.getPos().getY() + 2.5D, this.getPos().getZ() + 0.5D, 2.0F, false);
            this.world.destroyBlock(this.getPos(), false);
            for (BlockPos pillarPos : this.getPillarList()) {
                this.world.setBlockToAir(pillarPos);
            }

            // Summon the avatar
            EntityCorruptionAvatar avatar = new EntityCorruptionAvatar(this.world);
            avatar.setLocationAndAngles(this.getPos().getX() + 0.5D, this.getPos().getY(), this.getPos().getZ() + 0.5D, (float) this.world.rand.nextInt(360), 0.0F);
            this.world.spawnEntity(avatar);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public boolean isProgressFull() {
        return (this.getCharge() >= MAX_CHARGE) && (this.progress >= PROGRESS_PER_CHARGE);
    }

    protected boolean canMakeProgress() {
        return this.isValidPlacement() && !this.isProgressFull() && !this.getValidRifts().isEmpty();
    }

    public void incrementProgress(int amount) {
        this.progress += amount;
        if (this.progress >= PROGRESS_PER_CHARGE) {
            int charge = this.getCharge();
            if (charge < MAX_CHARGE) {
                this.setCharge(charge + 1);
                this.progress -= PROGRESS_PER_CHARGE;
            }
        }
        this.progress = MathHelper.clamp(this.progress, 0, PROGRESS_PER_CHARGE);
    }

    public void decrementProgress(int amount) {
        this.progress -= amount;
        if (this.progress < 0) {
            int charge = this.getCharge();
            if (charge > 0) {
                this.setCharge(charge - 1);
                this.progress += PROGRESS_PER_CHARGE;
            }
        }
        this.progress = MathHelper.clamp(this.progress, 0, PROGRESS_PER_CHARGE);
    }

    public int getCharge() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.COALESCENCE_MATRIX) {
            return state.getBlock().getMetaFromState(state);
        } else {
            return 0;
        }
    }

    public void setCharge(int amount) {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.COALESCENCE_MATRIX) {
            this.world.setBlockState(this.pos, state.withProperty(BlockCoalescenceMatrix.CHARGE, amount));
        }
    }

    protected List<EntityFluxRift> getValidRifts() {
        List<EntityFluxRift> retVal = new ArrayList<>();
        List<EntityFluxRift> riftList = EntityUtils.getEntitiesInRange(this.world, this.pos, null, EntityFluxRift.class, 16.0D);
        for (EntityFluxRift rift : riftList) {
            if (!rift.isDead && rift.getRiftSize() > 1) {
                Vec3d v1 = new Vec3d(this.pos.getX() + 0.5D, this.pos.getY() + 1.0D, this.pos.getZ() + 0.5D);
                Vec3d v2 = new Vec3d(rift.posX, rift.posY, rift.posZ);
                v1 = v1.add(v2.subtract(v1).normalize());
                if (EntityUtils.canEntityBeSeen(rift, v1.x, v1.y, v1.z)) {
                    retVal.add(rift);
                }
            }
        }
        return retVal;
    }

    protected void drainRifts() {
        List<EntityFluxRift> riftList = this.getValidRifts();
        boolean found = false;
        for (EntityFluxRift rift : riftList) {
            double drained = Math.sqrt(rift.getRiftSize());
            this.incrementProgress((int) drained);
            rift.setRiftStability(rift.getRiftStability() - (float) (drained / 15.0D));
            if (this.world.rand.nextInt(33) == 0) {
                rift.setRiftSize(rift.getRiftSize() - 1);
            }
            if (drained >= 1.0D) {
                found = true;
            }
        }
        if (found) {
            this.syncTile(false);
            this.markDirty();
            if (this.tickCounter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), PLAY_EFFECTS, this.tickCounter);
            }
        }
    }

    protected boolean isValidPlacement() {
        int i, k;

        // Check main void metal blocks
        for (i = -2; i <= 2; i++) {
            for (k = -3; k <= 3; k++) {
                if (this.world.getBlockState(this.pos.add(i, -1, k)).getBlock() != BlocksTC.metalBlockVoid) {
                    return false;
                }
            }
            if (this.world.getBlockState(this.pos.add(-3, -1, i)).getBlock() != BlocksTC.metalBlockVoid) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(3, -1, i)).getBlock() != BlocksTC.metalBlockVoid) {
                return false;
            }
        }

        // Check arcane brick blocks
        for (i = -2; i <= 2; i++) {
            if (this.world.getBlockState(this.pos.add(i, -1, -4)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(i, -1, 4)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(-4, -1, i)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(4, -1, i)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
        }
        if (this.world.getBlockState(this.pos.add(-3, -1, -3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(-3, -1, 3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(3, -1, -3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(3, -1, 3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }

        // Check pillars
        if (!(this.world.getBlockState(this.pos.add(-4, 0, -2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-4, 0, 2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(4, 0, -2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(4, 0, 2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-2, 0, -4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-2, 0, 4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(2, 0, -4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(2, 0, 4)).getBlock() instanceof BlockPillar)) {
            return false;
        }

        return true;
    }

    protected List<BlockPos> getPillarList() {
        if (this.pillarList == null) {
            this.pillarList = Lists.newArrayList(
                    this.getPos().add(-4, 0, -2),
                    this.getPos().add(-4, 0, 2),
                    this.getPos().add(4, 0, -2),
                    this.getPos().add(4, 0, 2),
                    this.getPos().add(-2, 0, -4),
                    this.getPos().add(-2, 0, 4),
                    this.getPos().add(2, 0, -4),
                    this.getPos().add(2, 0, 4)
            );
        }
        return this.pillarList;
    }

    protected void shufflePillars() {
        Collections.shuffle(this.getPillarList());
    }

    @Override
    public boolean onCasterRightClick(World world, ItemStack stack, EntityPlayer player, BlockPos pos, EnumFacing facing, EnumHand hand) {
        if (this.getCharge() >= MAX_CHARGE && !this.world.isRemote) {
            this.isSpawning = true;
            this.world.playSound(null, this.getPos(), SoundsTC.zap, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.shufflePillars();
            this.syncTile(false);
            this.markDirty();
        }
        return true;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_EFFECTS) {
            if (this.world.isRemote) {
                List<EntityFluxRift> riftList = this.getValidRifts();
                for (EntityFluxRift rift : riftList) {
                    FXDispatcher.INSTANCE.voidStreak(
                            rift.posX,
                            rift.posY,
                            rift.posZ,
                            this.pos.getX() + 0.5D,
                            this.pos.getY() + 1.0D,
                            this.pos.getZ() + 0.5D,
                            type,
                            0.04F);
                }
            }
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }
}
