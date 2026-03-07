package sc.server.api.entity.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * 让实体与目标点保持固定距离的Goal
 */
public abstract class KeepDistanceGoal extends NavigationGoal {
	protected double speedModifier;

	/**
	 * 要保持的目标距离
	 */
	private double keepDistance;

	/**
	 * 允许的距离误差值，在keepDistance ± toleranceRange范围内实体不移动
	 */
	private double tolerance;

	private double minDistanceSqr;
	private double maxDistanceSqr;

	public KeepDistanceGoal(Mob mob, double keepDistance, double tolerance, double speedModifier, boolean interrupt, int updateTicks) {
		super(mob, interrupt, updateTicks);
		this.speedModifier = speedModifier;
		this.keepDistance = keepDistance;
		this.tolerance = tolerance;
	}

	public KeepDistanceGoal(Mob mob, double keepDistance, double tolerance) {
		this(mob, keepDistance, tolerance, 1.0, true, DEFAULT_UPDATE_TICKS);// 原速移动
	}

	public double getKeepDistance() {
		return keepDistance;
	}

	public double getTolerance() {
		return tolerance;
	}

	public double getMinDistanceSqr() {
		return minDistanceSqr;
	}

	public double getMaxDistanceSqr() {
		return maxDistanceSqr;
	}

	public void setKeepDistance(double keepDistance) {
		this.keepDistance = keepDistance;
		double minDistance = keepDistance - this.tolerance;
		double maxDistance = keepDistance + this.tolerance;
		this.minDistanceSqr = minDistance * minDistance;
		this.maxDistanceSqr = maxDistance * maxDistance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
		double minDistance = this.keepDistance - tolerance;
		double maxDistance = this.keepDistance + tolerance;
		this.minDistanceSqr = minDistance * minDistance;
		this.maxDistanceSqr = maxDistance * maxDistance;
	}

	/**
	 * 判断是否处于目标距离范围内
	 */
	public boolean isInRange(Vec3 targetPos) {
		double distanceSqr = this.distanceSqrTo(targetPos);
		return distanceSqr >= this.minDistanceSqr && distanceSqr <= this.maxDistanceSqr;
	}

	@Override
	public boolean isNavigationComplete() {
		return isInRange(this.targetPos);// 仅当距离不符合时继续
	}

	@Override
	protected void updateMovement(Vec3 targetPos, Vec3 direction) {
		// 距离过远，实体要靠近。距离过近，实体要远离
		Vec3 moveToPos = targetPos.add(direction.scale(this.keepDistance));
		this.navigation.moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speedModifier);
	}
}