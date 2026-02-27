package sc.server.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.entity.mob.MaidMob;
import sc.server.entity.npc.trait.CustomerTrait;

public class MaidCustomer extends MaidMob {

	public static final String CUSTOMER_MAID_TYPE_NAME = "npc_maid_customer";

	public static final RegistryObject<EntityType<MaidCustomer>> CUSTOMER_MAID_TYPE = BaseMob.newType(MaidCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, MaidMob.RENDERER_TYPE, CUSTOMER_MAID_TYPE_NAME, BaseMob.BASIC_ATTRIBUTES);

	public MaidCustomer(EntityType<BaseMob> entityType, EntityRendererType<String> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
	}

	public static void init() {

	}
}
