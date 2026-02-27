package sc.server.api.entity.trait;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.BaseMob.OpComponent;
import sc.server.api.entity.BaseMob.OpComponent.Operation;

/**
 * 交互特性类。<br>
 * 交互事件是双端事件，客户端与服务端会分别触发一次，如果是本地游戏则触发两次。<br>
 */
public abstract class InteractionTrait extends DualEndedTrait {

	private OpComponent<PlayerInteractEvent.EntityInteract> interactOps;
	private Operation<PlayerInteractEvent.EntityInteract> interactOp = (mob, event) -> {
		Player player = event.getEntity();
		// 判断端
		if (this.validate(player)) {
			InteractionHand hand = event.getHand();
			if (player.getUsedItemHand() == hand) {
				this.interact(event, player, mob, hand);
			}
		}
		return true;
	};

	@Override
	public final void init(BaseMob mob) {
		this.interactOps = mob.opComponent(PlayerInteractEvent.EntityInteract.class);
		interactOps.add(interactOp);
	}

	@Override
	public final void uninit(BaseMob mob) {
		interactOps.remove(interactOp);
	}

	public InteractionTrait(boolean isClient) {
		super(isClient);
	}

	/**
	 * 交互事件。<br>
	 * 
	 * @param player
	 * @param mob
	 * @param hand
	 * @param isClient
	 */
	public abstract void interact(PlayerInteractEvent.EntityInteract event, Player player, BaseMob mob, InteractionHand hand);
}
