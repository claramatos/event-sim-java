package model;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import utils.Constants;

import java.util.PriorityQueue;
import java.util.Queue;

public class TaskCreator {

    private final TriangularDistribution taskSizeDistribution;
    private final TriangularDistribution newFeatureEffortDistribution;
    private final TriangularDistribution minorDefectEffortDistribution;
    private final TriangularDistribution majorDefectEffortDistribution;

    private final EnumeratedDistribution newFeaturePriorityDistribution;
    private final EnumeratedDistribution minorDefectPriorityDistribution;
    private final EnumeratedDistribution majorDefectPriorityDistribution;

    private final EnumeratedDistribution typeDefectDistribution;

    private final TriangularDistribution defectInjectionDistribution;

    private final boolean useTdd;

    public TaskCreator(boolean useTdd) {
        this.useTdd = useTdd;

        taskSizeDistribution = new TriangularDistribution(Constants.TASK_SIZE_LOWER_LIMIT,
                Constants.TASK_SIZE_MODE, Constants.TASK_SIZE_UPPER_LIMIT);

        newFeatureEffortDistribution = new TriangularDistribution(Constants.NEW_FEATURE_EFFORT_LOWER_LIMIT,
                Constants.NEW_FEATURE_EFFORT_MODE, Constants.NEW_FEATURE_EFFORT_UPPER_LIMIT);

        minorDefectEffortDistribution = new TriangularDistribution(Constants.MINOR_DEFECT_EFFORT_LOWER_LIMIT,
                Constants.MINOR_DEFECT_EFFORT_MODE, Constants.MINOR_DEFECT_EFFORT_UPPER_LIMIT);

        majorDefectEffortDistribution = new TriangularDistribution(Constants.MAJOR_DEFECT_EFFORT_LOWER_LIMIT,
                Constants.MAJOR_DEFECT_EFFORT_MODE, Constants.MAJOR_DEFECT_EFFORT_UPPER_LIMIT);

        newFeaturePriorityDistribution = new EnumeratedDistribution(Constants.NEW_FEATURE_PRIORITIES);
        minorDefectPriorityDistribution = new EnumeratedDistribution(Constants.MINOR_DEFECTS_PRIORITIES);
        majorDefectPriorityDistribution = new EnumeratedDistribution(Constants.MAJOR_DEFECTS_PRIORITIES);

        typeDefectDistribution = new EnumeratedDistribution(Constants.TYPE_OF_DEFECT);

        defectInjectionDistribution = new TriangularDistribution(Constants.INJECTED_DEFECT_LOWER_LIMIT,
                Constants.INJECTED_DEFECT_MODE, Constants.INJECTED_DEFECT_UPPER_LIMIT);

        if (Constants.SEED != -1) {
            taskSizeDistribution.reseedRandomGenerator(Constants.SEED);

            newFeatureEffortDistribution.reseedRandomGenerator(Constants.SEED + 1);
            minorDefectEffortDistribution.reseedRandomGenerator(Constants.SEED + 2);
            majorDefectEffortDistribution.reseedRandomGenerator(Constants.SEED + 3);

            newFeaturePriorityDistribution.reseedRandomGenerator(Constants.SEED + 4);
            minorDefectPriorityDistribution.reseedRandomGenerator(Constants.SEED + 5);
            majorDefectPriorityDistribution.reseedRandomGenerator(Constants.SEED + 6);

            typeDefectDistribution.reseedRandomGenerator(Constants.SEED + 7);

            defectInjectionDistribution.reseedRandomGenerator(Constants.SEED + 8);
        }
    }

    /**
     * Creates a new task according to the task type
     *
     * @param taskType
     * @return
     */
    public Task createTask(Task.TaskType taskType) {
        int curTaskSize = 0;
        float curEffort = 0f;

        switch (taskType) {
            case NEW_FEATURE:
                curTaskSize = getTaskSize(taskSizeDistribution);
                curEffort = getTaskEffort(newFeatureEffortDistribution);
                break;
            case MAJOR_DEFECT:
                curEffort = getTaskEffort(majorDefectEffortDistribution);
                break;
            case MINOR_DEFECT:
                curEffort = getTaskEffort(minorDefectEffortDistribution);
                break;
        }

        float codingEffort = curEffort * (1 - Constants.TESTING_DEFECT_EFFORT_PERC);
        float testingEffort = curEffort * Constants.TESTING_DEFECT_EFFORT_PERC;

        if (useTdd) {
            codingEffort *= Constants.TDD_CODING_EFFORT;
            testingEffort *= Constants.TDD_TESTING_EFFORT;
        } else {
            codingEffort *= Constants.NONTDD_CODING_EFFORT;
            testingEffort *= Constants.NONTDD_TESTING_EFFORT;
        }

        return new Task(taskType, codingEffort, testingEffort, curTaskSize);
    }

    /**
     * Update the priority of the provided queue of tasks
     *
     * @param backlog
     * @return the updated queue
     */
    public PriorityQueue updatePriorityOfTasks(Queue<Task> backlog) {

        // set the priority for the tasks
        for (Task task : backlog) {
            if (task.getStatus() == Task.TaskStatus.TO_DO) {
                switch (task.getType()) {
                    case NEW_FEATURE:
                        task.setPriority(getTaskPriority(newFeaturePriorityDistribution));
                        break;
                    case MINOR_DEFECT:
                        task.setPriority(getTaskPriority(minorDefectPriorityDistribution));
                        break;
                    case MAJOR_DEFECT:
                        task.setPriority(getTaskPriority(majorDefectPriorityDistribution));
                        break;
                }
            }
        }

        // reorder priority queue based on the new assigned priorities
        return new PriorityQueue<>(backlog);
    }

    /**
     * Detect the number of defects of the provided task
     *
     * @param testedTask
     */
    public void detectDefects(Task testedTask) {

        float numberOfDefects;
        // only features can produce defects
        if (testedTask.getType() == Task.TaskType.NEW_FEATURE) {
            numberOfDefects = (testedTask.getSize() * getInjectedDefects()) / 1000f;
            // update number of detected defects according to developer skill level
            numberOfDefects *= testedTask.getDeveloper().getDefectInjectionRate();
        } else {
            numberOfDefects = 0;

        }

        if (useTdd) {
            numberOfDefects *= Constants.TDD_INJECTED_DEFECTS;
        } else {
            numberOfDefects *= Constants.NONTDD_INJECTED_DEFECTS;
        }

        testedTask.updateInjectedDefects(numberOfDefects);
    }

    /**
     * Get the task size based on the provided triangular distribution
     *
     * @param distribution
     * @return the task size based on the provided triangular distribution
     */
    private int getTaskSize(TriangularDistribution distribution) {
        return Math.toIntExact(Math.round(distribution.sample()));
    }

    /**
     * Get the task effort based on the provided triangular distribution
     *
     * @param distribution
     * @return the task effort based on the provided triangular distribution
     */
    private float getTaskEffort(TriangularDistribution distribution) {
        return (float) (Math.round(distribution.sample() * 100.0) / 100.0);
    }

    /**
     * Get the task priority based on the provided triangular distribution
     *
     * @param distribution
     * @return the task priority based on the provided triangular distribution
     */
    private int getTaskPriority(EnumeratedDistribution distribution) {
        return (int) distribution.sample();
    }

    /**
     * Get the defect type based on the defect injection distribution
     *
     * @return the defect type
     */
    public Task.TaskType getDefectType() {
        return (Task.TaskType) typeDefectDistribution.sample();
    }

    /**
     * Get the number of injected defects based on the defect injection distribution
     *
     * @return the number of injected defects
     */
    private int getInjectedDefects() {
        return Math.toIntExact(Math.round(defectInjectionDistribution.sample()));
    }

}
