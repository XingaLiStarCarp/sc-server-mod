package sc.server.entity.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.component.trait.entity.ConstGoalTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.entity.EntityInteractions.CombinedTask;
import sc.server.api.entity.mob.HumanoidMob;
import sc.server.entity.npc.trait.CustomerTrait;
import sc.server.entity.npc.trait.GuardTrait;

public class HumanoidGuard extends HumanoidMob {
	public static final String GUARD_MALE_TYPE_NAME = "npc_male_guard";

	public static final RegistryObject<EntityType<HumanoidGuard>> GUARD_MALE_TYPE = HumanoidMob.newMaleType(HumanoidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_MALE_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	public static final String GUARD_FEMALE_TYPE_NAME = "npc_female_guard";

	public static final RegistryObject<EntityType<HumanoidGuard>> GUARD_FEMALE_TYPE = HumanoidMob.newFemaleType(HumanoidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_FEMALE_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	protected CombinedTask consumeItems;

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public HumanoidGuard(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
		this.addTrait(new ConstGoalTrait<BaseMob>()
				.add(1, (mob) -> new PanicGoal(mob, 1.25)));
	}
}