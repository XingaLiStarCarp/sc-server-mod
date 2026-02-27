package sc.server.entity.npc.trait;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityInteractions;
import sc.server.api.entity.trait.CombinedInteractionTrait;

public class CustomerTrait extends CombinedInteractionTrait {
	public CustomerTrait() {
		super((event, player, mob, hand) -> {
			return EntityInteractions.receiveItemFromPlayerMainHandAndHold(player, mob, "minecraft:diamond", 1);
		},
				(event, player, mob, hand) -> {
					return EntityInteractions.receiveItemFromPlayerMainHandAndHold(player, mob, "minecraft:apple", 2);
				});
	}

	@Override
	public void onSuccess(EntityInteract event, Player player, BaseMob mob, InteractionHand hand) {
		EntityInteractions.sengMsgToPlayer(player, "success");
	}
}
