package sc.server.api.entity.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum Interaction {
	ATTACK, USE_ITEM;

	@FunctionalInterface
	public static interface InteractOperation {
		/**
		 * @param action
		 * @param player
		 * @param npc
		 * @param items
		 * @return 本交互是否完成
		 */
		public boolean onInteract(Interaction action, Player player, Npc npc, ItemStack items);
	}

	public static final InteractOperation NONE = (action, player, npc, items) -> {
		return true;
	};

	/**
	 * 交互-反馈处理
	 */
	public static class InteractionHandler {
		private InteractOperation interact;
		private InteractOperation feedback_success;
		private InteractOperation feedback_failure;

		public InteractionHandler(InteractOperation interact, InteractOperation feedback_success,
				InteractOperation feedback_failure) {
			this.interact = interact;
			this.feedback_success = feedback_success;
			this.feedback_failure = feedback_failure;
		}

		public InteractionHandler(InteractOperation interact, InteractOperation feedback_success) {
			this(interact, feedback_success, NONE);
		}

		public InteractionHandler(InteractOperation interact) {
			this(interact, NONE, NONE);
		}

		/**
		 * 先执行interact，如果执行成功则继续执行feedback_success，否则执行
		 * 
		 * @param action
		 * @param player
		 * @param npc
		 * @param items
		 */
		public void execute(Interaction action, Player player, Npc npc, ItemStack items) {
			if (interact.onInteract(action, player, npc, items))
				feedback_success.onInteract(action, player, npc, items);
			else
				feedback_failure.onInteract(action, player, npc, items);
		}
	}

	/**
	 * 组合同时执行的多个操作，并且依次执行。<br>
	 * 任意一个执行失败则立即中断后续操作并返回false
	 * 
	 * @param ops
	 * @return
	 */
	public static InteractOperation isochronic(InteractOperation... ops) {
		return (action, player, npc, items) -> {
			for (InteractOperation op : ops) {
				if (!op.onInteract(action, player, npc, items))
					return false;
			}
			return true;
		};
	}

	/**
	 * 组合同时执行的多个操作，并且依次执行。<br>
	 * 不论是否执行失败都将执行全部操作，但只要有一个失败就返回false
	 * 
	 * @param ops
	 * @return
	 */
	public static InteractOperation isochronicNoInterrupt(InteractOperation... ops) {
		return (action, player, npc, items) -> {
			boolean success = true;
			for (InteractOperation op : ops) {
				if (!op.onInteract(action, player, npc, items))
					success = false;
			}
			return success;
		};
	}

	/**
	 * 组合同时执行多个操作，并且依次执行。<br>
	 * 不论是否执行失败都将执行全部操作，并返回true
	 * 
	 * @param ops
	 * @return
	 */
	public static InteractOperation isochronicAlwaysSuccess(InteractOperation... ops) {
		return (action, player, npc, items) -> {
			for (InteractOperation op : ops) {
				op.onInteract(action, player, npc, items);
			}
			return true;
		};
	}

	/**
	 * 组合不定时的执行的多个操作，每次操作都会记录结果，当所有操作全部执行完毕时才会返回true。<br>
	 * 执行结果已经为true的操作，不会再次执行。<br>
	 * 当全部操作执行都返回true时，list()操作完成并返回true，同时重置状态。
	 * 
	 * @param everySuccess 每次执行成功后的反馈
	 * @param ops
	 * @return
	 */
	public static InteractOperation listEverySuccess(InteractOperation everySuccess, InteractOperation... ops) {
		return new InteractOperation() {
			boolean[] successState = new boolean[ops.length];// 记录多个操作的独立执行结果

			public boolean onInteract(Interaction action, Player player, Npc npc, ItemStack items) {
				boolean thisPassSuccess = true;// 本次是否所有操作都执行成功
				for (int i = 0; i < successState.length; ++i) {
					if (!successState[i]) {
						if (ops[i].onInteract(action, player, npc, items)) {
							successState[i] = true;
							everySuccess.onInteract(action, player, npc, items);
						} else
							thisPassSuccess = false;
					}
				}
				if (thisPassSuccess) {
					// 列表里的全部操作执行完成
					Arrays.fill(successState, false);// 重置各个操作的执行结果
					return true;
				} else {
					return false;
				}
			}
		};
	}

	public static InteractOperation list(InteractOperation... ops) {
		return listEverySuccess(NONE, ops);
	}

	public static InteractOperation receiveItemFromPlayerMainHand(Item type, int count, Interaction... actions) {
		return (action, player, npc, items) -> {
			if ((actions.length == 0 && action == Interaction.USE_ITEM) || List.of(actions).contains(action)) {
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
			} else {
				return false;
			}
		};
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

	public static InteractOperation receiveItemFromPlayerInventory(Item type, int count, Interaction... actions) {
		return (action, player, npc, items) -> {
			if ((actions.length == 0 && action == Interaction.USE_ITEM) || List.of(actions).contains(action)) {
				if (count <= 0)
					return true;
				int requiredCount = count;
				Collection<? extends ItemStack> itemstacks = filterInventory(player.getInventory(), items);
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
			} else {
				return false;
			}
		};
	}

	public static InteractOperation giveItemToPlayer(ItemStack give_items, Interaction... actions) {
		return (action, player, npc, items) -> {
			if (actions.length == 0 || List.of(actions).contains(action)) {
				player.getInventory().add(give_items);
				return true;
			} else {
				return false;
			}
		};
	}

	public static InteractOperation chat(String msg, Interaction... actions) {
		return (action, player, npc, items) -> {
			if (actions.length == 0 || List.of(actions).contains(action)) {
				npc.chat(msg);
				return true;
			} else {
				return false;
			}
		};
	}

	public static InteractOperation chatToPlayer(String msg, Interaction... actions) {
		return (action, player, npc, items) -> {
			if (actions.length == 0 || List.of(actions).contains(action)) {
				npc.chatTo(player, msg);
				return true;
			} else {
				return false;
			}
		};
	}

	public static InteractOperation sengMsgToPlayer(String msg, Interaction... actions) {
		return (action, player, npc, items) -> {
			if (actions.length == 0 || List.of(actions).contains(action)) {
				player.sendSystemMessage(Component.literal(msg));
				return true;
			} else {
				return false;
			}
		};

	}

	public static InteractOperation moveTo(Vec3 loc, Interaction... actions) {
		return (action, player, npc, items) -> {
			if (actions.length == 0 || List.of(actions).contains(action)) {
				npc.moveTo(loc);
				return true;
			} else {
				return false;
			}
		};
	}
}
