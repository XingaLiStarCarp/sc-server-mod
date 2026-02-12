package sc.server.entity.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.entity.npc.Gender;
import sc.server.api.entity.npc.Interaction;
import sc.server.api.entity.npc.Npc;

/**
 * 模拟经营的顾客
 */
public class Customer extends Npc {
	public static final String CUSTOMER_MALE_TYPE_NAME = "npc_male_customer";

	public static final RegistryObject<EntityType<Npc>> CUSTOMER_MALE_TYPE = newType(Customer.class, Gender.MALE, CUSTOMER_MALE_TYPE_NAME, Npc.BASIC_ATTRIBUTES);

	public static final String CUSTOMER_FEMALE_TYPE_NAME = "npc_female_customer";

	public static final RegistryObject<EntityType<Npc>> CUSTOMER_FEMALE_TYPE = newType(Customer.class, Gender.FEMALE, CUSTOMER_FEMALE_TYPE_NAME, Npc.BASIC_ATTRIBUTES);

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public Customer(EntityType<Npc> entityType, Gender gender, String name, ResourceLocation skin, Level level) {
		super(entityType, gender, name, skin, level);
		this.onInteract(
				Interaction.list(
						Interaction.receiveItemFromPlayerMainHand(Items.DIAMOND, 2),
						Interaction.receiveItemFromPlayerMainHand(Items.GOLD_INGOT, 2),
						Interaction.receiveItemFromPlayerMainHand(Items.IRON_INGOT, 1)),
				Interaction.chatToPlayer("success"));
	}

	public Customer(EntityType<Npc> entityType, Gender gender, ResourceLocation skin, Level level) {
		this(entityType, gender, null, skin, level);
	}

	public Customer(EntityType<Npc> entityType, Gender gender, String name, Level level) {
		this(entityType, gender, name, null, level);
	}

	public Customer(EntityType<Npc> entityType, Gender gender, Level level) {
		this(entityType, gender, null, null, level);
	}

	public static void init() {

	}
}
