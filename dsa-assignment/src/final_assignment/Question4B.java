/*
You are provided with balanced binary tree with the target value k. return x number of values that are closest to the
given target k. provide solution in O(n)
Note: You have only one set of unique values x in binary search tree that are closest to the target.
Input:
K=3.8
x=2
Output: 3,4
 */

package final_assignment;

import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

public class Question4B {
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<>();
        TreeNode curr = root;

        // Perform in-order traversal
        while (curr != null || !stack.isEmpty()) {
            // Traverse left subtree and store nodes along the way
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            // Pop the top node from the stack
            curr = stack.pop();
            // Check if result list size is less than k
            if (result.size() < k) {
                result.add(curr.val);
            } else if (Math.abs(curr.val - target) < Math.abs(result.get(0) - target)) {
                // If the current node is closer to target then the farthest value in result, replace it
                result.remove(0);
                result.add(curr.val);
            } else {
                // If the current node is farther from target, break the loop
                break;
            }
            // Move to the right subtree
            curr = curr.right;
        }

        return result;
    }

    public static void main(String[] args) {
        // Construct the example binary search tree
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        Question4B solution = new Question4B();
        double target = 3.8;
        int x = 2;
        List<Integer> closestValues = solution.closestKValues(root, target, x);
        System.out.println("Closest values to " + target + " are: " + closestValues); //Output:3,4
    }
}
