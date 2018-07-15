package model;

import java.util.Objects;

public class Event implements Comparable {

    public enum EventType {
        PROJECT_PLANNING_END,
        RELEASE_PLANNING_END,
        SPRINT_PLANNING_END,
        DEVELOPMENT_END,
        TEST_END,
        PROJECT_END
    }

    private static int instanceCounter = 1;

    private final int id;

    private final EventType type;

    /**
     * Task associatd with the current event
     */
    private final Task task;

    /**
     * Start time of the event
     * Defined when the event enters the events queue
     */
    private final double startTime;

    /**
     * Constructor
     * @param type
     * @param task
     * @param startTime
     */
    Event(EventType type,
                 Task task,
                 double startTime) {
        this.id = instanceCounter++;
        this.type = type;
        this.task = task;
        this.startTime = startTime;
    }

    /**
     * Get the type of event
     *
     * @return the type of event
     * @see EventType for the supported types
     */
    public EventType getType() {
        return type;
    }

    /**
     * Get the start time of the event
     *
     * @return the start time of the event
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Get the task associated with the current event
     *
     * @return the task associated with the current event
     */
    public Task getTask() {
        return task;
    }

    /**
     * Compare the current event with another event
     * The events are ordered based on their start time,
     * meaning that the ones who started first have the highest priority
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        Event eventToCompare = (Event) o;

        if (startTime < eventToCompare.startTime) return -1;
        if (startTime > eventToCompare.startTime) return +1;
        return Integer.compare(id, eventToCompare.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                Double.compare(event.startTime, startTime) == 0 &&
                type == event.type &&
                Objects.equals(task, event.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, task, startTime);
    }
}
