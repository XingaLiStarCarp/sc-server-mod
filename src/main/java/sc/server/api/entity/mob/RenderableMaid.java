package sc.server.api.entity.mob;

import java.lang.reflect.Field;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManager;
import com.github.tartaricacid.touhoulittlemaid.entity.data.MaidTaskDataMaps;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import jvmsp.reflection;
import jvmsp.unsafe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import sc.server.api.capability.CapabilityData;
import sc.server.api.entity.EntityData;

/**
 * 可以作为女仆模型渲染的类
 */
public interface RenderableMaid {
	/**
	 * 创建一个虚假女仆实体，仅用于渲染
	 * 
	 * @param templateEntity 数据模板，新的EntityMaid将沿用此实体的数据。
	 * @return
	 */
	public static EntityMaid blankEntityMaid(Mob templateEntity) {
		class Symbols {
			private static Field EntityMaid_chatBubbleManager;
			private static Field EntityMaid_taskDataMaps;

			static {
				EntityMaid_chatBubbleManager = reflection.find_declared_field(EntityMaid.class, "chatBubbleManager");
				EntityMaid_taskDataMaps = reflection.find_declared_field(EntityMaid.class, "taskDataMaps");
			}
		}
		EntityMaid entity = EntityData.blankEntity(EntityMaid.class);
		EntityData.copyMobData(templateEntity, entity);
		unsafe.write(entity, Symbols.EntityMaid_chatBubbleManager, new ChatBubbleManager(entity));
		unsafe.write(entity, Symbols.EntityMaid_taskDataMaps, new MaidTaskDataMaps());
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

	/**
	 * 获取渲染的虚假EntityMaid实体。<br>
	 * 该虚假实体需要通过blankEntityMaid()方法创建。<br>
	 * 
	 * @return
	 */
	public abstract EntityMaid renderingEntity();

	/**
	 * 渲染时要绑定的实体数据，包括其位置信息、角度、血量等。<br>
	 * 
	 * @return
	 */
	public abstract Mob bindEntity();

	String getModelId();

	void setModelId(String modelId);

	boolean isYsmModel();

	void setIsYsmModel(boolean isYsmModel);

	String getYsmModelId();

	void setYsmModelId(String ysmModelId);

	String getYsmModelTexture();

	void setYsmModelTexture(String ysmModelTexture);

	Component getYsmModelName();

	void setYsmModelName(Component ysmModelName);

	/**
	 * 设置YSM模型
	 * 
	 * @param ysmModelId
	 * @param ysmModelTexture
	 * @param ysmModelName
	 */
	public default void setYsmModel(String ysmModelId, String ysmModelTexture, Component ysmModelName) {
		setYsmModelId(ysmModelId);
		setYsmModelTexture(ysmModelTexture);
		setYsmModelName(ysmModelName);
	}

	/**
	 * 同步EntityMaid虚假实体的模型.<br>
	 * 同时同步绑定的的实体数据到maid，包括渲染使用到的实体相关数据。<br>
	 * 该方法必须被子类调用以实时同步渲染模型，每tick调用一次即可。<br>
	 */
	public default void syncModel() {
		renderingEntity().setModelId(this.getModelId());
		renderingEntity().setIsYsmModel(this.isYsmModel());
		renderingEntity().setYsmModel(this.getYsmModelId(), this.getYsmModelTexture(), this.getYsmModelName());
		EntityData.copyMobData(bindEntity(), renderingEntity());
	}

	public default void playRouletteAnim(String rouletteAnim) {
		renderingEntity().playRouletteAnim(rouletteAnim);
	}

	public default void stopRouletteAnim() {
		renderingEntity().stopRouletteAnim();
	}
}
