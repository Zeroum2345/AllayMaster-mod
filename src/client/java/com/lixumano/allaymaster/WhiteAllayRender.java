package com.lixumano.allaymaster;

import net.minecraft.client.render.entity.AllayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;

public class WhiteAllayRender extends AllayEntityRenderer {
    public WhiteAllayRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(AllayEntity allayEntity) {
        return Identifier.of("allaymaster", "textures/entity/allay/white_allay.png");
    }
}
