/*
You are a planner working on organizing a series of events in a row of n venues. Each venue can be decorated with
one of the k available themes. However, adjacent venues should not have the same theme. The cost of decorating
each venue with a certain theme varies.
The costs of decorating each venue with a specific theme are represented by an n x k cost matrix. For example,
costs [0][0] represents the cost of decorating venue 0 with theme 0, and costs[1][2] represents the cost of
decorating venue 1 with theme 2. Your task is to find the minimum cost to decorate all the venues while adhering
to the adjacency constraint.
For example, given the input costs = [[1, 5, 3], [2, 9, 4]], the minimum cost to decorate all the venues is 5. One
possible arrangement is decorating venue 0 with theme 0 and venue 1 with theme 2, resulting in a minimum cost of
1 + 4 = 5. Alternatively, decorating venue 0 with theme 2 and venue 1 with theme 0 also yields a minimum cost of
3 + 2 = 5.
Write a function that takes the cost matrix as input and returns the minimum cost to decorate all the venues while
satisfying the adjacency constraint.
Please note that the costs are positive integers.
Example: Input: [[1, 3, 2], [4, 6, 8], [3, 1, 5]] Output: 7
Explanation: Decorate venue 0 with theme 0, venue 1 with theme 1, and venue 2 with theme 0. Minimum cost: 1 +
6 + 1 = 7.
 */


package final_assignment;

public class Question1A {

    public static int minCostToDecorate(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) return 0;

        int n = costs.length; // Number of venues
        int k = costs[0].length; // Number of themes

        // Create a DP table to store minimum costs
        int[][] dp = new int[n][k];

        // Initialize the first row of the DP table with the costs of the first venue
        for (int j = 0; j < k; j++) {
            dp[0][j] = costs[0][j];
        }

        // Iterate through the venues starting from the second one
        for (int i = 1; i < n; i++) {
            // Iterate through the themes for the current venue
            for (int j = 0; j < k; j++) {
                int minCost = Integer.MAX_VALUE;
                // Calculate the minimum cost of decorating the current venue with the current theme
                for (int m = 0; m < k; m++) {
                    // Ensure adjacent venues don't have the same theme
                    if (m != j) {
                        minCost = Math.min(minCost, dp[i - 1][m] + costs[i][j]);
                    }
                }
                dp[i][j] = minCost;
            }
        }

        // Find the minimum cost from the last row of the DP table
        int minCost = dp[n - 1][0];
        for (int j = 1; j < k; j++) {
            minCost = Math.min(minCost, dp[n - 1][j]);
        }

        return minCost;
    }

    public static void main(String[] args) {
        int[][] costs = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        System.out.println("min_cost_to_decorate all vanues: " + minCostToDecorate(costs));   //Output:7
    }
}
