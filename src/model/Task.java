package model;

import java.util.Objects;

public class Task implements Comparable {

    public enum TaskType {
        NEW_FEATURE(3),
        MAJOR_DEFECT(1),
        MINOR_DEFECT(2);

        public final int priority;

        TaskType(int taskTypePriority) {
            priority = taskTypePriority;
        }
    }

    public enum TaskStatus {
        DONE(4),
        TO_DO(3),
        IN_PROGRESS(2),
        TESTING(1);

        public final int priority;

        TaskStatus(int taskTypePriority) {
            priority = taskTypePriority;
        }
    }

    private static int instanceCounter = 1;

    private final int id;

    /**
     * Total size of the task measured in number of lines of code (LOC)
     */
    private final int size;

    /**
     * Type of the task
     * @see TaskType
     */
    private final TaskType type;

    /**
     * Priority assigned to the task during the planning activities
     */
    private int priority;

    /**
     * Coding effort of the current task
     */
    private float codingEffort;

    /**
     * Test effort of the current task
     */
    private float testEffort;

    /**
     * Number of defects injected into the system by the current task
     */
    private double injectedDefects;

    /**
     * Developer assigned to perform the current task
     */
    private Developer developer;

    /**
     * Status of the current task
     * Updated as the task goes through the development cycle
     *
     * @see TaskStatus
     */
    private TaskStatus status;

    /**
     * Arrival time of the task
     */
    private double arrivalTime;

    Task(TaskType type,
                float codingEffort,
                float testEffort,
                int size) {

        this.id = instanceCounter++;
        this.status = TaskStatus.TO_DO;
        this.priority = -1;

        this.type = type;
        this.codingEffort = codingEffort;
        this.testEffort = testEffort;
        this.size = size;
        this.injectedDefects = 0;
    }

    /**
     * Set the developer assigned to perform the current task
     * The coding effort is adjusted according to the skill level of the assigned developer
     *
     * @param assignedDeveloper the developer assigned to perform the current task
     */
    public void setDeveloper(Developer assignedDeveloper) {
        developer = assignedDeveloper;
        // update effort according to the assigned develop
        if (assignedDeveloper.getType() == Developer.DeveloperType.DEVELOPER) {
            codingEffort *= assignedDeveloper.getProductivity();
        } else if (assignedDeveloper.getType() == Developer.DeveloperType.TESTER) {
            testEffort *= assignedDeveloper.getProductivity();
        }
    }

    /**
     * Set the priority of the current task
     * A lower number represents a higher priority
     *
     * @param taskPriority the priority of the current task
     */
    public void setPriority(int taskPriority) {
        priority = taskPriority;
    }

    /**
     * Update the task arrival time every time the task is removed from
     * the task queue
     *
     * @param time the task arrival time
     */
    public void updateArrivalTime(double time) {
        arrivalTime = time;
    }

    /**
     * Update the task status according to the current development stage
     *
     * @param taskStatus the task status
     */
    public void updateStatus(TaskStatus taskStatus) {
        status = taskStatus;
    }

    /**
     * Update the number of injected defects
     * The number of injected defects is determined during the testing phase
     *
     * @param newDefects the number of injected defects
     */
    public void updateInjectedDefects(double newDefects) {
        injectedDefects += newDefects;
    }

    /**
     * Get the development status of the current task
     *
     * @return the development status of the current task
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Get the size in number of lines of code of the current task
     *
     * @return the size in number of lines of code
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Get the coding effort of the current task
     *
     * @return the coding effort of the current task
     */
    public double getCodingEffort() {
        return codingEffort;
    }

    /**
     * Get the test effort of the current task
     *
     * @return the test effort of the current task
     */
    public double getTestEffort() {
        return testEffort;
    }

    /**
     * Get the total task effort of the current task
     *
     * @return the total task effort of the current task
     */
    public double getEffort() {
        return codingEffort + testEffort;
    }

    /**
     * Get the arrival time of the current task
     *
     * @return the arrival time of the current task
     */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Get the size in number of lines of code of the current task
     *
     * @return the size in number of lines of code of the current task
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the developer assigned to the current task
     *
     * @return the developer assigned to the current task
     */
    public Developer getDeveloper() {
        return developer;
    }

    /**
     * Get the number of injected defects
     * The number of injected defects is determined during the testing phase
     *
     * @return the number of injected defects
     */
    public double getInjectedDefects() {
        return injectedDefects;
    }

    @Override
    public int compareTo(Object o) {
        Task taskToCompare = (Task) o;

        if (status.priority < taskToCompare.status.priority) return -1;
        if (status.priority > taskToCompare.status.priority) return 1;

        if (type.priority < taskToCompare.type.priority) return -1;
        if (type.priority > taskToCompare.type.priority) return 1;

        if (priority > taskToCompare.priority) return 1;
        else if (priority < taskToCompare.priority) return -1;

        return Integer.compare(id, taskToCompare.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                size == task.size &&
                priority == task.priority &&
                Float.compare(task.codingEffort, codingEffort) == 0 &&
                Float.compare(task.testEffort, testEffort) == 0 &&
                Double.compare(task.injectedDefects, injectedDefects) == 0 &&
                Double.compare(task.arrivalTime, arrivalTime) == 0 &&
                type == task.type &&
                Objects.equals(developer, task.developer) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, size, type, priority, codingEffort, testEffort,
                injectedDefects, developer, status, arrivalTime);
    }

    @Override
    public String toString() {
        return "id: " + id + ", type: " + type + ", codingEffort: " + codingEffort + ", testEffort: " + testEffort
                + ", size: " + size
                + ", priority: " + priority + ", status: " + status + ", defects: " + injectedDefects;
    }
}
