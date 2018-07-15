package model;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Resource {

    /**
     * Queue that stores the list of tasks of the current backlog
     */
    private Queue<Task> taskQueue;

    /**
     * List that stores the developers available to process the tasks of the task queue
     */
    private final List<Developer> developers;

    private double taskWaitingArea;

    /**
     * Number of delays of the current resource
     */
    private double delays;

    /**
     * Number of delays (in simTime) of the current resource
     */
    private double totalDelays;

    public Resource(List<Developer> assignedDevelopers) {
        this.developers = assignedDevelopers;
        this.taskQueue = new PriorityQueue<>();

        this.taskWaitingArea = 0;
        this.delays = 0;
        this.totalDelays = 0;
        this.taskWaitingArea = 0;
    }

    /**
     * Adds a new task to the task queue
     *
     * @param task task to be added
     */
    public void addTaskToQueue(Task task) {
        taskQueue.add(task);
    }

    /**
     * Returns true if the task queue of the current resource is empty
     *
     * @return true if the task queue of the current resource is empty
     */
    public boolean isTaskQueueEmpty() {
        return taskQueue.isEmpty();
    }

    /**
     * Get a available developer
     * If no developers are available null is returned
     *
     * @return available developer
     */
    public Developer getFreeDeveloper() {
        for (Developer developer : developers) {
            if (!developer.isBusy()) {
                developer.updateState(true);
                return developer;
            }
        }

        return null;
    }

    /**
     * Occupies the entire team at once
     * This is used for activities that use the entire team at one
     * (such as planning activities)
     */
    public void occupyTeam() {
        for (Developer developer : developers) {
            developer.updateState(true);
        }
    }

    /**
     * Frees the entire team at once
     * This is used for activities that use the entire team at one
     * (such as planning activities)
     */
    public void freeTeam() {
        for (Developer developer : developers) {
            developer.updateState(false);
        }
    }

    /**
     * Returns true if the entire team is free
     *
     * @return true if the entire team is free
     */
    public boolean isTeamFree() {
        for (Developer developer: developers) {
            if (developer.isBusy()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets next task on the waiting list
     *
     * @return next task
     */
    public Task getNextTask() {
        return taskQueue.remove();
    }

    /**
     * Adds the delay
     *
     * @param delay
     */
    public void addDelay(double delay) {
        delays++;
        totalDelays += delay;
    }

    /**
     * Get the total number of delays of the current resource
     *
     * @return the total number of delays of the current resource
     */
    public double getDelays() {
        return delays;
    }

    /**
     * Updates the task waiting area according to the time since last event
     *
     * @param timeLastEvent
     */
    public void updateTaskWaitingArea(double timeLastEvent) {
        taskWaitingArea += taskQueue.size() * timeLastEvent;
    }

    /**
     * Updates the resource usage area according to the time since last event
     *
     * @param timeLastEvent
     */
    public void updateUsageArea(double timeLastEvent) {
        for (Developer developer : developers) {
            developer.updateUsageArea(timeLastEvent);
        }
    }

    /**
     * Calculates the waiting time average per task
     * d(n) = totalDelays / delays
     *
     * @return waiting time average per task
     */
    public double calculateWaitingTimeAverage() {
        return (delays == 0) ? 0 : ((float) totalDelays / delays);
    }

    /**
     * Calculates the average number of tasks waiting to be developed for the given simTime
     * q(n) = taskWaitingArea / simTime
     *
     * @param simTime current simulation time
     * @return average number of tasks waiting
     */
    public double calculateWaitingAverage(double simTime) {
        return taskWaitingArea / simTime;
    }

    /**
     * Calculates developers usage rate for the given simTime
     * u(n) = usageArea / simTime / numberOfDevelopers
     *
     * @param simTime current simulation time
     * @return developers usage rate
     */
    public double calculateUsageRate(double simTime) {
        double usageArea = 0;
        for (Developer developer : developers) {
            usageArea += developer.getUsageArea();
        }
        return usageArea / simTime / developers.size();
    }
}
