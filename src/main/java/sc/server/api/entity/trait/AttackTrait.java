package sc.server.api.entity.trait;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.BaseMob.OpComponent;
import sc.server.api.entity.BaseMob.OpComponent.Operation;
import sc.server.api.entity.BaseMob.TraitComponent;

/**
 * 实体攻击/被攻击时的特性
 */
public abstract class AttackTrait implements TraitComponent {
	private OpComponent<LivingAttackEvent> attackOps;
	private Operation<LivingAttackEvent> attackOp = (mob, event) -> {
		LivingEntity damagee = event.getEntity();
		if (mob == damagee) {
			this.onAttacked(event, event.getSource().getEntity(), mob);
		} else {
			this.onAttack(event, damagee, mob);
		}
		return true;
	};

	@Override
	public final void init(BaseMob mob) {
		this.attackOps = mob.opComponent(LivingAttackEvent.class);
		attackOps.add(attackOp);
	}

	@Override
	public final void uninit(BaseMob mob) {
		attackOps.remove(attackOp);
	}

	/**
	 * 主动攻击事件。<br>
	 * 
	 * @param event
	 * @param damagee
	 * @param mob
	 */
	public void onAttack(LivingAttackEvent event, LivingEntity damagee, BaseMob mob) {

	}

	/**
	 * 被攻击事件。<br>
	 * 
	 * @param event
	 * @param damager
	 * @param mob
	 */
	public void onAttacked(LivingAttackEvent event, Entity damager, BaseMob mob) {

	}
}
