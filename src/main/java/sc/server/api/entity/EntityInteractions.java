package sc.server.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sc.server.api.registry.Registers;

/**
 * 交互相关动作。
 */
public class EntityInteractions {
	public static class CombinedTask {
		Consumer<Object[]> onSingleSuccess;
		Consumer<Object[]> onAnyFailed;
		Consumer<Object[]> onSuccess;
		Function<Object[], Boolean>[] conds;
		boolean[] successState;// 记录多个操作的独立执行结果

		/**
		 * 组合不定时的执行的多个操作，每次操作都会记录结果，当所有操作全部执行完毕时才会返回true。<br>
		 * 执行结果已经为true的操作，不会再次执行。<br>
		 * 当全部操作执行都返回true时，整个组合操作完成并返回true，同时重置各个独立操作的完成状态。
		 * 
		 * @param onSingleSuccess 单次交互成功的动作
		 * @param onAnyFailed     没有全部交互成功的动作
		 * @param onSuccess       所有交互都成功的动作
		 * @param conds
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public CombinedTask(Consumer<Object[]> onSingleSuccess, Consumer<Object[]> onAnyFailed, Consumer<Object[]> onSuccess, Function<Object[], Boolean>... conds) {
			this.onSingleSuccess = onSingleSuccess;
			this.onAnyFailed = onAnyFailed;
			this.onSuccess = onSuccess;
			this.conds = conds;
			this.successState = new boolean[conds.length];
		}

		public boolean execute(Object... args) {
			boolean thisPassSuccess = true;// 本次是否所有操作都执行成功
			for (int i = 0; i < successState.length; ++i) {
				if (!successState[i]) {
					if (conds[i].apply(args)) {
						successState[i] = true;
						if (onSingleSuccess != null)
							onSingleSuccess.accept(args);
					} else
						thisPassSuccess = false;
				}
			}
			if (thisPassSuccess) {
				// 列表里的全部操作执行完成
				Arrays.fill(successState, false);// 重置各个操作的执行结果
				if (onSuccess != null)
					onSuccess.accept(args);
				return true;
			} else {
				if (onAnyFailed != null)
					onAnyFailed.accept(args);
				return false;
			}
		}
	}

	public static boolean receiveItemFromPlayerMainHand(Player player, Item type, int count) {
		ItemStack items = player.getMainHandItem();
		if (items.getItem() == type) {
			int currentCount = items.getCount();
			if (currentCount < count) {
				return false;
			} else {
				items.setCount(currentCount - count);
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean receiveItemFromPlayerMainHand(Player player, String type, int count) {
		return receiveItemFromPlayerMainHand(player, Registers.item(type), count);
	}

	/**
	 * 查找玩家背包的物品
	 * 
	 * @param inv
	 * @param item
	 * @return
	 */
	public static final ArrayList<ItemStack> filterInventory(Inventory inv, ItemStack item) {
		ArrayList<ItemStack> result = new ArrayList<>();
		for (ItemStack current : inv.items) {
			if (current.getItem() == item.getItem() && current.getDamageValue() == item.getDamageValue()) {
				result.add(current);
			}
		}
		return result;
	}

	public static final ArrayList<ItemStack> filterInventory(Inventory inv, Item item) {
		ArrayList<ItemStack> result = new ArrayList<>();
		for (ItemStack current : inv.items) {
			if (current.getItem() == item) {
				result.add(current);
			}
		}
		return result;
	}

	public static boolean receiveItemFromPlayerInventory(Player player, Item type, int count) {
		if (count <= 0)
			return true;
		int requiredCount = count;
		Collection<? extends ItemStack> itemstacks = filterInventory(player.getInventory(), type);
		ArrayList<ItemStack> toBeCleared = new ArrayList<>();
		for (ItemStack itemstack : itemstacks) {
			if (requiredCount > 0) {
				int currentCount = itemstack.getCount();
				if (currentCount < requiredCount) {
					toBeCleared.add(itemstack);
					requiredCount -= currentCount;
				} else {
					itemstack.setCount(currentCount - requiredCount);
					for (ItemStack c : toBeCleared) {
						c.setCount(0);
					}
					return true;
				}
			}
		}
		return false;// 全部遍历完成，数量还是不够则返回false
	}

	public static boolean receiveItemFromPlayerInventory(Player player, String type, int count) {
		return receiveItemFromPlayerInventory(player, Registers.item(type), count);
	}

	public static void giveItemToPlayer(Player player, ItemStack give_items) {
		player.getInventory().add(give_items);
	}

	public static void sengMsgToPlayer(Player player, String msg) {
		player.sendSystemMessage(Component.literal(msg));
	}

	public static void holdItem(LivingEntity entity, ItemStack item) {
		entity.setItemInHand(InteractionHand.MAIN_HAND, item);
	}

	public static void clearHoldItem(LivingEntity entity, ItemStack item) {
		holdItem(entity, ItemStack.EMPTY);
	}

	/**
	 * 从玩家主手获取物品，并手持获取到的物品
	 * 
	 * @param type
	 * @param count
	 * @return
	 * @return
	 */
	public static boolean receiveItemFromPlayerMainHandAndHold(Player player, LivingEntity entity, Item type, int count) {
		ItemStack items = player.getMainHandItem();
		if (items.getItem() == type) {
			int currentCount = items.getCount();
			if (currentCount < count) {
				return false;
			} else {
				items.setCount(currentCount - count);
				ItemStack hold = items.copy();// 手持物品，需要拷贝
				hold.setCount(count);
				holdItem(entity, hold);
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean receiveItemFromPlayerMainHandAndHold(Player player, LivingEntity entity, String type, int count) {
		return receiveItemFromPlayerMainHandAndHold(player, entity, Registers.item(type), count);
	}
}
