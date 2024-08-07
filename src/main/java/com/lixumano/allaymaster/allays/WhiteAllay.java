package com.lixumano.allaymaster.allays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class WhiteAllay extends TameableAllay{
    public WhiteAllay(EntityType<? extends TameableAllay> entityType, World world) {
        super(entityType, world);
    }

    private int tickCount;

    @Override
    public void tick() {
        super.tick();

        tickCount++;

        if(tickCount>=300){
            this.useAbility();
            tickCount=0;
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    private void useAbility(){
        ItemStack itemStack = this.getMainHandStack();

        if(!itemStack.isEmpty() && itemStack.isOf(Items.POTION) && this.getOwner() != null){
            List<StatusEffectInstance> effects =
                    (List<StatusEffectInstance>) itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getEffects();

            if(this.getOwner().distanceTo(this) <= 20) {

                Vec3d ownerPos = this.getOwner().getPos();

                this.getWorld().getEntitiesByType(
                        TypeFilter.instanceOf(HostileEntity.class),
                        new Box(ownerPos.x-8, ownerPos.y-8, ownerPos.z-8, ownerPos.x+8, ownerPos.y+8, ownerPos.z+8),
                        new Predicate<>() {
                            @Override
                            public boolean test(HostileEntity hostileEntity) {
                                return true;
                            }
                        }
                ).forEach(entity -> {
                    for (StatusEffectInstance effect : effects) {
                        int duration = effect.getDuration();

                        if(duration >= 100){duration = 100;}

                        entity.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), duration, effect.getAmplifier()), this);
                    }
                });

            }

        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(itemStack.isOf(Items.NETHER_STAR) && this.getOwner() == null){
            return this.tryTame(player, hand, Items.NETHER_STAR);
        }

        if(this.getOwner() != null){
            if((itemStack.isOf(Items.POTION) || itemStack.isEmpty()) && this.getOwner().equals(player)){
                return super.interactMob(player, hand);
            }
        }

        return ActionResult.FAIL;
    }
}
