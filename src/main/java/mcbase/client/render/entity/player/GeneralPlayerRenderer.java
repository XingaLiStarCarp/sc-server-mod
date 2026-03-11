package mcbase.client.render.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class GeneralPlayerRenderer extends EntityRenderer<AbstractClientPlayer> {
	private class CustomPlayerRenderer extends PlayerRenderer {
		public CustomPlayerRenderer(Context context, boolean slim) {
			super(context, slim);
		}

		@Override
		public ResourceLocation getTextureLocation(AbstractClientPlayer entity) {
			return GeneralPlayerRenderer.this.getTextureLocation(entity);
		}

		protected boolean shouldShowName(AbstractClientPlayer entity) {
			return GeneralPlayerRenderer.this.shouldShowName(entity);
		}
	}

	private PlayerRenderer wideRenderer;
	private PlayerRenderer slimRenderer;

	public GeneralPlayerRenderer(EntityRendererProvider.Context context) {
		super(context);
		wideRenderer = this.new CustomPlayerRenderer(context, false);
		slimRenderer = this.new CustomPlayerRenderer(context, true);
	}

	protected boolean shouldShowName(AbstractClientPlayer entity) {
		return super.shouldShowName(entity);
	}

	/**
	 * 实体是否是slim类型的皮肤
	 * 
	 * @param entity
	 * @return
	 */
	protected abstract boolean isSlim(AbstractClientPlayer entity);

	@Override
	public void render(AbstractClientPlayer entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		if (this.isSlim(entity))
			slimRenderer.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		else
			wideRenderer.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
