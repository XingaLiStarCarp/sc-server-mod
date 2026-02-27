package sc.server.api.entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EntityData {

	private static MethodHandle createDataItem;

	static {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(SynchedEntityData.class, MethodHandles.lookup());
			createDataItem = lookup.findVirtual(SynchedEntityData.class,
					"createDataItem",
					MethodType.methodType(void.class, EntityDataAccessor.class, Object.class));
		} catch (IllegalAccessException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 安全地初始化字段。如果字段已经初始化则忽略该操作。
	 * 
	 * @param <T>
	 * @param synchedEntityData
	 * @param accessor
	 * @param value
	 */
	public static <T> void define(SynchedEntityData synchedEntityData, EntityDataAccessor<T> accessor, T value) {
		int i = accessor.getId();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
		} else if (synchedEntityData.hasItem(accessor)) {
			return;// 如果已经初始化，则不再重复初始化
		} else if (EntityDataSerializers.getSerializedId(accessor.getSerializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + accessor.getSerializer() + " for " + i + "!");
		} else {
			try {
				createDataItem.invoke(synchedEntityData, accessor, value);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	static {

	}

	private static final HashMap<Class<? extends Entity>, ArrayList<EntityDataAccessor<?>>> DATA_ENTRIES = new HashMap<>();
	private static final HashMap<EntityDataAccessor<?>, Object> DEFAULT_VALUES = new HashMap<>();

	/**
	 * 获取指定实体类定义的数据
	 * 
	 * @param entityClazz
	 * @return
	 */
	public static ArrayList<EntityDataAccessor<?>> dataEntries(Class<? extends Entity> entityClazz) {
		return DATA_ENTRIES.computeIfAbsent(entityClazz, (k) -> new ArrayList<>());
	}

	public static void setDefaultValue(EntityDataAccessor<?> acc, Object value) {
		DEFAULT_VALUES.put(acc, value);
	}

	public static Object getDefaultValue(EntityDataAccessor<?> acc) {
		return DEFAULT_VALUES.get(acc);
	}

	/**
	 * 为指定类型的实体创建一个新字段
	 * 
	 * @param <_T>
	 * @param entityClazz  实体类
	 * @param dataType     字段数据类型
	 * @param defaultValue 字段默认值
	 * @return
	 */
	public static <_T> EntityDataAccessor<_T> define(Class<? extends Entity> entityClazz, EntityDataSerializer<_T> dataType, _T defaultValue) {
		EntityDataAccessor<_T> acc = SynchedEntityData.defineId(entityClazz, dataType);
		dataEntries(entityClazz).add(acc);
		setDefaultValue(acc, Objects.requireNonNull(defaultValue));
		return acc;
	}

	/**
	 * 指定的实体类是否有字段。<br>
	 * 只有通过本类的define()方法加入的字段才能识别。<br>
	 * 
	 * @param entityClazz
	 * @return
	 */
	public static boolean hasEntries(Class<? extends Entity> entityClazz) {
		return DATA_ENTRIES.containsKey(entityClazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void reset(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		ArrayList<EntityDataAccessor<?>> entries = dataEntries(entityClazz);
		for (EntityDataAccessor<?> acc : entries) {
			entityData.set((EntityDataAccessor) acc, getDefaultValue(acc));
		}
	}

	public static void reset(Entity entity) {
		reset(entity.getClass(), entity.getEntityData());
	}

	/**
	 * 运行时初始化实体数据。<br>
	 * 仅初始化实体自身类的数据，父类的数据不会初始化。<br>
	 * 
	 * @param entityClazz
	 * @param entityData
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void define(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		if (hasEntries(entityClazz)) {
			ArrayList<EntityDataAccessor<?>> entries = dataEntries(entityClazz);
			for (EntityDataAccessor<?> acc : entries) {
				define(entityData, (EntityDataAccessor) acc, getDefaultValue(acc));
			}
		}
	}

	public static void define(Entity entity) {
		define(entity.getClass(), entity.getEntityData());
	}

	/**
	 * 递归地初始化实体类自身及其父类的字段。
	 * 
	 * @param entityClazz
	 * @param entityData
	 */
	@SuppressWarnings("unchecked")
	public static void defineAll(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		while (entityClazz != Entity.class) {
			define(entityClazz, entityData);
			entityClazz = (Class<? extends Entity>) entityClazz.getSuperclass();
		}
	}

	public static void defineAll(Entity entity) {
		defineAll(entity.getClass(), entity.getEntityData());
	}

	public static String string(Component component) {
		return Component.Serializer.toJson(component);
	}

	public static MutableComponent component(String component) {
		return component == null ? Component.empty() : Component.Serializer.fromJson(component);
	}

	public static String string(ResourceLocation loc) {
		return loc.toString();
	}

	public static ResourceLocation resourceLocation(String loc) {
		return ResourceLocation.parse(loc);
	}

	public static void loadString(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<String> accessor) {
		String data = null;
		if (!compound.contains(tag, Tag.TAG_STRING)) {
			data = (String) getDefaultValue(accessor);
		} else {
			data = compound.getString(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadComponent(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Component> accessor) {
		Component data = null;
		if (!compound.contains(tag, Tag.TAG_STRING)) {
			data = (Component) getDefaultValue(accessor);
		} else {
			data = component(compound.getString(tag));
		}
		entityData.set(accessor, data);
	}

	public static void loadBool(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Boolean> accessor) {
		boolean data = false;
		if (!compound.contains(tag, Tag.TAG_BYTE)) {
			data = (boolean) getDefaultValue(accessor);
		} else {
			data = compound.getBoolean(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadByte(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Byte> accessor) {
		byte data = 0;
		if (!compound.contains(tag, Tag.TAG_BYTE)) {
			data = (byte) getDefaultValue(accessor);
		} else {
			data = compound.getByte(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadShort(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Short> accessor) {
		short data = 0;
		if (!compound.contains(tag, Tag.TAG_SHORT)) {
			data = (short) getDefaultValue(accessor);
		} else {
			data = compound.getShort(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadInt(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Integer> accessor) {
		int data = 0;
		if (!compound.contains(tag, Tag.TAG_INT)) {
			data = (int) getDefaultValue(accessor);
		} else {
			data = compound.getInt(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadLong(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Long> accessor) {
		long data = 0;
		if (!compound.contains(tag, Tag.TAG_LONG)) {
			data = (long) getDefaultValue(accessor);
		} else {
			data = compound.getLong(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadFloat(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Float> accessor) {
		float data = 0;
		if (!compound.contains(tag, Tag.TAG_FLOAT)) {
			data = (int) getDefaultValue(accessor);
		} else {
			data = compound.getFloat(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadDouble(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Double> accessor) {
		double data = 0;
		if (!compound.contains(tag, Tag.TAG_DOUBLE)) {
			data = (double) getDefaultValue(accessor);
		} else {
			data = compound.getDouble(tag);
		}
		entityData.set(accessor, data);
	}
}
