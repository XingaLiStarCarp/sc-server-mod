package sc.server.api.entity.npc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

/**
 * 性别
 */
public enum Gender {
	MALE,
	FEMALE;

	public static final int NUM = Gender.values().length;

	/**
	 * 该性别的默认EntityType。<br>
	 * 即GENDER_ENTITY_TYPES储存的首个类型。<br>
	 * 
	 * @return
	 */
	public final EntityType<Npc> defaltEntityType() {
		RegistryObject<EntityType<Npc>> type = GENDER_ENTITY_TYPES[this.ordinal()].get(0);
		if (type == null) {// 该性别没有任何EntityType
			return null;
		} else {
			return type.get();
		}
	}

	// 每种性别都有哪些EntityType
	@SuppressWarnings("unchecked")
	private static final ArrayList<RegistryObject<EntityType<Npc>>>[] GENDER_ENTITY_TYPES = new ArrayList[NUM];

	static {
		for (int i = 0; i < GENDER_ENTITY_TYPES.length; ++i) {
			GENDER_ENTITY_TYPES[i] = new ArrayList<>();
		}
	}

	private static final ResourceLocation[] GENDER_DEFAULT_SKINS = new ResourceLocation[NUM];

	/**
	 * 设置默认皮肤
	 * 
	 * @param gender
	 * @param skin
	 * @return
	 */
	public final ResourceLocation setDefaultSkin(ResourceLocation skin) {
		int idx = ordinal();
		ResourceLocation prev = GENDER_DEFAULT_SKINS[idx];
		GENDER_DEFAULT_SKINS[idx] = skin;
		return prev;
	}

	public final ResourceLocation getDefaultSkin() {
		return GENDER_DEFAULT_SKINS[ordinal()];
	}

	/**
	 * 将一个EntityType注册为指定的性别。<br>
	 * 男性和女性的模型不同，将关乎渲染器的选择。<br>
	 * 
	 * @param type
	 * @return
	 */
	public final void registerType(RegistryObject<EntityType<Npc>> type) {
		GENDER_ENTITY_TYPES[ordinal()].add(type);
	}

	/**
	 * 获取该性别下所有的EntityType
	 * 
	 * @return
	 */
	public final List<RegistryObject<EntityType<Npc>>> getTypes() {
		return GENDER_ENTITY_TYPES[ordinal()];
	}
}