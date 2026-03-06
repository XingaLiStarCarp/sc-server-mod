package sc.server.api.component.trait.entity;

import java.util.ArrayList;
import java.util.function.Function;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import sc.server.api.component.TraitProvider.TraitComponent;

/**
 * 添加常驻的Goal的特性
 */
public class ConstGoalTrait<_GoalHolder extends Mob> implements TraitComponent<_GoalHolder> {
	private static class GoalEntry<_GoalHolder> {
		private int priority;
		private Goal goal;
		private Function<_GoalHolder, Goal> goalCtor;

		public GoalEntry(int priority, Function<_GoalHolder, Goal> goalCtor) {
			this.priority = priority;
			this.goalCtor = goalCtor;
		}

		public int priority() {
			return priority;
		}

		public Goal goal(_GoalHolder mob) {
			return goal == null ? (goal = goalCtor.apply(mob)) : goal;
		}
	}

	private final ArrayList<GoalEntry<_GoalHolder>> goals;

	public ConstGoalTrait() {
		goals = new ArrayList<>();
	}

	public ConstGoalTrait<_GoalHolder> add(int priority, Function<_GoalHolder, Goal> goalCtor) {
		goals.add(new GoalEntry<>(priority, goalCtor));
		return this;
	}

	@Override
	public void init(_GoalHolder mob) {
		for (GoalEntry<_GoalHolder> entry : goals) {
			mob.goalSelector.addGoal(entry.priority(), entry.goal(mob));
		}
	}

	@Override
	public void uninit(_GoalHolder mob) {
		for (GoalEntry<_GoalHolder> entry : goals) {
			mob.goalSelector.removeGoal(entry.goal(mob));
		}
	}
}
