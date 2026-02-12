package sc.server.api.entity.npc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;
import sc.server.ModEntry;
import sc.server.api.entity.EntityDefaultAttributes;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.npc.Interaction.InteractOperation;
import sc.server.api.entity.npc.Interaction.InteractionHandler;
import sc.server.api.registry.Registers;

@EventBusSubscriber(modid = Registers.MOD_ID, bus = Bus.FORGE)
public class Npc extends Mob {

	private final Gender gender;

	/**
	 * NPC所属性别
	 * 
	 * @return
	 */
	public final Gender gender() {
		return gender;
	}

	/**
	 * 注册一种新的NPC类型。<br>
	 * 不同的NPC类型具有不同的行为。<br>
	 * 
	 * @param <T>
	 * @param clazz
	 * @param gender
	 * @param typeName
	 * @param category
	 * @param attributes NPC的默认属性，属性必须至少包含MAX_HEALTH和FOLLOW_RANGE
	 * @return
	 */
	public static final <T extends Npc> RegistryObject<EntityType<Npc>> newType(Class<T> clazz, Gender gender, String typeName, MobCategory category, List<Entry> attributes) {
		ModEntry.LOGGER.info("Starting to create new npc type " + typeName);
		RegistryObject<EntityType<Npc>> type = Registers.ENTITIES.register(typeName,
				() -> EntityType.Builder.of((EntityType<Npc> entityType, Level level) -> {
					try {
						Constructor<T> constructor = clazz.getDeclaredConstructor(EntityType.class, Gender.class, String.class, ResourceLocation.class, Level.class);
						constructor.setAccessible(true);
						return constructor.newInstance(entityType, gender, null, null, level);
					} catch (Throwable ex) {
						ModEntry.LOGGER.error("Create npc of type '" + typeName + "' failed", ex);
						return null;
					}
				}, category)
						.sized(0.6F, 1.8F)
						.build(Registers.MOD_ID + ":" + typeName));
		gender.registerType(type);// 将新类型添加到指定性别
		EntityDefaultAttributes.set(type, attributes);// 设置默认的NPC属性
		return type;
	}

	public static final <T extends Npc> RegistryObject<EntityType<Npc>> newType(Class<T> clazz, Gender gender, String typeName, MobCategory category, Entry... attributes) {
		return newType(clazz, gender, typeName, category, List.of(attributes));
	}

	public static final <T extends Npc> RegistryObject<EntityType<Npc>> newType(Class<T> clazz, Gender gender, String typeName, List<Entry> attributes) {
		return newType(clazz, gender, typeName, MobCategory.MISC, attributes);
	}

	public static final <T extends Npc> RegistryObject<EntityType<Npc>> newType(Class<T> clazz, Gender gender, String typeName, Entry... attributes) {
		return newType(clazz, gender, typeName, MobCategory.MISC, attributes);
	}

	public static final List<Entry> BASIC_ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 20),
			Entry.of(Attributes.FOLLOW_RANGE, 32));

	/**
	 * 男性默认的EntityType，无任何逻辑
	 */
	public static final String TRIVIAL_MALE_TYPE_NAME = "npc_male";

	public static final RegistryObject<EntityType<Npc>> TRIVIAL_MALE_TYPE = newType(Npc.class, Gender.MALE, TRIVIAL_MALE_TYPE_NAME, BASIC_ATTRIBUTES);

	/**
	 * 女性默认的EntityType，无任何逻辑
	 */
	public static final String TRIVIAL_FEMALE_TYPE_NAME = "npc_female";

	public static final RegistryObject<EntityType<Npc>> TRIVIAL_FEMALE_TYPE = newType(Npc.class, Gender.FEMALE, TRIVIAL_FEMALE_TYPE_NAME, BASIC_ATTRIBUTES);

	static {
		Gender.MALE.setDefaultSkin(ResourceLocation.parse("sc:textures/entity/npc/male/ss.png"));
		Gender.FEMALE.setDefaultSkin(ResourceLocation.parse("sc:textures/entity/npc/female/ba/misono_mika.png"));
	}

	private boolean pushable;
	private boolean attackable;
	protected ResourceLocation skin;
	private double rmDistance;

	public Npc(EntityType<Npc> entityType, Gender gender, String name, ResourceLocation skin, Level level) {
		super(entityType, level);
		this.gender = gender;
		if (name != null) {
			this.setCustomName(Component.literal(name));
			this.setCustomNameVisible(true);
		}
		if (skin == null) {
			this.skin = gender.getDefaultSkin();
		}
	}

	public Npc(EntityType<Npc> entityType, Gender gender, ResourceLocation skin, Level level) {
		this(entityType, gender, null, skin, level);
	}

	public Npc(EntityType<Npc> entityType, Gender gender, String name, Level level) {
		this(entityType, gender, name, null, level);
	}

	public Npc(EntityType<Npc> entityType, Gender gender, Level level) {
		this(entityType, gender, null, null, level);
	}

	@Override
	public boolean isPushable() {
		return pushable;
	}

	/**
	 * 设置NPC是否可以被推动
	 * 
	 * @param pushable
	 * @return
	 */
	public Npc setPushable(boolean pushable) {
		this.pushable = pushable;
		return this;
	}

	/**
	 * 设置NPC的皮肤
	 * 
	 * @param skinName
	 */
	public Npc setSkin(ResourceLocation skin) {
		this.skin = skin;
		return this;
	}

	public Npc setSkin(String skinNamespace, String skinPath) {
		return setSkin(ResourceLocation.fromNamespaceAndPath(skinNamespace, skinPath));
	}

	public Npc setSkin(String loc) {
		return setSkin(ResourceLocation.parse(loc));
	}

	public ResourceLocation skin() {
		return skin;
	}

	/**
	 * 设置NPC远离消失的阈值距离。<br>
	 * 若小于等于0则永远不移除.<br>
	 * 
	 * @param distance
	 * @return
	 */
	public Npc setRemoveDistance(double distance) {
		this.rmDistance = distance;
		return this;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return rmDistance > 0 ? (distanceToClosestPlayer > rmDistance) : false;
	}

	@Override
	public boolean attackable() {
		return attackable;
	}

	/**
	 * 设置实体是否可以被攻击
	 * 
	 * @param attackable
	 * @return
	 */
	public Npc setAttackable(boolean attackable) {
		this.attackable = attackable;
		return this;
	}

	/**
	 * 让NPC朝向指定的方向
	 * 
	 * @param loc
	 * @return
	 */
	public Npc face(Vec3 loc) {
		this.lookAt(Anchor.EYES, loc);
		return this;
	}

	/**
	 * 让NPC朝向指定的实体
	 * 
	 * @param entity
	 * @return
	 */
	public Npc face(Entity entity) {
		return face(entity.getEyePosition());
	}

	/**
	 * 设置手持物品，该设置会替换NPC当前手持物品
	 * 
	 * @param item
	 * @return
	 */
	public Npc setHold(ItemStack item) {
		this.setItemInHand(InteractionHand.MAIN_HAND, item);
		return this;
	}

	public Npc chat(String msg) {
		// this.entity().chat(msg);
		return this;
	}

	/**
	 * 单独向一个玩家发送消息
	 * 
	 * @param player
	 * @param msg
	 * @return
	 */
	public Npc chatTo(Player player, String msg) {
		Component name = this.getCustomName();
		if (name == null) {
			// NPC如果没有名字则省去名字前缀，直接发送消息
			player.sendSystemMessage(Component.literal(msg));
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append('<');
			sb.append(name.getString());
			sb.append("> ");
			sb.append(msg);
			player.sendSystemMessage(Component.literal(sb.toString()));
		}
		return this;
	}

	ArrayList<InteractionHandler> interactionOps = new ArrayList<>();

	public void onInteract(InteractOperation op) {
		interactionOps.add(new InteractionHandler(op));
	}

	public void onInteract(InteractOperation interact, InteractOperation feedback_success) {
		interactionOps.add(new InteractionHandler(interact, feedback_success));
	}

	public void onInteract(InteractOperation interact, InteractOperation feedback_success, InteractOperation feedback_failure) {
		interactionOps.add(new InteractionHandler(interact, feedback_success, feedback_failure));
	}

	private void handleAttackEntity(Player player, Npc target, ItemStack items) {
		for (InteractionHandler op : interactionOps) {
			op.execute(Interaction.ATTACK, player, target, items);
		}
	}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		if (event.getTarget() instanceof Npc npc) {
			Player player = event.getEntity();
			npc.handleAttackEntity(player, npc, player.getMainHandItem());
		}
	}

	private void handleEntityInteract(Player player, Npc target, ItemStack items) {
		for (InteractionHandler op : interactionOps) {
			op.execute(Interaction.USE_ITEM, player, target, items);
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof Npc npc) {
			npc.handleEntityInteract(event.getEntity(), npc, event.getItemStack());
		}
	}
}
