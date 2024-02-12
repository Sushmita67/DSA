/*
Implement ant colony algorithm solving travelling a salesman problem
 */

package final_assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Question5A {

    private int numberOfCities;
    private int[][] distanceMatrix;
    private int numberOfAnts;
    private double[][] pheromoneMatrix;
    private double alpha;
    private double beta;
    private double evaporationRate;
    private double initialPheromone;
    private int maxIterations;

    public Question5A(int[][] distanceMatrix, int numberOfAnts, double alpha, double beta,
                                 double evaporationRate, double initialPheromone, int maxIterations) {
        this.numberOfCities = distanceMatrix.length;
        this.distanceMatrix = distanceMatrix;
        this.numberOfAnts = numberOfAnts;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.initialPheromone = initialPheromone;
        this.maxIterations = maxIterations;
        initializePheromoneMatrix();
    }

    private void initializePheromoneMatrix() {
        pheromoneMatrix = new double[numberOfCities][numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                pheromoneMatrix[i][j] = initialPheromone;
            }
        }
    }

    public List<Integer> solve() {
        List<Integer> bestTour = null;
        double bestTourLength = Double.POSITIVE_INFINITY;
        Random random = new Random();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<List<Integer>> antTours = new ArrayList<>();
            double[] tourLengths = new double[numberOfAnts];

            // Generate tours for each ant
            for (int ant = 0; ant < numberOfAnts; ant++) {
                List<Integer> tour = generateTour(random.nextInt(numberOfCities));
                antTours.add(tour);
                tourLengths[ant] = calculateTourLength(tour);
                if (tourLengths[ant] < bestTourLength) {
                    bestTourLength = tourLengths[ant];
                    bestTour = new ArrayList<>(tour);
                }
            }

            // Update pheromone matrix
            updatePheromoneMatrix(antTours, tourLengths);

            // Evaporate pheromone
            evaporatePheromone();
        }

        return bestTour;
    }

    private List<Integer> generateTour(int startCity) {
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[numberOfCities];
        int currentCity = startCity;
        tour.add(startCity);
        visited[startCity] = true;

        for (int step = 1; step < numberOfCities; step++) {
            int nextCity = selectNextCity(currentCity, visited);
            tour.add(nextCity);
            visited[nextCity] = true;
            currentCity = nextCity;
        }

        tour.add(startCity); // Complete the tour
        return tour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double[] probabilities = new double[numberOfCities];
        double totalProbability = 0;

        for (int city = 0; city < numberOfCities; city++) {
            if (!visited[city]) {
                probabilities[city] = Math.pow(pheromoneMatrix[currentCity][city], alpha) *
                        Math.pow(1.0 / distanceMatrix[currentCity][city], beta);
                totalProbability += probabilities[city];
            }
        }

        double randomValue = Math.random() * totalProbability;
        double sum = 0;
        for (int city = 0; city < numberOfCities; city++) {
            if (!visited[city]) {
                sum += probabilities[city];
                if (sum >= randomValue) {
                    return city;
                }
            }
        }

        throw new RuntimeException("Failed to select next city");
    }

    private double calculateTourLength(List<Integer> tour) {
        double length = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int city1 = tour.get(i);
            int city2 = tour.get(i + 1);
            length += distanceMatrix[city1][city2];
        }
        return length;
    }

    private void updatePheromoneMatrix(List<List<Integer>> antTours, double[] tourLengths) {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                pheromoneMatrix[i][j] *= (1 - evaporationRate);
            }
        }

        for (int ant = 0; ant < numberOfAnts; ant++) {
            List<Integer> tour = antTours.get(ant);
            double tourLength = tourLengths[ant];
            for (int i = 0; i < tour.size() - 1; i++) {
                int city1 = tour.get(i);
                int city2 = tour.get(i + 1);
                pheromoneMatrix[city1][city2] += 1.0 / tourLength;
                pheromoneMatrix[city2][city1] += 1.0 / tourLength;
            }
        }
    }

    private void evaporatePheromone() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                pheromoneMatrix[i][j] *= (1 - evaporationRate);
            }
        }
    }

    public static void main(String[] args) {
        int[][] distanceMatrix = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };
        int numberOfAnts = 10;
        double alpha = 1.0;
        double beta = 2.0;
        double evaporationRate = 0.5;
        double initialPheromone = 0.1;
        int maxIterations = 100;

        Question5A aco = new Question5A(distanceMatrix, numberOfAnts, alpha, beta,
                evaporationRate, initialPheromone, maxIterations);
        List<Integer> bestTour = aco.solve();
        System.out.println("Best tour: " + bestTour);
    }
}