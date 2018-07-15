package utils;

import model.Task;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static boolean USE_TDD = false;

    public static boolean SET_MAX_PROJECT_DURATION = false;

    /**
     * Seed to generate the probability distributions
     */
    public static long SEED = 12345;

    public static int NUMBER_OF_SENIOR_DEVELOPERS = 4; //1;
    public static int NUMBER_OF_MID_LEVEL_DEVELOPERS = 4; //2;
    public static int NUMBER_OF_JUNIOR_DEVELOPERS = 5; //3;

    public static int NUMBER_OF_TESTERS = 8; //3;

    public static int NUMBER_RELEASES_PER_PROJECT = 3;
    public static int NUMBER_SPRINTS_PER_RELEASE = 3;

    public static int MAX_PROJECT_EFFORT = 16200;
    public static int MAX_RELEASE_EFFORT = MAX_PROJECT_EFFORT / NUMBER_RELEASES_PER_PROJECT;
    public static int MAX_SPRINT_EFFORT = MAX_RELEASE_EFFORT / NUMBER_SPRINTS_PER_RELEASE;

    public static float PROJECT_DEFECT_EFFORT_PERC = 0.5f;
    public static float TESTING_DEFECT_EFFORT_PERC = 0.3f;

    public static float DAILY_EFFORT = 7f;

    public static float MAX_PROJECT_DURATION_DAYS = 45f;
    public static float MAX_PROJECT_DURATION = MAX_PROJECT_DURATION_DAYS * DAILY_EFFORT;

    public static float PROJECT_PLANNING_DURATION_DAYS = 2f;
    public static float PROJECT_PLANNING_DURATION = PROJECT_PLANNING_DURATION_DAYS * DAILY_EFFORT;

    public static float RELEASE_PLANNING_DURATION_DAYS = 0.5f;
    public static float RELEASE_PLANNING_DURATION = RELEASE_PLANNING_DURATION_DAYS * DAILY_EFFORT;

    public static float SPRINT_PLANNING_DURATION_DAYS = 0.25f;
    public static float SPRINT_PLANNING_DURATION = SPRINT_PLANNING_DURATION_DAYS * DAILY_EFFORT;

    public static float TASK_SIZE_UPPER_LIMIT = 2500;
    public static float TASK_SIZE_LOWER_LIMIT = 100;
    public static float TASK_SIZE_MODE = 250;

    public static float NEW_FEATURE_EFFORT_UPPER_LIMIT = 10;
    public static float NEW_FEATURE_EFFORT_LOWER_LIMIT = 0.5f;
    public static float NEW_FEATURE_EFFORT_MODE = 1;

    public static float MINOR_DEFECT_EFFORT_UPPER_LIMIT = 3;
    public static float MINOR_DEFECT_EFFORT_LOWER_LIMIT = 1;
    public static float MINOR_DEFECT_EFFORT_MODE = 2;

    public static float MAJOR_DEFECT_EFFORT_UPPER_LIMIT = 24;
    public static float MAJOR_DEFECT_EFFORT_LOWER_LIMIT = 3;
    public static float MAJOR_DEFECT_EFFORT_MODE = 8;

    public static float INJECTED_DEFECT_UPPER_LIMIT = 5;
    public static float INJECTED_DEFECT_LOWER_LIMIT = 0;
    public static float INJECTED_DEFECT_MODE = 2;

    // lowest value represents the highest priority
    public static List<Pair<Integer, Double>> NEW_FEATURE_PRIORITIES = new ArrayList<Pair<Integer, Double>>() {{
        add(new Pair(5, 0.25));
        add(new Pair(4, 0.25));
        add(new Pair(3, 0.15));
        add(new Pair(2, 0.1));
        add(new Pair(1, 0.1));
    }};

    public static List<Pair<Integer, Double>> MINOR_DEFECTS_PRIORITIES = new ArrayList<Pair<Integer, Double>>() {{
        add(new Pair(5, 0.5));
        add(new Pair(4, 0.35));
        add(new Pair(3, 0.15));
    }};

    public static List<Pair<Integer, Double>> MAJOR_DEFECTS_PRIORITIES = new ArrayList<Pair<Integer, Double>>() {{
        add(new Pair(3, 0.5));
        add(new Pair(2, 0.35));
        add(new Pair(1, 0.15));
    }};

    public static List<Pair<Task.TaskType, Double>> TYPE_OF_DEFECT = new ArrayList<Pair<Task.TaskType, Double>>() {{
        add(new Pair(Task.TaskType.MINOR_DEFECT, 0.9));
        add(new Pair(Task.TaskType.MAJOR_DEFECT, 0.1));
    }};

    public static float TDD_CODING_EFFORT = 1.16f;
    public static float NONTDD_CODING_EFFORT = 1f;

    public static float TDD_TESTING_EFFORT = 1f;
    public static float NONTDD_TESTING_EFFORT = 1.25f;

    public static float TDD_INJECTED_DEFECTS = 1f;
    public static float NONTDD_INJECTED_DEFECTS = 1.18f;
}
