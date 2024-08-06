package com.lixumano.allaymaster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class AllayMasterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(AllayMaster.RED_ALLAY, RedAllayRender::new);
		EntityRendererRegistry.register(AllayMaster.WHITE_ALLAY, WhiteAllayRender::new);

	}
}