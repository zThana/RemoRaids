package ca.landonjw.remoraids.internal.tasks;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * A task that may be executed at some point in the future, or repeated on an interval.
 *
 * @author landonjw
 */
public class Task {

	/** The consumer to be run when the time ticks remaining hits 0. */
	private Consumer<Task> consumer;

	/** The identifier for the task. This allows for identifying what the task is. */
	private String identifier;

	/** The number of ticks before the task should be run again. */
	private long interval;
	/** The number of times the task has been run. */
	private long currentIteration;
	/** The number of times the task will run before it's expired. */
	private long iterations;

	/** The number of ticks remaining before the task will be executed. */
	private long ticksRemaining;
	/** If the task is expired and ready to be removed. */
	private boolean expired;
	/** States whether or not the task is currently paused from executing */
	private boolean paused;

	/**
	 * Constructor for a task.
	 *
	 * @param consumer   the consumer to run
	 * @param delay      the delay before the task will be run
	 * @param interval   the time before the task will run after being executed
	 * @param iterations the number of times the task will be run
	 */
	private Task(String identifier, Consumer<Task> consumer, long delay, long interval, long iterations) {
		this.identifier = identifier;
		this.consumer = consumer;
		this.interval = interval;
		this.iterations = iterations;

		if (delay > 0) {
			ticksRemaining = delay;
		}
	}

	/**
	 * Gets the number of ticks remaining before the task will be run.
	 *
	 * @return the number of ticks remaining before the task will be run
	 */
	public long getTicksRemaining() {
		return ticksRemaining;
	}

	/**
	 * Checks if the task is expired and will never be run again.
	 *
	 * @return true if the task is expired, false if it isn't
	 */
	public boolean isExpired() {
		return expired;
	}

	/**
	 * Sets the task to be expired and makes it never run again.
	 */
	public void setExpired() {
		expired = true;
	}

	/**
	 * Pauses the task. This will prevent any further invokations of the task until it is resumed.
	 */
	public void pause() {
		this.paused = true;
	}

	/**
	 * Resumes the task.
	 */
	public void resume() {
		this.paused = false;
	}

	/**
	 * Decrements the number of ticks remaining and evaluates if the task should run.
	 */
	void tick() {
		if (!expired && !paused) {
			this.ticksRemaining = Math.max(0, --ticksRemaining);

			if (ticksRemaining == 0) {
				consumer.accept(this);
				currentIteration++;

				if (interval > 0 && (currentIteration < iterations || iterations == -1)) {
					ticksRemaining = interval;
				} else {
					expired = true;
				}
			}
		}
	}

	@Override
	public String toString() {
		if (this.identifier != null) {
			return "Task-" + this.identifier;
		} else {
			return super.toString();
		}
	}

	/**
	 * Gets a builder for a task.
	 *
	 * @return a task builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * A builder for a {@link Task}.
	 *
	 * @author landonjw
	 */
	public static class Builder {

		/** The consumer to be run. */
		private Consumer<Task> consumer;
		/** The number of ticks before the task will first be executed. */
		private long delay;
		/** The number of ticks before the task will be executed after it's already executed. */
		private long interval;
		/** The number of times the task will run. -1 to run indefinitely. */
		private long iterations = -1;

		private String identifier;

		public Builder identifier(String identifier) {
			this.identifier = identifier;
			return this;
		}

		/**
		 * Sets the consumer to be executed by the task.
		 *
		 * @param consumer the consumer to be executed by the task
		 * @return builder with consumer set
		 */
		public Builder execute(@Nonnull Consumer<Task> consumer) {
			this.consumer = consumer;
			return this;
		}

		/**
		 * Sets the runnable to be executed by the task.
		 *
		 * @param runnable the runnable to be executed by the task
		 * @return builder with runnable set
		 */
		public Builder execute(@Nonnull Runnable runnable) {
			this.consumer = (task) -> runnable.run();
			return this;
		}

		/**
		 * Sets the delay of the task.
		 *
		 * @param delay the number of ticks before the task will first be executed
		 * @return builder with delay set
		 * @throws IllegalArgumentException if delay is below 0
		 */
		public Builder delay(long delay) {
			if (delay < 0) {
				throw new IllegalArgumentException("Delay must not be below 0");
			}
			this.delay = delay;
			return this;
		}

		/**
		 * Sets the interval between task executions.
		 *
		 * @param interval the number of ticks before the task will execute after already executing
		 * @return builder with interval set
		 * @throws IllegalArgumentException if interval is below 0
		 */
		public Builder interval(long interval) {
			if (interval < 0) {
				throw new IllegalArgumentException("Interval must not be below 0");
			}
			this.interval = interval;
			return this;
		}

		/**
		 * Sets the number of times the task will run.
		 *
		 * @param iterations the number of times the task will run, -1 treated as indefinitely
		 * @return builder with number of iterations set
		 * @throws IllegalArgumentException if iterations is below -1
		 */
		public Builder iterations(long iterations) {
			if (iterations < -1) {
				throw new IllegalArgumentException("Iterations must not be below -1");
			}
			this.iterations = iterations;
			return this;
		}

		/**
		 * Builds the task using the properties of the task builder.
		 * This will add the task to {@link TaskTickListener}.
		 *
		 * @return a task with the task builder's properties
		 * @throws IllegalStateException if consumer is not set
		 */
		public Task build() {
			if (consumer == null) {
				throw new IllegalStateException("consumer must be set");
			}
			Task task = new Task(identifier, consumer, delay, interval, iterations);
			TaskTickListener.addTask(task);
			return task;
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Task task = (Task) o;
		return interval == task.interval && currentIteration == task.currentIteration && iterations == task.iterations && ticksRemaining == task.ticksRemaining && expired == task.expired && Objects.equals(consumer, task.consumer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(consumer, interval, currentIteration, iterations, ticksRemaining, expired);
	}

}
