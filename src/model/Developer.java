package model;

public class Developer {

    public enum DeveloperType {
        DEVELOPER,
        TESTER
    }

    public enum Skill {
        JUNIOR(2f, 1.32f),
        MID_LEVEL(1f, 1f),
        SENIOR(0.5f, 0.76f);

        private float productivity;

        private float defectInjectionRate;

        Skill(float level, float defectRate) {
            productivity = level;
            defectInjectionRate = defectRate;
        }
    }

    private static int instanceCounter = 1;

    private final int id;

    /**
     * Type of developer
     */
    private final DeveloperType type;

    /**
     * Developer daily effort
     * Represents the number of available hours
     */
    private final float dailyEffort;

    /**
     * Developer skill level
     * The skill level defines the developer productivity and defect injection rate
     *
     * @see Skill
     */
    private final Skill skill;

    /**
     * True id the developer is busy with a task (or planning activity)
     */
    private boolean isBusy;

    /**
     * Developer usage area
     * Represents the time the developer is occupied with a task
     * during the simulation time
     */
    private double usageArea;

    public Developer(DeveloperType type,
                     float dailyEffort,
                     Skill skill) {

        this.id = instanceCounter++;
        this.isBusy = false;
        this.type = type;
        this.dailyEffort = dailyEffort;
        this.skill = skill;

        this.usageArea = 0;
    }

    /**
     * Updates the status of the developer
     *
     * @param busy true when the developer has an assigned task
     *             false when he finishes the task
     */
    public void updateState(boolean busy) {
        isBusy = busy;
    }

    /**
     * Returns true if the developer is busy
     *
     * @return
     */
    public boolean isBusy() {
        return isBusy;
    }

    /**
     * Get the productivity of the developer
     * An older developer has an higher productivity
     *
     * @return the productivity of the developer
     */
    public float getProductivity() {
        return skill.productivity;
    }

    /**
     * Get the defect injection rate of the developer
     * An older developer has a lower defect injection rate
     *
     * @return the defect injection rate of the developer
     */
    public float getDefectInjectionRate() {
        return skill.defectInjectionRate;
    }


    /** Get the type of the developer
     * @see DeveloperType
     * @return the type of the developer
     */
    public DeveloperType getType() {
        return type;
    }

    /**
     * Update the developer usage area according to the simulation time
     *
     * @param timeLastEvent
     */
    public void updateUsageArea(double timeLastEvent) {
        if (isBusy) {
            usageArea += timeLastEvent;
        }
    }

    /**
     * Returns the usage area for the current developer
     *
     * @return the usage area for the current developer
     */
    public double getUsageArea() {
        return usageArea;
    }

    @Override
    public String toString() {
        return "id: " + id + ", type: " + type + ", skill: " + skill + ", isBusy: " + isBusy;
    }

}
