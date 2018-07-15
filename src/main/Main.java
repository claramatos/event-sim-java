package main;

import model.Simulator;
import utils.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static int NUMBER_OF_ITERATIONS = 100;
    private final static int MAX_NUMBER_RESOURCES = 25;

    private static void saveToCsv(String filename, List<String> resultList) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filename);

            for (String result : resultList) {
                fileWriter.append(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {

            }
        }

    }

    private static void runSimulationResources(List<String> resultsList,
                                               int numberOfSeniorDevelopers,
                                               int numberOfMidLevelDevelopers,
                                               int numberOfJuniorDevelopers,
                                               int numberOfTesters) {
        Simulator simulator;

        int iter = NUMBER_OF_ITERATIONS;
        while (iter > 0) {
            System.out.println("iter: " + iter);

            simulator = new Simulator(false,
                    numberOfSeniorDevelopers,
                    numberOfMidLevelDevelopers,
                    numberOfJuniorDevelopers,
                    numberOfTesters);

            simulator.setup();
            simulator.run();
            resultsList.add(simulator.getFullReport());
            iter--;
            Constants.SEED -= 1;
        }

    }

    private static void runSimulationForTesters() {
        int numberOfSeniorDevelopers = Constants.NUMBER_OF_SENIOR_DEVELOPERS;
        int numberOfMidLevelDevelopers = Constants.NUMBER_OF_MID_LEVEL_DEVELOPERS;
        int numberOfJuniorDevelopers = Constants.NUMBER_OF_JUNIOR_DEVELOPERS;

        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_tester.csv";

        int numberOfResources = 1;
        while (numberOfResources != MAX_NUMBER_RESOURCES) {
            System.out.println("nr: " + numberOfResources);

            runSimulationResources(resultsList,
                    numberOfSeniorDevelopers,
                    numberOfMidLevelDevelopers,
                    numberOfJuniorDevelopers,
                    numberOfResources);

            numberOfResources += 1;
        }

        saveToCsv(filename, resultsList);
    }

    private static void runSimulationForJuniors() {
        int numberOfSeniorDevelopers = Constants.NUMBER_OF_SENIOR_DEVELOPERS;
        int numberOfMidLevelDevelopers = Constants.NUMBER_OF_MID_LEVEL_DEVELOPERS;
        int numberOfTesters = Constants.NUMBER_OF_TESTERS;

        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_juniors.csv";

        int numberOfResources = 1;
        while (numberOfResources != MAX_NUMBER_RESOURCES) {
            System.out.println("nr: " + numberOfResources);

            runSimulationResources(resultsList,
                    numberOfSeniorDevelopers,
                    numberOfMidLevelDevelopers,
                    numberOfResources,
                    numberOfTesters);

            numberOfResources += 1;
        }

        saveToCsv(filename, resultsList);
    }

    private static void runSimulationForMidLevels() {
        int numberOfSeniorDevelopers = Constants.NUMBER_OF_SENIOR_DEVELOPERS;
        int numberOfJuniorDevelopers = Constants.NUMBER_OF_JUNIOR_DEVELOPERS;
        int numberOfTesters = Constants.NUMBER_OF_TESTERS;


        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_midlevel.csv";

        int numberOfResources = 1;
        while (numberOfResources != MAX_NUMBER_RESOURCES) {
            System.out.println("nr: " + numberOfResources);

            runSimulationResources(resultsList,
                    numberOfSeniorDevelopers,
                    numberOfResources,
                    numberOfJuniorDevelopers,
                    numberOfTesters);

            numberOfResources += 1;
        }

        saveToCsv(filename, resultsList);
    }

    private static void runSimulationForSeniors() {
        int numberOfMidLevelDevelopers = Constants.NUMBER_OF_MID_LEVEL_DEVELOPERS;
        int numberOfJuniorDevelopers = Constants.NUMBER_OF_JUNIOR_DEVELOPERS;
        int numberOfTesters = Constants.NUMBER_OF_TESTERS;


        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_seniors.csv";

        int numberOfResources = 1;
        while (numberOfResources != MAX_NUMBER_RESOURCES) {
            System.out.println("nr: " + numberOfResources);

            runSimulationResources(resultsList,
                    numberOfResources,
                    numberOfMidLevelDevelopers,
                    numberOfJuniorDevelopers,
                    numberOfTesters);

            numberOfResources += 1;
        }

        saveToCsv(filename, resultsList);
    }

    public static void runFinalSimulation() {
        int numberOfSeniorDevelopers = Constants.NUMBER_OF_SENIOR_DEVELOPERS;
        int numberOfMidLevelDevelopers = Constants.NUMBER_OF_MID_LEVEL_DEVELOPERS;
        int numberOfJuniorDevelopers = Constants.NUMBER_OF_JUNIOR_DEVELOPERS;
        int numberOfTesters = Constants.NUMBER_OF_TESTERS;


        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_final.csv";

        runSimulationResources(resultsList,
                numberOfSeniorDevelopers,
                numberOfMidLevelDevelopers,
                numberOfJuniorDevelopers,
                numberOfTesters);

        saveToCsv(filename, resultsList);
    }

    private static void runSimulationTdd() {

        List<String> resultsList = new ArrayList<>();
        resultsList.add(Simulator.getHeader());

        String filename = "results_tdd.csv";


        int iter = NUMBER_OF_ITERATIONS;
        while (iter > 0) {
            System.out.println("iter: " + iter);

            Simulator simulatorNonTdd = new Simulator(false);

            simulatorNonTdd.setup();
            simulatorNonTdd.run();
            resultsList.add(simulatorNonTdd.getFullReport());

            Simulator simulatorTdd = new Simulator(true);

            simulatorTdd.setup();
            simulatorTdd.run();
            resultsList.add(simulatorTdd.getFullReport());

            iter--;
            Constants.SEED -= 1;
        }

        saveToCsv(filename, resultsList);
    }

    public static void runDefaultSimulation() {
        Simulator simulator = new Simulator(false);
        simulator.setup();
        simulator.run();

        System.out.println(simulator.getReport());
    }

    public static void test1() {
        Constants.SEED = 12345;
        runSimulationForTesters();
        Constants.SEED = 12345;
        runSimulationForJuniors();
        Constants.SEED = 12345;
        runSimulationForMidLevels();
        Constants.SEED = 12345;
        runSimulationForSeniors();
    }

    public static void main(String[] args) {
        //runFinalSimulation();
        runDefaultSimulation();
        //runSimulationTdd();
    }

}