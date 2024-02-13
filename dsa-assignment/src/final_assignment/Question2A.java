/*
You are the manager of a clothing manufacturing factory with a production line of super sewing machines. The
production line consists of n super sewing machines placed in a line. Initially, each sewing machine has a certain
number of dresses or is empty.
For each move, you can select any m (1 <= m <= n) consecutive sewing machines on the production line and pass
one dress from each selected sewing machine to its adjacent sewing machine simultaneously.
Your goal is to equalize the number of dresses in all the sewing machines on the production line. You need to
determine the minimum number of moves required to achieve this goal. If it is not possible to equalize the number
of dresses, return -1.
Input: [1,0,5]
Output: 2
Example 1:
Imagine you have a production line with the following number of dresses in each sewing machine: [1,0,5]. The
production line has 5 sewing machines.
Here's how the process works:
1. Initial state: [1,0,5]
2. Move 1: Pass one dress from the third sewing machine to the first sewing machine, resulting in [1,1,4]
3. Move 2: Pass one dress from the second sewing machine to the first sewing machine, and from third to
first sewing Machine [2,1,3]
4. Move 3: Pass one dress from the third sewing machine to the second sewing machine, resulting in [2,2,2]
After these 3 moves, the number of dresses in each sewing machine is equalized to 2. Therefore, the minimum
number of moves required to equalize the number of dresses is 3.
 */


package final_assignment;

public class Question2A {

    public static int minMovesToEqualize(int[] machines) {
        int totalDresses = 0;
        int numOfMachines = machines.length;

        // Calculate the total number of dresses in all machines
        for (int dresses : machines) {
            totalDresses += dresses;
        }

        // If the total number of dresses cannot be equally distributed among machines, return -1
        if (totalDresses % numOfMachines != 0) {
            return -1;
        }

        // Calculate the target number of dresses in each machine
        int target = totalDresses / numOfMachines;

        int moves = 0;
        int dressesToLeft = 0;
        int dressesToRight = 0;

        // Iterate through each machine
        for (int i = 0; i < numOfMachines; i++) {
            int diff = machines[i] - target;

            // Accumulate the dresses needed to move to the left and right of the current machine
            dressesToLeft += diff;
            dressesToRight -= diff;
            moves = Math.max(moves, Math.max(Math.abs(dressesToLeft), Math.abs(dressesToRight)));
        }

        return moves;
    }

    public static void main(String[] args) {
        int[] machines = {1, 0, 5};
        System.out.println(minMovesToEqualize(machines)); // Output:3
    }
}
