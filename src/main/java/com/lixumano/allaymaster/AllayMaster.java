package com.lixumano.allaymaster;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import com.lixumano.allaymaster.allays.RedAllay;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class AllayMaster implements ModInitializer {
	public static final EntityType<RedAllay> RED_ALLAY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("allaymaster", "red_allay"),
			EntityType.Builder.create(RedAllay::new, SpawnGroup.CREATURE).dimensions(0.75f, 0.75f).build()
	);

	public static final Item RED_ALLAY_SPAWN_EGG = new SpawnEggItem(RED_ALLAY, 0xff1443, 0xff0000, new Item.Settings());

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(RED_ALLAY, RedAllay.createAllayAttributes());

		Registry.register(Registries.ITEM, Identifier.of("allaymaster", "red_allay_spawn_egg"), RED_ALLAY_SPAWN_EGG);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
			content.add(RED_ALLAY_SPAWN_EGG);
		});
	}
}