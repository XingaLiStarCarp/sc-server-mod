package mcbase.ext.tlm.client.render.entity.maid;

import mcbase.client.render.entity.EntityRenderers;
import mcbase.ext.tlm.entity.maid.MaidMob;
import mcbase.ext.tlm.entity.maid.SyncedRenderMaid;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * MaidMob渲染器
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class MaidMobRenderer extends SyncedRenderMaidRenderer {
	static {
		MaidMob.RENDERER_TYPE.registerRenderer(MaidMobRenderer.class, EntityRendererProvider.Context.class);
	}

	public MaidMobRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public SyncedRenderMaid dispatch(Entity entity) {
		if (entity instanceof SyncedRenderMaid maid)
			return maid;
		else
			return null;
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.AddLayers event) {
		EntityRenderers.register(MaidMob.RENDERER_TYPE, EntityRenderers.context());
	}
}
