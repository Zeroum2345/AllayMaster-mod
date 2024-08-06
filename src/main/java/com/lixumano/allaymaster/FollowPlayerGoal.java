package com.lixumano.allaymaster;

import com.lixumano.allaymaster.allays.TameableAllay;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal{
    public static final int TELEPORT_DISTANCE = 12;
    private static final int HORIZONTAL_RANGE = 2;
    private static final int HORIZONTAL_VARIATION = 3;
    private static final int VERTICAL_VARIATION = 1;
    private final TameableAllay tameableAllay;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowPlayerGoal(TameableAllay tameableAllay, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.tameableAllay = tameableAllay;
        this.world = tameableAllay.getWorld();
        this.speed = speed;
        this.navigation = tameableAllay.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(tameableAllay.getNavigation() instanceof MobNavigation) && !(tameableAllay.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowPlayerGoal");
        }
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.tameableAllay.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (livingEntity.isSpectator()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else if (this.tameableAllay.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else {
            return !(this.tameableAllay.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
        }
    }

    private boolean cannotFollow() {
        return this.tameableAllay.isSitting() || this.tameableAllay.hasVehicle() || this.tameableAllay.isLeashed();
    }

    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameableAllay.getPathfindingPenalty(PathNodeType.WATER);
        this.tameableAllay.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameableAllay.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    public void tick() {
        this.tameableAllay.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameableAllay.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            if (this.tameableAllay.squaredDistanceTo(this.owner) >= 144.0) {
                this.tryTeleport();
            } else {
                this.navigation.startMovingTo(this.owner, this.speed);
            }

        }
    }

    private void tryTeleport() {
        BlockPos blockPos = this.owner.getBlockPos();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) {
                return;
            }
        }

    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.tameableAllay.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.tameableAllay.getYaw(), this.tameableAllay.getPitch());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType((MobEntity) this.world, pos.mutableCopy());
        BlockState blockState = this.world.getBlockState(pos.down());

        if (pathNodeType != PathNodeType.WALKABLE && !(blockState.getBlock() instanceof AirBlock)) {
            return false;
        } else {
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.tameableAllay.getBlockPos());
                return this.world.isSpaceEmpty(this.tameableAllay, this.tameableAllay.getBoundingBox().offset(blockPos));
            }
        }
    }

    private int getRandomInt(int min, int max) {
        return this.tameableAllay.getRandom().nextInt(max - min + 1) + min;
    }
}
