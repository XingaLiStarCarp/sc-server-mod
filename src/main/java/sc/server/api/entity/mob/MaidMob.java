package sc.server.api.entity.mob;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.EntityRendererType;

/**
 * Touhou Little Maid模组的女仆类型实体，需要配合渲染器EntityMaidRenderer使用
 */
public class MaidMob extends BaseMob implements IMaid {
	/**
	 * 女仆模型信息。<br>
	 * 包含TLM的模型信息和YSM的模型信息。<br>
	 */
	public static final class MaidModelInfo {
		private String tlmModelId;
		private String ysmModelId;
		private String ysmModelTexture;
		private Component ysmModelName;

		public MaidModelInfo(String tlmModelId, String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this.tlmModelId = tlmModelId;
			this.ysmModelId = ysmModelId;
			this.ysmModelTexture = ysmModelTexture;
			this.ysmModelName = ysmModelName;
		}

		public MaidModelInfo(String tlmModelId) {
			this(tlmModelId, "", "", Component.empty());
		}

		public MaidModelInfo(String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this("", ysmModelId, ysmModelTexture, ysmModelName);
		}

		public String getTlmModelId() {
			return tlmModelId;
		}

		public String getYsmModelId() {
			return ysmModelId;
		}

		public String getYsmModelTexture() {
			return ysmModelTexture;
		}

		public Component getYsmModelName() {
			return ysmModelName;
		}
	}

	public static final EntityRendererType<MaidModelInfo> RENDERER_TYPE = new EntityRendererType<>(EntityMaidRenderer.class);

	static {
		RENDERER_TYPE.setDefaultRenderAsset(new MaidModelInfo(
				"touhou_little_maid:hakurei_reimu"));
	}

	public static final String TAG_TLM_MODEL_ID = "tlm_model_id";
	public static final String TAG_IS_YSM_MODEL = "is_ysm_model";
	public static final String TAG_YSM_MODEL_ID = "ysm_model_id";
	public static final String TAG_YSM_MODEL_TEXTURE = "ysm_model_texture";
	public static final String TAG_YSM_MODEL_NAME = "ysm_model_name";

	public static final EntityDataAccessor<String> ACC_TLM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().getTlmModelId());
	public static final EntityDataAccessor<Boolean> ACC_IS_YSM_MODEL = EntityData.define(MaidMob.class, EntityDataSerializers.BOOLEAN, false);
	public static final EntityDataAccessor<String> ACC_YSM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().getYsmModelId());
	public static final EntityDataAccessor<String> ACC_YSM_MODEL_TEXTURE = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().getYsmModelTexture());
	public static final EntityDataAccessor<Component> ACC_YSM_MODEL_NAME = EntityData.define(MaidMob.class, EntityDataSerializers.COMPONENT, RENDERER_TYPE.defaultRenderAsset().getYsmModelName());

	public MaidMob(EntityType<BaseMob> entityType, EntityRendererType<String> renderType, Level level) {
		super(entityType, renderType, level);
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.loadString(compound, TAG_TLM_MODEL_ID, entityData, ACC_TLM_MODEL_ID);
		EntityData.loadBool(compound, TAG_IS_YSM_MODEL, entityData, ACC_IS_YSM_MODEL);
		EntityData.loadString(compound, TAG_YSM_MODEL_ID, entityData, ACC_YSM_MODEL_ID);
		EntityData.loadString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ACC_YSM_MODEL_TEXTURE);
		EntityData.loadComponent(compound, TAG_YSM_MODEL_NAME, entityData, ACC_YSM_MODEL_NAME);
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		compound.putString(TAG_TLM_MODEL_ID, getModelId());
		compound.putBoolean(TAG_IS_YSM_MODEL, isYsmModel());
		compound.putString(TAG_YSM_MODEL_ID, getYsmModelId());
		compound.putString(TAG_YSM_MODEL_TEXTURE, getYsmModelTexture());
		compound.putString(TAG_YSM_MODEL_NAME, EntityData.string(getYsmModelName()));
	}

	@Override
	public final String getModelId() {
		return entityData.get(ACC_TLM_MODEL_ID);
	}

	/**
	 * 设置TLM模型ID
	 * 
	 * @param modelId 模型ID
	 */
	public final void setModelId(String modelId) {
		entityData.set(ACC_TLM_MODEL_ID, modelId);
	}

	@Override
	public final boolean isYsmModel() {
		return entityData.get(ACC_IS_YSM_MODEL);
	}

	@Override
	public final void setIsYsmModel(boolean isYsmModel) {
		entityData.set(ACC_IS_YSM_MODEL, isYsmModel);
	}

	@Override
	public Mob asEntity() {
		return this;
	}

	@Override
	public final String getYsmModelId() {
		return entityData.get(ACC_YSM_MODEL_ID);
	}

	protected final void setYsmModelId(String modelId) {
		entityData.set(ACC_YSM_MODEL_ID, modelId);
	}

	/**
	 * 设置YSM模型
	 */
	@Override
	public void setYsmModel(String modelId, String texture, Component name) {
		this.setYsmModelId(modelId);
		this.setYsmModelTexture(texture);
		this.setYsmModelName(name);
	}

	@Override
	public String getYsmModelTexture() {
		return entityData.get(ACC_YSM_MODEL_TEXTURE);
	}

	protected void setYsmModelTexture(String texture) {
		entityData.set(ACC_YSM_MODEL_TEXTURE, texture);
	}

	@Override
	public Component getYsmModelName() {
		return entityData.get(ACC_YSM_MODEL_NAME);
	}

	protected void setYsmModelName(Component name) {
		entityData.set(ACC_YSM_MODEL_NAME, name);
	}

	@Override
	public void playRouletteAnim(String rouletteAnim) {
	}

	@Override
	public void stopRouletteAnim() {
	}
}
