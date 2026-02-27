package sc.server.api.entity.trait;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import sc.server.api.LogicalEnd;
import sc.server.api.entity.BaseMob.TraitComponent;

/**
 * 双端行为不同的特性。
 */
public abstract class DualEndedTrait implements TraitComponent {
	private final boolean isClient;

	public DualEndedTrait(boolean isClient) {
		this.isClient = isClient;
	}

	public final boolean isClient() {
		return isClient;
	}

	/**
	 * 判断当前是否处于正确的端上。<br>
	 * 双端事件的执行必须先验证端是否正确，否则会造成重复执行或服务端链接客户端的类。<br>
	 * 
	 * @param level 触发的Level
	 * @return
	 */
	protected final boolean validate(Level level) {
		return isClient == level.isClientSide;
	}

	@SuppressWarnings("resource")
	protected final boolean validate(Entity entity) {
		return isClient == entity.level().isClientSide;
	}

	/**
	 * 此方法仅判断运行环境，无法区分双端事件。<br>
	 * 
	 * @return
	 */
	protected final boolean validate() {
		return isClient == LogicalEnd.host().isClientAvailable();
	}
}
