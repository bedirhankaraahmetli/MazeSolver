package GraphPackage;

import ADTPackage.LinkedStack;
import ADTPackage.QueueInterface;
import ADTPackage.StackInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("C:\\Users\\bedoo\\Desktop\\JavaProjects\\MazeSolver\\src\\maze1.txt");
        Scanner scanner = new Scanner(file);
        Scanner sc = new Scanner(file);
        UndirectedGraph<String> graph = new UndirectedGraph<>();

        int row = 1;
        String beginVertex = null;
        String finishVertex = null;
        Random random = new Random();
        int column = sc.nextLine().length();
        while (sc.hasNext()) {
            sc.nextLine();
            ++row;
        }
        char[][] maze = new char[row][column];
        row = 0;
        while (scanner.hasNext()) { //Creating graph with respect to maze
            String line = scanner.nextLine();
            double rnd = random.nextDouble(1, 4);
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == ' ') {
                    maze[row][i] = ' ';
                    String vertexName = row + "-" + i;
                    graph.addVertex(vertexName);
                    if (i >= 1 && line.charAt(i - 1) == ' ') {
                        String oldVertexName = row + "-" + (i - 1);
                        graph.addEdge(oldVertexName, vertexName, Math.round(rnd));
                    }
                    String oldVertexName = (row - 1) + "-" + i;
                    if (row >= 1 && graph.isContainsVertex(oldVertexName)) {
                        graph.addEdge(vertexName, oldVertexName, Math.round(rnd));
                    }
                    if (row == 0) {
                        beginVertex = vertexName;
                    }
                    if (i == line.length() - 1) {
                        finishVertex = vertexName;
                    }
                } else
                    maze[row][i] = '#';
            }
            row++;
        }
        StackInterface<String> cheapestPathStack = new LinkedStack<>();
        double cost = graph.getCheapestPath(beginVertex, finishVertex, cheapestPathStack);
        QueueInterface<String> bfsQueue = graph.getBreadthFirstSearch(beginVertex, finishVertex);
        QueueInterface<String> bfsQueueForEmpty = graph.getBreadthFirstSearch(beginVertex, finishVertex);
        QueueInterface<String> dfsQueue = graph.getDepthFirstSearch(beginVertex, finishVertex);
        QueueInterface<String> dfsQueueForEmpty = graph.getDepthFirstSearch(beginVertex, finishVertex);
        StackInterface<String> shortestPathStack = new LinkedStack<>();
        graph.getShortestPath(beginVertex, finishVertex, shortestPathStack);
        StackInterface<String> shortestPathStackForEmpty = new LinkedStack<>();
        graph.getShortestPath(beginVertex, finishVertex, shortestPathStackForEmpty);

        //-------------------Adjacency List-----------------------
        System.out.println("Adjacency Lists of Each Vertex of the Graph After Maze to Graph Operation");
        graph.displayEdges();

        //-------------------Adjacency Matrix---------------------
        System.out.println("-------------------------------------------------------------");
        System.out.println("Adjacency Matrix of the Graph After Maze to Graph Operation");
        String[][] array = graph.AdjacencyMatrix();
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[i].length; ++j) {
                if (graph.hasEdge(array[i][0], array[0][j]) && i > 0 && j > 0) {
                    array[i][j] = " 1 ";
                }
            }
        }
        System.out.print("     ");
        for (String[] i : array) {
            for (String j : i) {
                if (j != null)
                    System.out.print("(" + j + ")");
                else
                    System.out.print("     ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------------------------");
        System.out.println("The number of edges found: " + graph.getNumberOfEdges());
        System.out.println("-------------------------------------------------------------");

        // ------------------ BFS ----------------------
        System.out.println("BFS output between the starting and the end points of the maze");
        System.out.println("\nStarting Point: " + beginVertex);
        System.out.println("Ending Point: " + finishVertex);
        System.out.println();

        int visitedVertexForBFS = 0;
        while (!bfsQueue.isEmpty()) {
            String str = bfsQueue.dequeue();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = '.';
            ++visitedVertexForBFS;
            if (str.equals(finishVertex))
                break;
        }
        for (char[] value : maze) {
            for (char c : value) {
                System.out.print(c);
            }
            System.out.println();
        }

        while (!bfsQueueForEmpty.isEmpty()) {
            String str = bfsQueueForEmpty.dequeue();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = ' ';
        }

        System.out.println("\nThe number of visited vertices for BFS: " + visitedVertexForBFS);

        // -------------- DFS ---------------------
        System.out.println("-------------------------------------------------------------");
        System.out.println("DFS output between the starting and the end points of the maze");
        System.out.println("\nStarting Point: " + beginVertex);
        System.out.println("Ending Point: " + finishVertex);
        int visitedVertexForDFS = 0;
        while (!dfsQueue.isEmpty()) {
            String str = dfsQueue.dequeue();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = '.';
            ++visitedVertexForDFS;
            if (str.equals(finishVertex))
                break;
        }
        System.out.println();
        for (char[] value : maze) {
            for (char c : value) {
                System.out.print(c);
            }
            System.out.println();
        }

        while (!dfsQueueForEmpty.isEmpty()) {
            String str = dfsQueueForEmpty.dequeue();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = ' ';
        }
        System.out.println("\nThe number of visited vertices for DFS: " + visitedVertexForDFS);

        // ------------- SHORTEST PATH -------------
        System.out.println("-------------------------------------------------------------");
        System.out.println("Shortest path between the starting and the end points of the maze");
        System.out.println("\nStarting Point: " + beginVertex);
        System.out.println("Ending Point: " + finishVertex);
        System.out.println();

        int visitedVertexForShortestPath = 0;

        while (!shortestPathStack.isEmpty()) {
            String str = shortestPathStack.peek();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = '.';
            ++visitedVertexForShortestPath;
            shortestPathStack.pop();
            if (str.equals(finishVertex)) {
                break;
            }
        }
        for (char[] chars : maze) {
            for (char aChar : chars) {
                System.out.print(aChar);
            }
            System.out.println();
        }

        while (!shortestPathStackForEmpty.isEmpty()) {
            String str = shortestPathStackForEmpty.peek();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = ' ';
            shortestPathStackForEmpty.pop();

        }
        System.out.println("\nThe number of visited vertices for Shortest Path: " + visitedVertexForShortestPath);

        // ------------- CHEAPEST PATH -------------
        System.out.println("-------------------------------------------------------------");
        System.out.println("The cheapest path for the Weighted Graph");
        System.out.println("\nStarting Point: " + beginVertex);
        System.out.println("Ending Point: " + finishVertex);
        System.out.println();
        int visitedVertexForCheapestPath = 0;
        while (!cheapestPathStack.isEmpty()) {
            String str = cheapestPathStack.peek();
            String[] index = str.split("-");
            maze[Integer.parseInt(index[0])][Integer.parseInt(index[1])] = '.';
            ++visitedVertexForCheapestPath;
            cheapestPathStack.pop();
            if (str.equals(finishVertex)) {
                break;
            }
        }
        for (char[] chars : maze) {
            for (char aChar : chars) {
                System.out.print(aChar);
            }
            System.out.println();
        }
        System.out.println("\nThe number of visited vertices for the Weighted Graph:" + visitedVertexForCheapestPath);
        System.out.println("\nThe cost of the cheapest path: " + cost);

    }
}
