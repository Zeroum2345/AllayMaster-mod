package com.lixumano.allaymaster.allays;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class RedAllay extends TameableAllay{
    public RedAllay(EntityType<? extends TameableAllay> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        this.useHability();
    }

    private void useHability(){
        ItemStack itemStack = this.getMainHandStack();

        if(!itemStack.isEmpty() && itemStack.isOf(Items.POTION) && this.getOwner() != null){
            List<StatusEffectInstance> effects =
                    (List<StatusEffectInstance>) itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getEffects();

            if(this.getOwner().distanceTo(this) <= 20) {
                for (StatusEffectInstance effect : effects) {
                    this.getOwner().addStatusEffect(new StatusEffectInstance(effect.getEffectType(), 200, effect.getAmplifier()), this);
                }
            }

        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(itemStack.isOf(Items.REDSTONE) && this.getOwner() == null){
            return this.tryTame(player, hand, Items.REDSTONE);
        }

        if(this.getOwner() != null){
            if(itemStack.isOf(Items.POTION) && this.getOwner().equals(player)){
                return super.interactMob(player, hand);
            }
        }

        return ActionResult.FAIL;
    }
}
