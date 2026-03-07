package sc.server.api.entity.goal;

import net.minecraft.world.entity.Mob;

/**
 * 让实体与瞄准的target实体保持一定距离的Goal
 */
public class KeepDistanceToTargetGoal extends KeepDistanceGoal {

	public KeepDistanceToTargetGoal(Mob mob, double keepDistance, double tolerance, double speedModifier, boolean interrupt, int updateTicks) {
		super(mob, keepDistance, tolerance, speedModifier, interrupt, updateTicks);
	}

	public KeepDistanceToTargetGoal(Mob mob, double keepDistance, double tolerance) {
		super(mob, keepDistance, tolerance);
	}

	@Override
	protected void updateTargetPos() {
		this.targetPos = this.retrieveTargetPos();// 实时更新目标实体的位置
	}
}
