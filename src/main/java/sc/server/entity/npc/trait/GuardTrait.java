package sc.server.entity.npc.trait;

import java.util.List;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import sc.server.api.component.trait.entity.ConstGoalTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.goal.MobGoalUtils;
import sc.server.api.entity.goal.NearestTargetGoal;
import sc.server.api.entity.goal.UseItemMeleeAttackGoal;
import sc.server.api.registry.Registers;

public class GuardTrait extends ConstGoalTrait<BaseMob> {
	public static final List<Entry> GUARD_ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 100),
			Entry.of(Attributes.MOVEMENT_SPEED, 0.25),
			Entry.of(Attributes.FOLLOW_RANGE, 32),
			Entry.of(Attributes.ARMOR, 20),
			Entry.of(Attributes.ARMOR_TOUGHNESS, 10),
			Entry.of(Attributes.ATTACK_DAMAGE, 8),
			Entry.of(Attributes.ATTACK_KNOCKBACK, 1),
			Entry.of(Attributes.ATTACK_SPEED, 5));

	String mainHandItem;
	String offhandItem;

	public GuardTrait(String mainHandItem, String offhandItem, MobCategory attackCategory) {
		super();
		this.add(0, (mob) -> new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, 1.0D));
		this.add(1, (mob) -> new RandomLookAroundGoal(mob));
		this.add(2, (mob) -> new NearestTargetGoal(mob, true, true, MobGoalUtils.entityCategory(attackCategory)));
		this.add(3, (mob) -> new UseItemMeleeAttackGoal((PathfinderMob) mob, 1.5, true, 4));
		this.mainHandItem = mainHandItem;
		this.offhandItem = offhandItem;
	}

	public GuardTrait(String mainHandItem, String offhandItem) {
		this(mainHandItem, offhandItem, MobCategory.MONSTER);
	}

	public GuardTrait(String mainHandItem) {
		this(mainHandItem, "minecraft:shield");
	}

	@Override
	public void init(BaseMob mob) {
		super.init(mob);
		mob.setMainHandHold(Registers.item(mainHandItem));
		mob.setOffHandHold(Registers.item(offhandItem));
	}
}