package sc.server.api.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import sc.server.api.client.render.entity.mob.HumanoidRenderer;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.EntityRendererType;

public class HumanoidMob extends BaseMob {
	public static final EntityRendererType<ResourceLocation> MALE_RENDERER_TYPE = new EntityRendererType<>(HumanoidRenderer.Male.class);
	public static final EntityRendererType<ResourceLocation> FEMALE_RENDERER_TYPE = new EntityRendererType<>(HumanoidRenderer.Female.class);

	static {
		MALE_RENDERER_TYPE.setDefaultRenderAsset(ResourceLocation.parse("sc:textures/entity/npc/male/ss.png"));
		FEMALE_RENDERER_TYPE.setDefaultRenderAsset(ResourceLocation.parse("sc:textures/entity/npc/female/ba/misono_mika.png"));
	}

	public static final String TAG_SKIN = "skin";

	public static final EntityDataAccessor<String> ACC_SKIN = EntityData.define(HumanoidMob.class, EntityDataSerializers.STRING, "");

	public HumanoidMob(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> renderType, Level level) {
		super(entityType, renderType, level);
	}

	public HumanoidMob(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> renderType, ResourceLocation skin, Level level) {
		super(entityType, renderType, level);
		this.setSkin(skin);
	}

	private ResourceLocation skin;

	/**
	 * 获取皮肤
	 * 
	 * @return
	 */
	public final ResourceLocation getSkin() {
		if (skin == null) {
			String storedSkin = entityData.get(ACC_SKIN);
			if (storedSkin == null || "".equals(storedSkin)) {
				setSkin((ResourceLocation) this.defaultRenderAsset());
			} else {
				skin = ResourceLocation.parse(storedSkin);// 缓存
			}
		}
		return skin;
	}

	/**
	 * 设置皮肤
	 * 
	 * @param skin
	 */
	public final void setSkin(ResourceLocation skin) {
		this.skin = skin;
		entityData.set(ACC_SKIN, skin.toString());
	}

	public final void setSkin(String skin) {
		setSkin(ResourceLocation.parse(skin));
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		compound.putString(TAG_SKIN, getSkin().toString());
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.loadString(compound, TAG_SKIN, entityData, ACC_SKIN);
	}
}
