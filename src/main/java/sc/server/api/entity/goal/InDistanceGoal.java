package sc.server.api.entity.goal;

import net.minecraft.world.entity.Mob;

public abstract class InDistanceGoal extends BaseGoal {
	private double distanceSqr;

	public InDistanceGoal(Mob mob, double distance, int interval) {
		super(mob, interval);
		this.setDistance(distance);
	}

	public InDistanceGoal(Mob mob, double distance) {
		this(mob, distance, DEFAULT_TICK_INTERVAL);
	}

	public double getDistanceSqr() {
		return distanceSqr;
	}

	public void setDistance(double distance) {
		this.distanceSqr = distance * distance;
	}

	@Override
	public boolean canUse() {
		return this.checkMobTarget() && this.distanceSqrToTarget() <= distanceSqr;
	}
}
