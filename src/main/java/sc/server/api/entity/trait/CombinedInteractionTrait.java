package sc.server.api.entity.trait;

import java.util.function.Function;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityInteractions.CombinedTask;

/**
 * 组合动作。<br>
 * 客户端和服务端需要分开定义为两个不同的类，防止服务端运行时链接客户端类。<br>
 */
public abstract class CombinedInteractionTrait extends InteractionTrait {

	private CombinedTask consumeItemsTask;

	/**
	 * 所有交互都成功的动作
	 */
	public abstract void onSuccess(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand);

	/**
	 * 单次交互成功的动作
	 */
	public void onSingleSuccess(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand) {

	}

	/**
	 * 没有全部交互成功的动作
	 */
	public void onAnyFailed(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand) {

	}

	@FunctionalInterface
	public static interface CombinedInteractionOp {
		public abstract boolean interact(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand);
	}

	/**
	 * 将交互操作函数转换为CombinedTask使用的函数
	 * 
	 * @param op
	 * @return
	 */
	public static final Function<Object[], Boolean> combineTaskInteraction(final CombinedInteractionOp op) {
		return (Object... args) -> {
			return op.interact((PlayerInteractEvent.EntityInteract) args[0], (Player) args[1], (BaseMob) args[2], (InteractionHand) args[3]);
		};
	}

	@SuppressWarnings("unchecked")
	public static final Function<Object[], Boolean>[] combineTaskInteractions(CombinedInteractionOp... ops) {
		Function<Object[], Boolean>[] convertedOps = new Function[ops.length];
		for (int i = 0; i < ops.length; ++i) {
			convertedOps[i] = combineTaskInteraction(ops[i]);
		}
		return convertedOps;
	}

	public CombinedInteractionTrait(boolean isClient, CombinedInteractionOp... ops) {
		super(isClient);
		this.consumeItemsTask = new CombinedTask(
				(Object... args) -> this.onSingleSuccess((PlayerInteractEvent.EntityInteract) args[0], (Player) args[1], (BaseMob) args[2], (InteractionHand) args[3]),
				(Object... args) -> this.onAnyFailed((PlayerInteractEvent.EntityInteract) args[0], (Player) args[1], (BaseMob) args[2], (InteractionHand) args[3]),
				(Object... args) -> this.onSuccess((PlayerInteractEvent.EntityInteract) args[0], (Player) args[1], (BaseMob) args[2], (InteractionHand) args[3]),
				combineTaskInteractions(ops));
	}

	public CombinedInteractionTrait(CombinedInteractionOp... ops) {
		this(false, ops);
	}

	@Override
	public void interact(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand) {
		consumeItemsTask.execute(event, player, mob, hand);
	}
}
