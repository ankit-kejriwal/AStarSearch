/**
* Team members
* Ankit Kejriwal <akejriw1@uncc.edu>
* Kanika Saini   <ksaini1@uncc.edu>
* Sindhura Sadam <jsadam@uncc.edu>
* This class takes the input and display solutions for both Manhathhan  and Misplaced tiles
*/
import java.util.*;

public class AStar {
    private static String MANHATTAN_HEURISTIC = "MANHATTAN DISTANCE HEURISTIC";
    private static String MISPLACED_HEURISTIC = "Misplaced Tiles Heuristic";

    public String[] hType ={MANHATTAN_HEURISTIC,MISPLACED_HEURISTIC};
    public PriorityQueue<Node> priorityQueue;
    public int countOfNodesExpanded;
    public int countOfNodesGenerated;
    public int stateId;
    public HashMap<Integer, Node> exploredNodeList;


    public  static  void main(String[] args){
        AStar astar = new AStar();
        Node initialState, goalState;
        // input from the user
        System.out.println("Please enter the initial state row-wise with each input separated by the enter");
        initialState = initializeNode();
        System.out.println("Please enter the final state row-wise with each input separated by the enter");
        goalState = initializeNode();
        // validate the input
        if(checkInput(initialState.getState()) && checkInput(goalState.getState())){
            astar.startProcess(initialState,goalState);
        } else {
            return;
        }
    }

    /**
     * Takes a node state as an input and returns true if it is a valid input else false.
     * @param node is a two dimensional integer array which represents the state of the board.
     * @return a boolean true if input is valid and false if it is invalid.
     */
    private static boolean checkInput(int[][] node) {
        ArrayList<Integer> validEntry = new ArrayList<Integer>();
        for(int i =0;i<9;i++){
            validEntry.add(i);
        }
        for(int i=0;i<node.length;i++){
            for(int j=0;j<node.length;j++){
                if(validEntry.contains(node[i][j])){
                    validEntry.remove((Integer) node[i][j]);
                } else {
                    System.out.println("Entered values are not unique");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the input from the user and create a new node
     * @return a new generated node
     */
    private static Node initializeNode(){
        int[][] node = new int[3][3];
        Scanner scanner = new Scanner(System.in);
        for(int i=0;i<node.length;i++){
            for(int j=0;j<node.length;j++){
                node[i][j] = scanner.nextInt();
            }
        }
        Node newNode = new Node();
        newNode.setState(node);
        return newNode;
    }

    /**
     * Takes the initial and the goal node and display the possible solutions
     * @param initialState Node representing initial state
     * @param goalState representing final state
     */
    private void startProcess(Node initialState, Node goalState){
        for(int i=0;i<hType.length;i++) {
            System.out.println("************ A* search algorithm for " +hType[i] + " *********");
            countOfNodesExpanded = 0;
            countOfNodesGenerated = 1;
            stateId = 1;
            priorityQueue = new PriorityQueue<Node>(100, new FCostComparator());
            exploredNodeList = new HashMap<Integer, Node>();
            initialState.setgCost(0);
            initialState.setId(stateId++);
            initialState.setParentId(0);
            getHeuristic(initialState,goalState,hType[i]);
            priorityQueue.add(initialState);

            while (!priorityQueue.isEmpty()){

                Node dequeue = priorityQueue.poll();
                exploredNodeList.put(dequeue.getId(),dequeue);
                countOfNodesExpanded++;
                // check if goal state is reached. For goal node hcost will be zero
                if(dequeue.gethCost() == 0) {
                    Stack<Node> solutionPath = new Stack<Node>();
                    solutionPath.push(dequeue);
                    Node currentNode = dequeue;
                    int parentId = currentNode.getParentId();

                    while (parentId !=0) {
                        solutionPath.push(exploredNodeList.get(parentId));
                        currentNode = exploredNodeList.get(parentId);
                        parentId = currentNode.getParentId();
                    }

                    // Print Solution path using nodes from the backtrack solution stack.
                    System.out.println("------------ Printing Solution Path --------------");
                    int pathCost = solutionPath.size() - 1;
                    printSolutionPath(solutionPath);
                    System.out.println();
                    System.out.println("Nodes generated: " + countOfNodesGenerated);
                    System.out.println("Nodes explored: " + exploredNodeList.size());
                    System.out.println("Path Cost: " + pathCost);
                    System.out.println();
                    break;
                }

                // Finding the free space
                String indexOfFreeTile = findingFreeTileLocation(dequeue);
                String[] temp = indexOfFreeTile.split(",");
                int rowValue = Integer.parseInt(temp[0]);
                int colValue = Integer.parseInt(temp[1]);

                // find possible state based on the free location

                List<String> possibleStates = findPossibleState(dequeue,rowValue,colValue);

                for(int k=0;k<possibleStates.size();k++){
                    Node childNode = createChildNode(dequeue,rowValue,colValue,possibleStates.get(k));
                    childNode.setId(stateId++);
                    countOfNodesGenerated++;
                    boolean isChildInExploredSet = isChildInExploredSet(exploredNodeList, childNode);
                    boolean isChildInPriorityQueue = isChildInPriorityQueue(priorityQueue, childNode);

                    if (!isChildInExploredSet && !isChildInPriorityQueue) {
                        // if yes do nothing if no calculate heuristic function for child
                        getHeuristic(childNode, goalState, hType[i]);
                        // insert child in queue
                        priorityQueue.add(childNode);
                    }

                }


            }

        }
    }

    /**
     * Takes a initial node and goal node and calculate heuristic value
     * @param initial is a node representing the initial state.
     * @param goal is a node representing the goal state.
     * @param type represents the type of heuristic that needs to be calculated
     */

    private void getHeuristic(Node initial, Node goal,String type){
        if(type.equalsIgnoreCase(MANHATTAN_HEURISTIC)){
            int manhattanCost = manhattanDistanceHeuristic(initial, goal);
            initial.sethCost(manhattanCost);
            initial.setfCost(initial.getgCost() + initial.gethCost());
        } else {
            int misplacedTilesCost = misplacedTilesHeuristic(initial, goal);
            initial.sethCost(misplacedTilesCost);
            initial.setfCost(initial.getgCost() + initial.gethCost());
        }
    }

    /**
     * Takes a initial node and goal node and calculate Manhatthan heuristic value
     * @param initial is a node representing the initial state.
     * @param goal is a node representing the goal state.
     * @return a Manhatthan heuristic value for the given nodes
     */

    public int manhattanDistanceHeuristic(Node initial, Node goal) {
        int[][] initialState = initial.getState();
        int[][] goalState = goal.getState();
        int hCost = 0;
        int i = 0, j = 0, l = 0, m = 0;
        for (int a = 1; a <= 8; a++) {
            outerLoop: for (i = 0; i < initialState.length; i++)
                for (j = 0; j < initialState.length; j++)
                    if (initialState[i][j] == a)
                        break outerLoop;
            outerLoop1: for (l = 0; l < goalState.length; l++)
                for (m = 0; m < goalState.length; m++)
                    if (goalState[l][m] == a)
                        break outerLoop1;
            hCost = hCost + (Math.abs(i - l) + Math.abs(j - m));
        }
        return hCost;
    }

    /**
     * Takes a initial node and goal node and calculate Manhatthan heuristic value
     * @param initial is a Node representing the initial state.
     * @param goal is a Node representing the goal state.
     * @return a Misplaced heuristic value for the given nodes
     */

    public int misplacedTilesHeuristic(Node initial, Node goal) {
        int[][] initialState = initial.getState();
        int[][] goalState = goal.getState();
        int hCost = 0;
        for (int i = 0; i < initialState.length; i++)
            for (int j = 0; j < initialState.length; j++) {
                if (initialState[i][j] != goalState[i][j] && initialState[i][j] > 0) {
                    hCost++;
                }
            }
        return hCost;
    }

    /**
     * Takes a solution path and print it
     * @param solutionPath is a stack representing the solution of a A* search problem.
     */

    private void printSolutionPath(Stack<Node> solutionPath){
        Node lastNode;
        while (!solutionPath.isEmpty()) {
            lastNode = solutionPath.pop();
            displayPuzzle(lastNode);
            if(!solutionPath.isEmpty()) {
                System.out.println("\n Next state: ");
            }
        }
    }

    /**
     * Takes a node as an input and display it in matrix form along with g(n), h(n) anf f(n)
     * @param node is representing a Node to be represented in matrix form
     */

    private void displayPuzzle(Node node){
        int[][] nodeState = node.getState();
        // Print matrix
        System.out.println("g(n)= " + node.getgCost() + "\t h(n)= " + node.gethCost()
                + "\t f(n)= " + node.getfCost());
        for (int i = 0; i < nodeState.length; i++) {
            for (int j = 0; j < nodeState.length; j++)
                System.out.print("\t" + nodeState[i][j]);
            System.out.println();
        }

    }

    /**
     * Takes a node as an input and return the position of the free space in string formated separated by commas
     * @param node is representing a Node
     */

    private String findingFreeTileLocation(Node node){
        int[][] currentState = node.getState();
        int i = 0, j = 0;
        outerLoop: for (i = 0; i < currentState.length; i++) {
            for (j = 0; j < currentState.length; j++) {
                if (currentState[i][j] == 0)
                    break outerLoop;
            }
        }
        String indexOfFreeTile = i + "," +j;
        return indexOfFreeTile;
    }

    /**
     * It computes the possible position for the free tile
     * @param initiaNode Node representing the current state of the board.
     * @param rowValue Row index of the free tile
     * @param columnValue Column index of the free tile.
     * @return a list of Strings representing comma separated indexes of valid possible free tile positions.
     */


    private List<String> findPossibleState(Node initiaNode, int rowValue, int columnValue) {
        int[][] initialState = initiaNode.getState();
        List<String> possiblePositionsForExpansion = new ArrayList<String>();
        // DOWN
        if ((rowValue + 1) < initialState.length)
            possiblePositionsForExpansion
                    .add(Integer.toString(rowValue + 1) + "," + Integer.toString(columnValue));
        // UP
        if ((rowValue - 1) >= 0)
            possiblePositionsForExpansion
                    .add(Integer.toString(rowValue - 1) + "," + Integer.toString(columnValue));
        // RIGHT
        if ((columnValue + 1) < initialState.length)
            possiblePositionsForExpansion
                    .add(Integer.toString(rowValue) + "," + Integer.toString(columnValue + 1));
        // LEFT
        if ((columnValue - 1) >= 0)
            possiblePositionsForExpansion
                    .add(Integer.toString(rowValue) + "," + Integer.toString(columnValue - 1));
        return possiblePositionsForExpansion;
    }

    /**
     * it creates a new node for based on the position of free tile
     * @param dequeueNode Node representing the parent Node
     * @param rowVal Row index of the free tile
     * @param colVal Column index of the free tile.
     * @param possiblePositionsForExpansion a String representing comma separated indexes of the next valid possible free tile positions.
     * @return Node representing the child state
     */

    private Node createChildNode(Node dequeueNode, int rowVal, int colVal, String possiblePositionsForExpansion) {
        // copying state of parent
        Node childNode = new Node();
        int[][] childNodeState = new int[3][3];

        // For each Child clone the current state and replace changed tiles
        for (int i = 0; i < dequeueNode.getState().length; i++)
            for (int j = 0; j < dequeueNode.getState().length; j++)
                childNodeState[i][j] = dequeueNode.getState()[i][j];

        String[] temp = possiblePositionsForExpansion.split(",");
        int newRow = Integer.parseInt(temp[0]);
        int newColumn = Integer.parseInt(temp[1]);

        // move empty tile to new position
        int swapNum = childNodeState[rowVal][colVal];
        childNodeState[rowVal][colVal] = childNodeState[newRow][newColumn];
        childNodeState[newRow][newColumn] = swapNum;

        childNode.setState(childNodeState);
        childNode.setgCost(dequeueNode.getgCost() + 1);
        childNode.setParentId(dequeueNode.getId());
        return childNode;
    }

    /**
     * It checks whether the child node is in explored set
     * @param exploredNodeSet list of Nodes that have been explored already
     * @param  childStateNode Node to checked
     * @return true if child node is in explored set else false
     */

    private boolean isChildInExploredSet(HashMap<Integer, Node> exploredNodeSet, Node childStateNode) {
        for (Map.Entry<Integer, Node> entry : exploredNodeSet.entrySet()) {
            Node exploredNode = (Node) entry.getValue();
            if (compareNodesForEquality(childStateNode, exploredNode))
                return true;
        }
        return false;
    }

    /**
     * It checks whether the child node is in priority queue
     * @param priorityQueue list of Nodes that are in Priority Queue
     * @param  childStateNode Node to checked
     * @return true if child node is in priority queue  else false
     */

    private boolean isChildInPriorityQueue(PriorityQueue<Node> priorityQueue, Node childStateNode) {
        List<Node> pqList = new ArrayList<Node>(priorityQueue);
        for (int i = 0; i < pqList.size(); i++) {
            if (compareNodesForEquality(pqList.get(i), childStateNode))
                return true;
        }
        return false;
    }

    /** It compares two nodes for equality
     * @param first
     * @param second
     * @return true if nodes are equal otherwise false
     */
    private boolean compareNodesForEquality(Node first, Node second) {
        int[][] firstState = first.getState();
        int[][] secondState = second.getState();
        for (int i = 0; i < firstState.length; i++)
            for (int j = 0; j < firstState.length; j++)
                if (firstState[i][j] != secondState[i][j])
                    return false;
        return true;

    }


}