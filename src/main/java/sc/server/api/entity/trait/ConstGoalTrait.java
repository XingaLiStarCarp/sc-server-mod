package sc.server.api.entity.trait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.minecraft.world.entity.ai.goal.Goal;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.BaseMob.TraitComponent;

/**
 * 添加常驻的Goal的特性
 */
public class ConstGoalTrait implements TraitComponent {
	public static class GoalEntry {
		private int priority;
		private Goal goal;
		private Function<BaseMob, Goal> goalCtor;

		public GoalEntry(int priority, Function<BaseMob, Goal> goalCtor) {
			this.priority = priority;
			this.goalCtor = goalCtor;
		}

		public int priority() {
			return priority;
		}

		public Goal goal(BaseMob mob) {
			return goal == null ? (goal = goalCtor.apply(mob)) : goal;
		}

		public static final GoalEntry of(int priority, Function<BaseMob, Goal> goalCtor) {
			return new GoalEntry(priority, goalCtor);
		}
	}

	private final ArrayList<GoalEntry> goals;

	public ConstGoalTrait() {
		goals = new ArrayList<GoalEntry>();
	}

	public ConstGoalTrait add(GoalEntry entry) {
		goals.add(entry);
		return this;
	}

	public ConstGoalTrait add(Collection<GoalEntry> entries) {
		goals.addAll(entries);
		return this;
	}

	public ConstGoalTrait add(GoalEntry... entries) {
		goals.addAll(List.of(entries));
		return this;
	}

	@Override
	public void init(BaseMob mob) {
		for (GoalEntry entry : goals) {
			mob.goalSelector.addGoal(entry.priority(), entry.goal(mob));
		}
	}

	@Override
	public void uninit(BaseMob mob) {
		for (GoalEntry entry : goals) {
			mob.goalSelector.removeGoal(entry.goal(mob));
		}
	}
}
