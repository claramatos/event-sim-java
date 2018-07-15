package model;

import java.util.PriorityQueue;
import java.util.Queue;

public class Backlog {

    /**
     * Effort of the current backlog
     */
    private double backlogEffort;

    /**
     * Queue that stores the list of tasks of the current backlog
     */
    private Queue<Task> taskQueue;

    /**
     * Constructor
     */
    public Backlog() {
        taskQueue = new PriorityQueue<>();
        backlogEffort = 0;
    }

    /**
     * Adds a new task to the backlog
     *
     * @param task task to be added
     */
    public void addTaskToBacklog(Task task) {
        taskQueue.add(task);
        backlogEffort += task.getEffort();
    }

    /**
     * Get the effort of the current backlog
     *
     * @return the effort of the current backlog
     */
    public double getBacklogEffort() {
        return backlogEffort;
    }

    /**
     * Returns true if no tasks are left in the task queue
     *
     * @return true if no tasks are left in the task queue
     */
    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    /**
     * Returns the next task on the task queue according to the task priority
     *
     * @return the next task on the task queue according to the task priority
     */
    public Task getNextTask() {
        Task taskToRemove = taskQueue.remove();
        backlogEffort -= taskToRemove.getEffort();
        return taskToRemove;
    }

    /**
     * Get the size of the backlog
     *
     * @return the size of the backlog
     */
    public int size() {
        return taskQueue.size();
    }

    /**
     * Get the task queue that composes the current backlog
     *
     * @return the task queue that composes the current backlog
     */
    public Queue<Task> getBacklog() {
        return taskQueue;
    }

    /**
     * Get the total number of injected defects on the tasks that
     * are part of the task queue
     *
     * @return the total number of injected defects
     */
    public int getInjectedDefects() {
        float curInjectedDefects = 0;
        for (Task task : taskQueue) {
            if (task.getStatus() == Task.TaskStatus.TESTING) {
                curInjectedDefects += task.getInjectedDefects();
                task.updateStatus(Task.TaskStatus.DONE);
            }
        }

        return (int) Math.ceil(curInjectedDefects);
    }

    /**
     * Updates the priority of the task on the taskQueue
     */
    public void updateTaskQueue(PriorityQueue<Task> newTaskQueue) {
        taskQueue = new PriorityQueue<>(newTaskQueue);
    }

    private int getNumberOfTasksByType(Task.TaskType taskType) {
        int numberOfTasks = 0;

        for (Task task : taskQueue) {
            if (task.getType() == taskType) {
                numberOfTasks++;
            }
        }

        return numberOfTasks;
    }

    /**
     * Get the total number of minor defects that
     * are part of the task queue
     *
     * @return the total number of minor defects
     */
    public int getNumberOfMinorDefects() {
        return getNumberOfTasksByType(Task.TaskType.MINOR_DEFECT);
    }

    /**
     * Get the total number of major defects that
     * are part of the task queue
     *
     * @return the total number of major defects
     */
    public int getNumberOfMajorDefects() {
        return getNumberOfTasksByType(Task.TaskType.MAJOR_DEFECT);
    }

    /**
     * Get the total number of new features that
     * are part of the task queue
     *
     * @return the total number of new features
     */
    public int getNumberOfNewFeatures() {
        return getNumberOfTasksByType(Task.TaskType.NEW_FEATURE);
    }

    /**
     * Get the number of lines of code (LOC) added
     * by the tasks that are part of the task queue
     *
     * @return the number of lines of code (LOC) added
     */
    public int getNumberOfLocs() {
        int numberOfLocs = 0;
        for (Task task : taskQueue) {
            numberOfLocs += task.getSize();
        }
        return numberOfLocs;
    }

    /**
     * Get the number of defects injected by 1000 LOCS
     *
     * @return the number of defects injected by 1000 LOCS
     */
    public float getNumberOfDefectsPerKLoc() {
        int defects = getNumberOfMajorDefects() + getNumberOfMinorDefects();
        return defects / (getNumberOfLocs() / 1000f);
    }
}
