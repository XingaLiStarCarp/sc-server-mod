package mcbase.client.render.entity.player;

import mcbase.client.render.entity.ProxyRenderEntityRenderer;
import mcbase.entity.mob.ProxyRenderPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ProxyRenderPlayerRenderer extends ProxyRenderEntityRenderer<ProxyRenderPlayer, CustomPlayerRenderer> {

	public ProxyRenderPlayerRenderer(EntityRendererProvider.Context context) {
		super(context, new CustomPlayerRenderer(context));
	}

	public void setupModelAsset(ResourceLocation currentTextureLocation, boolean currentIsSlim) {
		this.proxyEntityRenderer.setupModelAsset(currentTextureLocation, currentIsSlim);
	}

	@Override
	protected ProxyRenderPlayer dispatchProxyEntity(Entity entity) {
		if (entity instanceof ProxyRenderPlayer player) {
			this.setupModelAsset(player.getSkin(), player.isSlim());// 先设置好当前的模型皮肤
			return player;
		} else
			return null;
	}

}
