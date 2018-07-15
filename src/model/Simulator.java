package model;

import utils.Constants;

import java.util.*;
import java.util.logging.*;

public class Simulator {

    private static final Logger LOGGER = Logger.getLogger(Simulator.class.getName());

    /**
     * State variables
     */
    private double simTime;
    private double timeLastEvent;
    private int curProject;
    private int curRelease;
    private int curSprint;

    /**
     * Queue that stores the events scheduled during the simulation
     */
    private Queue<Event> eventsQueue;

    /**
     * Available resources
     */
    private Resource developers;
    private Resource testers;

    /**
     * Project backlogs
     */
    private Backlog projectBacklog;
    private Backlog releaseBacklog;
    private Backlog sprintBacklog;
    private Backlog doneBacklog;

    private boolean useTdd;

    private String result;

    private final int numberOfSeniorDevelopers;
    private final int numberOfMidLevelDevelopers;
    private final int numberOfJuniorDevelopers;
    private final int numberOfTesters;

    private TaskCreator taskCreator;

    /**
     * Default constructor
     *
     * @param useTdd true if tdd is used
     */
    public Simulator(boolean useTdd) {
        this.useTdd = useTdd;
        this.result = "";

        this.numberOfSeniorDevelopers = Constants.NUMBER_OF_SENIOR_DEVELOPERS;
        this.numberOfMidLevelDevelopers = Constants.NUMBER_OF_MID_LEVEL_DEVELOPERS;
        this.numberOfJuniorDevelopers = Constants.NUMBER_OF_JUNIOR_DEVELOPERS;
        this.numberOfTesters = Constants.NUMBER_OF_TESTERS;

        Level level = Level.SEVERE;
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(level);
    }

    /**
     * Constructor
     *
     * @param useTdd  true if tdd is used
     * @param seniors number of senior developers
     * @param mid     number of mid level developers
     * @param juniors number of junior developers
     * @param testers number of testers
     */
    public Simulator(boolean useTdd, int seniors, int mid, int juniors, int testers) {
        this.useTdd = useTdd;
        this.result = "";

        this.numberOfSeniorDevelopers = seniors;
        this.numberOfMidLevelDevelopers = mid;
        this.numberOfJuniorDevelopers = juniors;
        this.numberOfTesters = testers;

        Level level = Level.SEVERE;
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(level);
    }

    /**
     * Main routine
     */
    public int run() {
        if (eventsQueue.isEmpty()) {
            return -1;
        } else {
            do {
                Event curEvent = eventsQueue.remove();
                simTime = curEvent.getStartTime();

                LOGGER.log(Level.CONFIG, "\ncurEventType: " + curEvent.getType() + ", simTime: " + simTime);

                Task curTask = curEvent.getTask();

                updateStatistics();

                switch (curEvent.getType()) {
                    case PROJECT_PLANNING_END:
                        createProject();
                        break;
                    case RELEASE_PLANNING_END:
                        createRelease();
                        break;
                    case SPRINT_PLANNING_END:
                        createSprint();
                        break;
                    case DEVELOPMENT_END:
                        developmentSession(curTask);
                        break;
                    case TEST_END:
                        testingSession(curTask);
                        break;
                    case PROJECT_END:
                        return -1;
                }

            } while (!eventsQueue.isEmpty());
        }

        return 0;
    }

    /**
     * Setup routine
     */
    public void setup() {
        LOGGER.log(Level.FINE, "\nsetup");

        simTime = 0;
        timeLastEvent = -1;
        curProject = 0;
        curRelease = 0;
        curSprint = 0;

        eventsQueue = new PriorityQueue<>();
        doneBacklog = new Backlog();

        taskCreator = new TaskCreator(useTdd);
        createTeam();

        developers.occupyTeam();
        testers.occupyTeam();

        // add first event
        eventsQueue.add(new Event(Event.EventType.PROJECT_PLANNING_END, null,
                simTime + Constants.PROJECT_PLANNING_DURATION));

        // add end of simulation event
        if (Constants.SET_MAX_PROJECT_DURATION) {
            eventsQueue.add(new Event(Event.EventType.PROJECT_END, null,
                    simTime + Constants.MAX_PROJECT_DURATION));
        }
    }

    /**
     * Creates the team according to the specified number of developers and testers
     */
    private void createTeam() {
        LOGGER.log(Level.FINE, "createTeam");

        List<Developer> developersList = new ArrayList<>();
        for (int i = 0; i < numberOfJuniorDevelopers; i++) {
            developersList.add(new Developer(Developer.DeveloperType.DEVELOPER,
                    Constants.DAILY_EFFORT, Developer.Skill.JUNIOR));
        }

        for (int i = 0; i < numberOfMidLevelDevelopers; i++) {
            developersList.add(new Developer(Developer.DeveloperType.DEVELOPER,
                    Constants.DAILY_EFFORT, Developer.Skill.MID_LEVEL));
        }

        for (int i = 0; i < numberOfSeniorDevelopers; i++) {
            developersList.add(new Developer(Developer.DeveloperType.DEVELOPER,
                    Constants.DAILY_EFFORT, Developer.Skill.SENIOR));
        }

        developers = new Resource(developersList);

        ArrayList<Developer> testersList = new ArrayList<>();
        for (int i = 0; i < numberOfTesters; i++) {
            testersList.add(new Developer(Developer.DeveloperType.TESTER,
                    Constants.DAILY_EFFORT, Developer.Skill.MID_LEVEL));
        }

        testers = new Resource(testersList);

        projectBacklog = new Backlog();
        releaseBacklog = new Backlog();
        sprintBacklog = new Backlog();
    }

    /**
     * Create project routine
     */
    private void createProject() {
        LOGGER.log(Level.FINE, "createProject");

        developers.freeTeam();
        testers.freeTeam();

        curProject += 1;

        while (projectBacklog.getBacklogEffort() <
                Constants.MAX_PROJECT_EFFORT * (1.0 - Constants.PROJECT_DEFECT_EFFORT_PERC)) {
            Task curTask = taskCreator.createTask(Task.TaskType.NEW_FEATURE);
            projectBacklog.addTaskToBacklog(curTask);
        }

        LOGGER.log(Level.FINE, "projectBacklog size: " + projectBacklog.size()
                + ", currentProjectEffort: " + projectBacklog.getBacklogEffort());

        developers.occupyTeam();
        testers.occupyTeam();

        eventsQueue.add(new Event(Event.EventType.RELEASE_PLANNING_END, null,
                simTime + Constants.RELEASE_PLANNING_DURATION));
    }

    /**
     * Create release routine
     */
    private void createRelease() {
        LOGGER.log(Level.FINE, "createRelease");

        developers.freeTeam();
        testers.freeTeam();

        curRelease += 1;
        curSprint = 0;

        // reorder priority queue based on the new assigned priorities
        projectBacklog.updateTaskQueue(taskCreator.updatePriorityOfTasks(projectBacklog.getBacklog()));

        // select the tasks for the current release
        while (releaseBacklog.getBacklogEffort() < Constants.MAX_RELEASE_EFFORT) {
            if (!projectBacklog.isEmpty()) {
                Task task = projectBacklog.getNextTask();
                releaseBacklog.addTaskToBacklog(task);
            } else {
                break;
            }
        }

        LOGGER.log(Level.FINE, "releaseBacklog size: " + releaseBacklog.size()
                + ", curReleaseEffort: " + releaseBacklog.getBacklogEffort());

        developers.occupyTeam();
        testers.occupyTeam();

        eventsQueue.add(new Event(Event.EventType.SPRINT_PLANNING_END, null,
                simTime + Constants.SPRINT_PLANNING_DURATION));
    }


    /**
     * Create sprint routine
     */
    private void createSprint() {
        LOGGER.log(Level.FINE, "createSprint");

        developers.freeTeam();
        testers.freeTeam();

        LOGGER.log(Level.FINE, "release backlog size: " + releaseBacklog.size());

        curSprint += 1;

        while (sprintBacklog.getBacklogEffort() < Constants.MAX_SPRINT_EFFORT) {
            if (!releaseBacklog.isEmpty()) {
                Task task = releaseBacklog.getNextTask();
                sprintBacklog.addTaskToBacklog(task);
            } else {
                LOGGER.log(Level.FINE, "release is empty, simTime: " + simTime
                        + " , sprint size: " + sprintBacklog.size());
                break;
            }
        }

        LOGGER.log(Level.FINE, "sprintBacklog size: " + sprintBacklog.size()
                + ", curSprintEffort: " + sprintBacklog.getBacklogEffort());
        LOGGER.log(Level.FINE, "curSprint: " + curSprint);

        while (!sprintBacklog.isEmpty()) {
            Task taskToDevelop = sprintBacklog.getNextTask();
            taskToDevelop.updateArrivalTime(simTime);
            developmentEndEvent(taskToDevelop);
        }
    }

    private void createDefectTasks(int numberOfDefects) {
        LOGGER.log(Level.FINE, "createDefectTasks");

        for (int i = 0; i < numberOfDefects; i++) {
            // tasks of the type defect do not increase the system size
            Task.TaskType defectType = taskCreator.getDefectType();
            Task defect = taskCreator.createTask(defectType);
            releaseBacklog.addTaskToBacklog(defect);
        }
    }

    /**
     * Development session routine
     */
    private void developmentSession(Task developedTask) {
        LOGGER.log(Level.FINE, "developmentSession");

        developedTask.updateArrivalTime(simTime);

        LOGGER.log(Level.FINER, "developedTask: " + developedTask);

        Developer curDeveloper = developedTask.getDeveloper();
        curDeveloper.updateState(false);

        if (!developers.isTaskQueueEmpty()) {
            Task nextTask = developers.getNextTask();
            developmentEndEvent(nextTask);
        }

        testingEndEvent(developedTask);
    }

    private void developmentEndEvent(Task curTask) {
        Developer curDeveloper = developers.getFreeDeveloper();

        if (curDeveloper == null) {
            developers.addTaskToQueue(curTask);
        } else {
            curTask.setDeveloper(curDeveloper);
            curTask.updateStatus(Task.TaskStatus.IN_PROGRESS);

            developers.addDelay(simTime - curTask.getArrivalTime());
            eventsQueue.add(new Event(Event.EventType.DEVELOPMENT_END, curTask,
                    simTime + curTask.getCodingEffort()));
        }
    }

    /**
     * Testing session routine
     */
    private void testingSession(Task testedTask) {
        LOGGER.log(Level.FINE, "testingSession");

        testedTask.updateArrivalTime(simTime);
        LOGGER.log(Level.FINER, "testedTask: " + testedTask);

        Developer curTester = testedTask.getDeveloper();
        curTester.updateState(false);

        taskCreator.detectDefects(testedTask);
        doneBacklog.addTaskToBacklog(testedTask);

        if (!testers.isTaskQueueEmpty()) {
            Task nextTask = testers.getNextTask();
            testingEndEvent(nextTask);
        }

        if (testers.isTaskQueueEmpty() && developers.isTaskQueueEmpty()
                && developers.isTeamFree() && testers.isTeamFree()) {

            int injectedDefects = doneBacklog.getInjectedDefects();
            createDefectTasks(injectedDefects);

            // reset priorities when defects are detected
            if (injectedDefects != 0) {
                releaseBacklog.updateTaskQueue(taskCreator.updatePriorityOfTasks(releaseBacklog.getBacklog()));
            }

            LOGGER.log(Level.FINE, "detectedDefects: " + injectedDefects
                    + " , release backlog size: " + releaseBacklog.size());

            // if all backlogs are empty finish the project
            if (sprintBacklog.isEmpty() && releaseBacklog.isEmpty() && projectBacklog.isEmpty()) {
                eventsQueue.add(new Event(Event.EventType.PROJECT_END, null, simTime));
            }
            // if the max number of releases is reached end the project
            if (Constants.SET_MAX_PROJECT_DURATION && curRelease == Constants.NUMBER_RELEASES_PER_PROJECT) {
                eventsQueue.add(new Event(Event.EventType.PROJECT_END, null, simTime));
            }
            // if the max number of sprints for the current release is reached schedule a new release planning event or
            // if the release does not have enough tasks to fill a sprint schedule a new release planning event
            else if (curSprint == Constants.NUMBER_SPRINTS_PER_RELEASE ||
                    releaseBacklog.getBacklogEffort() < Constants.MAX_SPRINT_EFFORT && !projectBacklog.isEmpty()) {
                eventsQueue.add(new Event(Event.EventType.RELEASE_PLANNING_END, null,
                        simTime + Constants.RELEASE_PLANNING_DURATION));
            } else {
                eventsQueue.add(new Event(Event.EventType.SPRINT_PLANNING_END, null,
                        simTime + Constants.SPRINT_PLANNING_DURATION));
            }

        }
    }

    private void testingEndEvent(Task curTask) {
        Developer curTester = testers.getFreeDeveloper();

        if (curTester == null) {
            testers.addTaskToQueue(curTask);
        } else {
            curTask.setDeveloper(curTester);
            curTask.updateStatus(Task.TaskStatus.TESTING);

            testers.addDelay(simTime - curTask.getArrivalTime());
            eventsQueue.add(new Event(Event.EventType.TEST_END, curTask, simTime + curTask.getTestEffort()));
        }
    }

    /**
     * Get the simulation time
     *
     * @return the simulation time
     */
    private double getSimTime() {
        return simTime;
    }

    /**
     * Upadate statistics routine
     */
    private void updateStatistics() {
        if (timeLastEvent == -1) {
            timeLastEvent = simTime;
        }

        double timeSinceLastEvent = simTime - timeLastEvent;
        timeLastEvent = simTime;

        developers.updateTaskWaitingArea(timeSinceLastEvent);
        developers.updateUsageArea(timeSinceLastEvent);

        testers.updateTaskWaitingArea(timeSinceLastEvent);
        testers.updateUsageArea(timeSinceLastEvent);
    }

    /**
     * Get the report for the current simulation
     *
     * @return the report for the current simulation
     */
    public String getReport() {
        String tab = "\t";
        String newline = "\n";

        StringBuilder builder = new StringBuilder();

        builder.append("End of simulation time = ").append(simTime).append(newline);

        builder.append("\nDevelopers:");
        builder.append(newline).append(tab).append("Number of delays = ").append(developers.getDelays());
        builder.append(newline).append(tab).append("Developers usage rate = ")
                .append(developers.calculateUsageRate(simTime));
        builder.append(newline).append(tab).append("Tasks's waiting time average = ")
                .append(developers.calculateWaitingTimeAverage());
        builder.append(newline).append(tab).append("Average number of tasks in queue = ").
                append(developers.calculateWaitingAverage(simTime));
        builder.append(newline);

        builder.append("\nTesters:");
        builder.append(newline).append(tab).append("Number of delays = ").append(testers.getDelays());
        builder.append(newline).append(tab).append("Testers usage rate = ")
                .append(testers.calculateUsageRate(simTime));
        builder.append(newline).append(tab).append("Tasks's waiting time average = ")
                .append(testers.calculateWaitingTimeAverage());
        builder.append(newline).append(tab).append("Average number of tasks in queue = ")
                .append(testers.calculateWaitingAverage(simTime));

        builder.append(newline).append(newline);

        builder.append(newline).append("projectBacklog, undone tasks: ").append(projectBacklog.size());
        if (projectBacklog.size() > 0) {
            builder.append(newline).append(tab).append("features = ")
                    .append(projectBacklog.getNumberOfNewFeatures());
            builder.append(newline).append(tab).append("major defects = ")
                    .append(projectBacklog.getNumberOfMajorDefects());
            builder.append(newline).append(tab).append("minor defects = ")
                    .append(projectBacklog.getNumberOfMinorDefects());
        }
        builder.append(newline);

        builder.append(newline).append("releaseBacklog, undone tasks: ").append(releaseBacklog.size());
        if (releaseBacklog.size() > 0) {
            builder.append(newline).append(tab).append("features = ")
                    .append(releaseBacklog.getNumberOfNewFeatures());
            builder.append(newline).append(tab).append("major defects = ")
                    .append(releaseBacklog.getNumberOfMajorDefects());
            builder.append(newline).append(tab).append("minor defects = ")
                    .append(releaseBacklog.getNumberOfMinorDefects());
        }
        builder.append(newline);

        builder.append(newline).append("sprintBacklog, undone tasks: ").append(sprintBacklog.size());
        if (sprintBacklog.size() > 0) {
            builder.append(newline).append(tab).append("features = ").append(sprintBacklog.getNumberOfNewFeatures());
            builder.append(newline).append(tab).append("major defects = ")
                    .append(sprintBacklog.getNumberOfMajorDefects());
            builder.append(newline).append(tab).append("minor defects = ")
                    .append(sprintBacklog.getNumberOfMinorDefects());
        }
        builder.append(newline);

        builder.append(newline).append("doneBacklog, undone tasks: ").append(doneBacklog.size());
        if (doneBacklog.size() > 0) {
            builder.append(newline).append(tab).append("features = ")
                    .append(doneBacklog.getNumberOfNewFeatures());
            builder.append(newline).append(tab).append("major defects = ")
                    .append(doneBacklog.getNumberOfMajorDefects());
            builder.append(newline).append(tab).append("minor defects = ")
                    .append(doneBacklog.getNumberOfMinorDefects());
        }
        builder.append(newline);

        result += builder.toString();

        return result;
    }

    /**
     * Get the full report for the current simulation
     * The report is provided comma separated to be stored in a csv
     *
     * @return the full report for the current simulation
     */
    public String getFullReport() {
        String comma = ",";
        String newline = "\n";

        String result = "";
        result += numberOfJuniorDevelopers + comma;
        result += numberOfMidLevelDevelopers + comma;
        result += numberOfSeniorDevelopers + comma;
        result += numberOfTesters + comma;

        result += String.valueOf(developers.getDelays()) + comma;
        result += String.valueOf(developers.calculateUsageRate(simTime)) + comma;
        result += String.valueOf(developers.calculateWaitingTimeAverage()) + comma;
        result += String.valueOf(developers.calculateWaitingAverage(simTime)) + comma;

        result += String.valueOf(testers.getDelays()) + comma;
        result += String.valueOf(testers.calculateUsageRate(simTime)) + comma;
        result += String.valueOf(testers.calculateWaitingTimeAverage()) + comma;
        result += String.valueOf(testers.calculateWaitingAverage(simTime)) + comma;

        result += String.valueOf(doneBacklog.getNumberOfLocs()) + comma;
        result += String.valueOf(doneBacklog.getNumberOfDefectsPerKLoc()) + comma;
        result += String.valueOf(doneBacklog.getNumberOfNewFeatures()) + comma;
        result += String.valueOf(doneBacklog.getNumberOfMajorDefects()) + comma;
        result += String.valueOf(doneBacklog.getNumberOfMinorDefects()) + comma;

        result += String.valueOf(simTime) + comma;
        result += String.valueOf(useTdd) + comma;

        return result + newline;
    }

    /**
     * Get the header of the full report
     *
     * @return the header of the full report
     */
    public static String getHeader() {
        String comma = ",";
        String newline = "\n";

        String header = "";

        header += "NUMBER_OF_JUNIOR_DEVELOPERS" + comma;
        header += "NUMBER_OF_MID_LEVEL_DEVELOPERS" + comma;
        header += "NUMBER_OF_SENIOR_DEVELOPERS" + comma;
        header += "NUMBER_OF_TESTERS" + comma;

        header += "DEVS_DELAYS" + comma;
        header += "DEVS_USAGE_RATE" + comma;
        header += "DEVS_WAITING_TIME_AVERAGE" + comma;
        header += "DEVS_WAITING_AVERAGE" + comma;

        header += "TESTERS_DELAYS" + comma;
        header += "TESTERS_USAGE_RATE" + comma;
        header += "TESTERS_WAITING_TIME_AVERAGE" + comma;
        header += "TESTERS_WAITING_AVERAGE" + comma;

        header += "NUMBER_OF_LOCS" + comma;
        header += "NUMBER_DEFECTS_KLOC" + comma;
        header += "NUMBER_OF_NEW_FEATURES" + comma;
        header += "NUMBER_OF_MAJOR_DEFECTS" + comma;
        header += "NUMBER_OF_MINOR_DEFECTS" + comma;

        header += "SIM_TIME" + comma;
        header += "USE_TDD" + comma;

        return header + newline;
    }


}
