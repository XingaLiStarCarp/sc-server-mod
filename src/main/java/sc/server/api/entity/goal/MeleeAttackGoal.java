package sc.server.api.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import sc.server.api.TimeUtils;

public class MeleeAttackGoal extends InDistanceGoal {
	protected int attackInterval;
	protected AttributeInstance attackSpeedAttribute;

	public static final int ENTITY_ATTRIBUTE_ATTACK_INTERVAL = -1;

	public MeleeAttackGoal(Mob mob, double distance, int attackInterval) {
		super(mob, distance);
		this.attackInterval = attackInterval;
		this.attackSpeedAttribute = mob.getAttribute(Attributes.ATTACK_SPEED);
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}

	public MeleeAttackGoal(Mob mob, double distance) {
		this(mob, distance, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	@Override
	public void update() {
		if (this.attackInterval > 0) {
			// 手动设置了attackInterval
			this.mob.swing(InteractionHand.MAIN_HAND);
			this.mob.doHurtTarget(mob.getTarget());
		} else {
			// 使用实体的的attack speed
			int attributeAttackInterval = (int) (TimeUtils.TICKS_PER_SECOND / attackSpeedAttribute.getBaseValue());
			if (this.getTicks() % attributeAttackInterval == 0) {
				this.mob.swing(InteractionHand.MAIN_HAND);
				this.mob.doHurtTarget(mob.getTarget());
			}
		}
	}
}
