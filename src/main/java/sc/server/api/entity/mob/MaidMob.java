package sc.server.api.entity.mob;

import java.lang.reflect.Field;
import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManager;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.data.MaidTaskDataMaps;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import jvmsp.reflection;
import jvmsp.unsafe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.capability.CapabilityData;
import sc.server.api.client.render.entity.mob.MaidRenderer;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.EntityRendererType;

/**
 * Touhou Little Maid模组的女仆类型实体，需要配合渲染器EntityMaidRenderer使用
 */
public class MaidMob extends BaseMob {
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

	public static final EntityRendererType<MaidModelInfo> RENDERER_TYPE = new EntityRendererType<>(MaidRenderer.class);

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	static {
		RENDERER_TYPE.setDefaultRenderAsset(new MaidModelInfo("touhou_little_maid:hakurei_reimu"));
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
	public static final EntityDataAccessor<ChatBubbleDataCollection> ACC_CHAT_BUBBLE = EntityData.define(MaidMob.class, ChatBubbleRegister.INSTANCE, ChatBubbleDataCollection.getEmptyCollection());

	private static Field EntityMaid_chatBubbleManager;
	private static Field EntityMaid_taskDataMaps;

	static {
		EntityMaid_chatBubbleManager = reflection.find_declared_field(EntityMaid.class, "chatBubbleManager");
		EntityMaid_taskDataMaps = reflection.find_declared_field(EntityMaid.class, "taskDataMaps");
	}

	/**
	 * 创建一个虚假女仆实体，仅用于渲染
	 * 
	 * @param templateEntity 数据模板，新的EntityMaid将沿用此实体的数据。
	 * @return
	 */
	public static final EntityMaid blankEntityMaid(Mob templateEntity) {
		EntityMaid entity = EntityData.blankEntity(EntityMaid.class);
		EntityData.copyMobData(templateEntity, entity);
		unsafe.write(entity, MaidMob.EntityMaid_chatBubbleManager, new ChatBubbleManager(entity));
		unsafe.write(entity, MaidMob.EntityMaid_taskDataMaps, new MaidTaskDataMaps());
		entity.renderState = MaidRenderState.ENTITY;
		entity.rouletteAnimPlaying = false;
		entity.rouletteAnim = "empty";
		entity.rouletteAnimDirty = false;
		entity.roamingVarsUpdateFlag = 0;
		entity.roamingVars = new Object2FloatOpenHashMap<>();
		entity.animationId = 0;
		entity.animationRecordTime = -1L;
		entity.shouldReset = false;
		CapabilityData.gatherCapabilities(entity);// 必须！否则YSM不会渲染模型
		return entity;
	}

	private EntityMaid dummyEntityMaid;

	/**
	 * 虚假女仆实体，不实际存在于游戏中，仅仅用于模型渲染
	 * 
	 * @return
	 */
	public final EntityMaid renderingEntity() {
		return dummyEntityMaid;
	}

	public MaidMob(EntityType<BaseMob> entityType, EntityRendererType<String> renderType, Level level) {
		super(entityType, renderType, level);
		dummyEntityMaid = blankEntityMaid(this);
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
		EntityData.storeString(compound, TAG_TLM_MODEL_ID, entityData, ACC_TLM_MODEL_ID);
		EntityData.storeBool(compound, TAG_IS_YSM_MODEL, entityData, ACC_IS_YSM_MODEL);
		EntityData.storeString(compound, TAG_YSM_MODEL_ID, entityData, ACC_YSM_MODEL_ID);
		EntityData.storeString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ACC_YSM_MODEL_TEXTURE);
		EntityData.storeComponent(compound, TAG_YSM_MODEL_NAME, entityData, ACC_YSM_MODEL_NAME);
	}

	/**
	 * 同步EntityMaid虚假实体的模型
	 */
	protected final void syncModel() {
		dummyEntityMaid.setModelId(this.getModelId());
		dummyEntityMaid.setIsYsmModel(this.isYsmModel());
		dummyEntityMaid.setYsmModel(this.getYsmModelId(), this.getYsmModelTexture(), this.getYsmModelName());
	}

	@Override
	public void tick() {
		super.tick();
		this.syncModel();// 同步渲染模型
		EntityData.copyMobData(this, dummyEntityMaid);// 同步实体数据
	}

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

	public final boolean isYsmModel() {
		return entityData.get(ACC_IS_YSM_MODEL);
	}

	public final void setIsYsmModel(boolean isYsmModel) {
		entityData.set(ACC_IS_YSM_MODEL, isYsmModel);
	}

	public Mob asEntity() {
		return this;
	}

	public final String getYsmModelId() {
		return entityData.get(ACC_YSM_MODEL_ID);
	}

	protected final void setYsmModelId(String modelId) {
		entityData.set(ACC_YSM_MODEL_ID, modelId);
	}

	/**
	 * 设置YSM模型
	 */
	public void setYsmModel(String modelId, String texture, Component name) {
		this.setYsmModelId(modelId);
		this.setYsmModelTexture(texture);
		this.setYsmModelName(name);
	}

	public String getYsmModelTexture() {
		return entityData.get(ACC_YSM_MODEL_TEXTURE);
	}

	protected void setYsmModelTexture(String texture) {
		entityData.set(ACC_YSM_MODEL_TEXTURE, texture);
	}

	public Component getYsmModelName() {
		return entityData.get(ACC_YSM_MODEL_NAME);
	}

	protected void setYsmModelName(Component name) {
		entityData.set(ACC_YSM_MODEL_NAME, name);
	}

	public void playRouletteAnim(String rouletteAnim) {
		dummyEntityMaid.playRouletteAnim(rouletteAnim);
	}

	public void stopRouletteAnim() {
		dummyEntityMaid.stopRouletteAnim();
	}
}
