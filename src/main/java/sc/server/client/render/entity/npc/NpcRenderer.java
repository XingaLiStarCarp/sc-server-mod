package sc.server.client.render.entity.npc;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.entity.npc.Gender;
import sc.server.api.entity.npc.Npc;
import sc.server.api.registry.Registers;

@EventBusSubscriber(modid = Registers.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class NpcRenderer extends HumanoidMobRenderer<Npc, PlayerModel<Npc>> {
	public NpcRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new PlayerModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5F);
		// 盔甲层
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
		this.addLayer(new ArrowLayer<>(context, this));
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
		this.addLayer(new SpinAttackEffectLayer<>(this, context.getModelSet()));
		this.addLayer(new BeeStingerLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(Npc entity) {
		return entity.skin();
	}

	@Override
	public void render(Npc entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		this.model.setAllVisible(true);
		super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}

	public static final class Male extends NpcRenderer {
		public Male(EntityRendererProvider.Context context) {
			super(context, false);
		}
	}

	public static final class Female extends NpcRenderer {
		public Female(EntityRendererProvider.Context context) {
			super(context, true);
		}
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.RegisterRenderers event) {
		for (RegistryObject<EntityType<Npc>> maleType : Gender.MALE.getTypes()) {
			event.registerEntityRenderer(maleType.get(), NpcRenderer.Male::new);
		}
		for (RegistryObject<EntityType<Npc>> femaleType : Gender.FEMALE.getTypes()) {
			event.registerEntityRenderer(femaleType.get(), NpcRenderer.Female::new);
		}
	}

	public static void init() {

	}
}