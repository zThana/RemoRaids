package ca.landonjw.remoraids.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Listens for ticks and invokes the ticking mechanism in tasks.
 *
 * @author landonjw
 */
public class TaskTickListener {

	/** List of active tasks to be ticked by the listener. */
	private static List<Task> tasks = new ArrayList<>();

	/**
	 * Invokes {@link Task#tick()} on each active task and checks if any should be removed.
	 *
	 * @param event the event called when the server ticks
	 */
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (Task task : new ArrayList<>(tasks)) {
				task.tick();
				if (task.isExpired()) {
					tasks.remove(task);
				}
			}
		}
	}

	/**
	 * Adds a task to be ticked by the listener.
	 *
	 * @param task the task to add
	 */
	static void addTask(@Nonnull Task task) {
		tasks.add(task);
	}

}
