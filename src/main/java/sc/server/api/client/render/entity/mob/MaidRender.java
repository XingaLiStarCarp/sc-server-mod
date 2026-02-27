package sc.server.api.client.render.entity.mob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.entity.mob.MaidMob;

/**
 * Touhou Little Maid模组的实体渲染
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class MaidRender {
	@SubscribeEvent
	public static void register(EntityRenderersEvent.RegisterRenderers event) {
		MaidMob.RENDERER_TYPE.register(event);
	}
}
