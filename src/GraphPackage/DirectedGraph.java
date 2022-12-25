package GraphPackage;

import java.util.Iterator;

import ADTPackage.*; // Classes that implement various ADTs

/**
 * A class that implements the ADT directed graph.
 *
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 * @version 5.1
 */
public class DirectedGraph<T> implements GraphInterface<T> {
    private final DictionaryInterface<T, VertexInterface<T>> vertices;
    private int edgeCount;

    public DirectedGraph() {
        vertices = new UnsortedLinkedDictionary<>();
        edgeCount = 0;
    } // end default constructor

    public boolean addVertex(T vertexLabel) {
        VertexInterface<T> addOutcome = vertices.add(vertexLabel, new Vertex<>(vertexLabel));
        return addOutcome == null; // Was addition to dictionary successful?
    } // end addVertex

    public boolean addEdge(T begin, T end, double edgeWeight) {
        boolean result = false;
        VertexInterface<T> beginVertex = vertices.getValue(begin);
        VertexInterface<T> endVertex = vertices.getValue(end);
        if ((beginVertex != null) && (endVertex != null))
            result = beginVertex.connect(endVertex, edgeWeight);
        if (result)
            edgeCount++;
        return result;
    } // end addEdge

    public boolean addEdge(T begin, T end) {
        return addEdge(begin, end, 0);
    } // end addEdge

    public boolean hasEdge(T begin, T end) {
        boolean found = false;
        VertexInterface<T> beginVertex = vertices.getValue(begin);
        VertexInterface<T> endVertex = vertices.getValue(end);
        if ((beginVertex != null) && (endVertex != null)) {
            Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
            while (!found && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (endVertex.equals(nextNeighbor))
                    found = true;
            } // end while
        } // end if

        return found;
    } // end hasEdge

    public boolean isEmpty() {
        return vertices.isEmpty();
    } // end isEmpty

    public void clear() {
        vertices.clear();
        edgeCount = 0;
    } // end clear

    public int getNumberOfVertices() {
        return vertices.getSize();
    } // end getNumberOfVertices

    public int getNumberOfEdges() {
        return edgeCount;
    } // end getNumberOfEdges

    protected void resetVertices() {
        Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
        while (vertexIterator.hasNext()) {
            VertexInterface<T> nextVertex = vertexIterator.next();
            nextVertex.unvisit();
            nextVertex.setCost(0);
            nextVertex.setPredecessor(null);
        } // end while
    } // end resetVertices

    public StackInterface<T> getTopologicalOrder() {
        resetVertices();

        StackInterface<T> vertexStack = new LinkedStack<>();
        int numberOfVertices = getNumberOfVertices();
        for (int counter = 1; counter <= numberOfVertices; counter++) {
            VertexInterface<T> nextVertex = findTerminal();
            nextVertex.visit();
            vertexStack.push(nextVertex.getLabel());
        } // end for

        return vertexStack;
    } // end getTopologicalOrder


    //###########################################################################
    public QueueInterface<T> getBreadthFirstSearch(T origin, T end) {
        resetVertices();
        QueueInterface<T> traversalOrder = new LinkedQueue<>();
        QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();
        VertexInterface<T> originVertex = vertices.getValue(origin);
        originVertex.visit();
        traversalOrder.enqueue(origin); // Enqueue vertex label
        vertexQueue.enqueue(originVertex); // Enqueue vertex
        while (!vertexQueue.isEmpty()) {
            VertexInterface<T> frontVertex = vertexQueue.dequeue();
            Iterator<VertexInterface<T>> neighbors =
                    frontVertex.getNeighborIterator();
            while (neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    traversalOrder.enqueue(nextNeighbor.getLabel());
                    vertexQueue.enqueue(nextNeighbor);
                } // end if
            } // end while
        } // end while
        return traversalOrder;
    }//end getBreadthFirstSearch
    //###########################################################################


    //###########################################################################
    /*   public QueueInterface<T> getDepthFirstSearch(T origin, T end)
     * 		return depth first search traversal order between origin vertex and end vertex
     */
    public QueueInterface<T> getDepthFirstSearch(T origin, T end) {
        resetVertices();
        QueueInterface<T> traversalOrder = new LinkedQueue<>();
        StackInterface<VertexInterface<T>> vertexStack = new LinkedStack<>();
        VertexInterface<T> originVertex = vertices.getValue(origin);
        originVertex.visit();
        traversalOrder.enqueue(origin); // Enqueue vertex label
        vertexStack.push(originVertex); // Enqueue vertex
        while (!vertexStack.isEmpty()) {
            VertexInterface<T> topVertex = vertexStack.peek();

            if (topVertex.getUnvisitedNeighbor() != null) {
                VertexInterface<T> nextNeighbor = topVertex.getUnvisitedNeighbor();
                nextNeighbor.visit();
                traversalOrder.enqueue(nextNeighbor.getLabel());
                vertexStack.push(nextNeighbor);
            } else
                vertexStack.pop();
        } // end while
        return traversalOrder;
    }// end getDepthFirstSearch
    //###########################################################################


    //###########################################################################
    public int getShortestPath(T begin, T end, StackInterface<T> path) {
        resetVertices();
        boolean done = false;
        QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();
        VertexInterface<T> originVertex = vertices.getValue(begin);
        VertexInterface<T> endVertex = vertices.getValue(end);
        originVertex.visit();
        // Assertion: resetVertices() has executed setCost(0)
        // and setPredecessor(null) for originVertex
        vertexQueue.enqueue(originVertex);
        while (!done && !vertexQueue.isEmpty()) {
            VertexInterface<T> frontVertex = vertexQueue.dequeue();
            Iterator<VertexInterface<T>> neighbors =
                    frontVertex.getNeighborIterator();
            while (!done && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    nextNeighbor.setCost(1 + frontVertex.getCost());
                    nextNeighbor.setPredecessor(frontVertex);
                    vertexQueue.enqueue(nextNeighbor);
                } // end if
                if (nextNeighbor.equals(endVertex))
                    done = true;
            } // end while
        } // end while
        // Traversal ends; construct shortest path
        int pathLength = (int) endVertex.getCost();
        path.push(endVertex.getLabel());
        VertexInterface<T> vertex = endVertex;
        while (vertex.hasPredecessor()) {
            vertex = vertex.getPredecessor();
            path.push(vertex.getLabel());
        } // end while
        return pathLength;
    }// end getShortestPath
    //###########################################################################


    //###########################################################################

    /**
     * Precondition: path is an empty stack (NOT null)
     */
    /* Use EntryPQ instead of Vertex in Priority Queue because multiple entries contain
     * 	the same vertex but different costs - cost of path to vertex is EntryPQ's priority value
     * public double getCheapestPath(T begin, T end, StackInterface<T> path)
     * 		return the cost of the cheapest path
     */
    public double getCheapestPath(T begin, T end, StackInterface<T> path) {
        boolean flag = false;
        PriorityQueueInterface<EntryPQ> vertexQueue = new HeapPriorityQueue<>();
        VertexInterface<T> originVertex = vertices.getValue(begin);
        VertexInterface<T> endVertex = vertices.getValue(end);
        vertexQueue.add(new EntryPQ(originVertex, 0, null));

        while (!flag && !vertexQueue.isEmpty()) {
            EntryPQ front = vertexQueue.remove();
            VertexInterface<T> frontVertex = front.vertex;

            if (!frontVertex.isVisited()) {
                frontVertex.visit();
                frontVertex.setCost(front.cost);
                frontVertex.setPredecessor(front.getPredecessor());

                if (frontVertex.equals(endVertex)) {
                    flag = true;
                } else {
                    Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
                    while (neighbors.hasNext()) {
                        VertexInterface<T> nextNeighbor = neighbors.next();
                        Iterator<Double> weights = nextNeighbor.getWeightIterator();
                        double weight = weights.next();

                        if (!nextNeighbor.isVisited()) {
                            double nextCost = weight + frontVertex.getCost();
                            vertexQueue.add(new EntryPQ(nextNeighbor, nextCost, frontVertex));
                        }
                    }
                }
            }
        }
        double pathCost = endVertex.getCost();
        path.push(endVertex.getLabel());
        VertexInterface<T> vertex = endVertex;

        while (vertex.hasPredecessor()) {
            vertex = vertex.getPredecessor();
            path.push(vertex.getLabel());
        }
        return pathCost;
    }
    //###########################################################################


    protected VertexInterface<T> findTerminal() {
        boolean found = false;
        VertexInterface<T> result = null;

        Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();

        while (!found && vertexIterator.hasNext()) {
            VertexInterface<T> nextVertex = vertexIterator.next();

            // If nextVertex is unvisited AND has only visited neighbors)
            if (!nextVertex.isVisited()) {
                if (nextVertex.getUnvisitedNeighbor() == null) {
                    found = true;
                    result = nextVertex;
                } // end if
            } // end if
        } // end while

        return result;
    } // end findTerminal

    // Used for testing

    public String[][] AdjacencyMatrix() { //Creates Adjacency Matrix for all vertices
        int size = vertices.getSize();
        String[][] array = new String[size][size];
        size--;
        Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
        while (vertexIterator.hasNext()) {
            String str = vertexIterator.next().getLabel().toString();
            array[0][size] = str;
            array[size][0] = str;
            size--;
        }//end while
        return array;
    }// end displayAdjMatrix

    public void displayEdges() {
        System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
        System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
        Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
        while (vertexIterator.hasNext()) {
            ((Vertex<T>) (vertexIterator.next())).display();
        } // end while
    } // end displayEdges

    public boolean isContainsVertex(T vertexLabel) { //Checks is the vertex contains or not
        return vertices.contains(vertexLabel);
    }

    private class EntryPQ implements Comparable<EntryPQ> {
        private final VertexInterface<T> vertex;
        private final VertexInterface<T> previousVertex;
        private final double cost; // cost to nextVertex

        private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex) {
            this.vertex = vertex;
            this.previousVertex = previousVertex;
            this.cost = cost;
        } // end constructor

        public VertexInterface<T> getVertex() {
            return vertex;
        } // end getVertex

        public VertexInterface<T> getPredecessor() {
            return previousVertex;
        } // end getPredecessor

        public double getCost() {
            return cost;
        } // end getCost

        public int compareTo(EntryPQ otherEntry) {
            // Using opposite of reality since our priority queue uses a maxHeap;
            // could revise using a minheap
            return (int) Math.signum(otherEntry.cost - cost);
        } // end compareTo

        public String toString() {
            return vertex.toString() + " " + cost;
        } // end toString
    } // end EntryPQ
} // end DirectedGraph
