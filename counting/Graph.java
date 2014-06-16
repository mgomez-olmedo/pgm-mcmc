/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;

/**
 * Class for representing and managing graphs
 * @author mgomez, jmpeña
 */
public class Graph implements Serializable{

   /**
    * Constants for links
    */
   final static int NONE = 0;
   final static int DIRECTED = 1;
   final static int UNDIRECTED = 2;
   /**
    * Data member to store the number of variables
    */
   private int numberVariables;
   /**
    * Data member to keep a reference to the memory space reserved for
    * representing the graph
    */
   private int graph[][];
   /**
    * Data member to store the ancestral order of the nodes
    */
   private int order[];
   
   /**
    * Data member to store if the graph admits and oriented
    * extension (is a PDAG)
    */
   private boolean pDag;
   
   /**
    * Data member to store the number of components. This data member is set in
    * computeOrder method
    */
   private int numberComponents;
   /**
    * Stores the nodes related to the last operation
    */
   private int lastX, lastY, lastZ;
   /**
    * Stores the number of arcs and links
    */
   private int numberArcs, numberLinks;
   /**
    * Stores the code of the last operation
    */
   private Operations lastOperation;

   /**
    * Class constructor
    *
    * @param numVariables
    */
   public Graph(int numVariables) {
      // Sets the number of variables
      this.numberVariables = numVariables;

      // Gets space for rows pointers
      graph = new int[numVariables][numVariables];

      // Gets space for the rows
      for (int i = 0; i < numVariables; i++) {
         // Sets all the cells to 0
         for (int j = 0; j < numVariables; j++) {
            graph[i][j] = NONE;
         }
      }

      // Gets space for order
      order = null;

      // Initialize data members
      lastX = -1;
      lastY = -1;
      lastOperation = Operations.NOOP;

      // Initialize the number of arcs and links
      numberArcs = 0;
      numberLinks = 0;
   }
   
   /**
    *Copy constructor
    * @param originalGraph
    */
   public Graph(Graph originalGraph){
      // Sets the number of variables
      this.numberVariables = originalGraph.numberVariables;

      // Gets space for rows pointers
      graph = new int[numberVariables][numberVariables];

      // Gets space for the rows
      for (int i = 0; i < numberVariables; i++) {
         // Sets all the cells to 0
         for (int j = 0; j < numberVariables; j++) {
            graph[i][j] = originalGraph.graph[i][j];
         }
      }

      // Gets space for order
      order = null;

      // Initialize data members
      lastX = -1;
      lastY = -1;
      lastOperation = Operations.NOOP;

      // Initialize the number of arcs and links
      numberArcs = 0;
      numberLinks = 0;   
   }

   /**
    * Method to check if an arc can be added in a certain position
    *
    * @param x
    * @param y
    * @return
    */
   public boolean isAddArcPossible(int x, int y) {
      return (x != y && graph[x][y] == NONE && graph[y][x] == NONE);
   }
   
   /**
    * Adds and arc between two nodes
    *
    * @param parent
    * @param child
    */
   public void addArc(int x, int y) {
      // Perform the change
      graph[x][y] = DIRECTED;

      // Stores the nodes of the last operation
      lastX = x;
      lastY = y;
      lastOperation = Operations.ADDARC;
   }

   /**
    * Method to check if a link can be added in a certain position
    *
    * @param x
    * @param y
    * @return
    */
   public boolean isDeleteArcPossible(int x, int y) {
      return (x != y && graph[x][y] == DIRECTED);
   }

   
   /**
    * Deletes an arc between two nodes
    *
    * @param parent
    * @param child
    */
   public  void deleteArc(int x, int y) {
      // Perform the change
      graph[x][y] = NONE;

      // Stores the nodes and the last operation
      lastX = x;
      lastY = y;
      lastOperation = Operations.DELETEARC;
   }
   
   /**
    * Method to check if a link can be added in a certain position
    *
    * @param x
    * @param y
    * @return
    */
   public boolean isAddLinkPossible(int x, int y) {
      return (x != y && graph[x][y] == NONE && graph[y][x] == NONE);
   }
   
   /**
    * Adds an undirected links between two nodes. First checks there is not a
    * link between them
    *
    * @param x first node
    * @param y second node
    */
   public void addLink(int x, int y) {
      // Perform the changes
      graph[x][y] = UNDIRECTED;
      graph[y][x] = UNDIRECTED;

      // Stores the last operation
      lastX = x;
      lastY = y;
      lastOperation = Operations.ADDLINK;
   }   

   /**
    * Method to check if a link can be added in a certain position
    *
    * @param x
    * @param y
    * @return
    */
   public boolean isDeleteLinkPossible(int x, int y) {
      return (x != y && graph[x][y] == UNDIRECTED);
   }
   
   /**
    * Removes an undirected link between two nodes
    *
    * @param x first node
    * @param y second node
    */
   public void deleteLink(int x, int y) {
      if (graph[x][y] == UNDIRECTED) {
         // Perform the operation
         graph[x][y] = NONE;
         graph[y][x] = NONE;

         // Stores the last operation data
         lastX = x;
         lastY = y;
         lastOperation = Operations.DELETELINK;
      }
   }

   /**
    * Method to check if a link can be added in a certain position
    *
    * @param x
    * @param y
    * @param z
    * @return
    */
   public boolean isAddVStructurePossible(int x, int y, int z) {
      return (x != y && x != z && y != z && graph[x][y] == NONE && graph[y][x] == NONE
              && graph[x][z] == NONE && graph[z][x] == NONE
              && graph[y][z] == NONE && graph[z][y] == NONE);
   }
   
   /**
    * Adds a v-structure having z as head to head node
    *
    * @param x
    * @param y
    * @param z
    */
   public void addVStructure(int x, int y, int z) {
      // Perform the changes
      graph[x][z] = DIRECTED;
      graph[y][z] = DIRECTED;

      // Store the last operation data
      lastX = x;
      lastY = y;
      lastZ = z;
      lastOperation = Operations.ADDVSTRUCTURE;
   }
   
   /**
    * Method to check if a link can be added in a certain position
    *
    * @param x
    * @param y
    * @param z
    * @return
    */
   public boolean isDeleteVStructurePossible(int x, int y, int z) {
      return (x != y && x != z && y != z && graph[x][y] == NONE && graph[y][x] == NONE
              && graph[x][z] == DIRECTED && graph[y][z] == DIRECTED);
   }   
   
   /**
    * Deletes a v-structure having z as head to head node
    *
    * @param x
    * @param y
    * @param z
    */
   public void deleteVStructure(int x, int y, int z) {
      // Perform the changes
      graph[x][z] = NONE;
      graph[y][z] = NONE;

      // Store the last operation data
      lastX = x;
      lastY = y;
      lastZ = z;
      lastOperation = Operations.DELETEVSTRUCTURE;
   }   
   
   /**
    * Undo the last operation
    */
   public void undo() {
      switch (lastOperation) {
         case EQUAL:
         case NOOP:
            break;

         case ADDARC:
            graph[lastX][lastY] = NONE;
            break;

         case DELETEARC:
            graph[lastX][lastY] = DIRECTED;
            break;

         case ADDLINK:
            graph[lastX][lastY] = NONE;
            graph[lastY][lastX] = NONE;
            break;

         case DELETELINK:
            graph[lastX][lastY] = UNDIRECTED;
            graph[lastY][lastX] = UNDIRECTED;
            break;

         case ADDVSTRUCTURE:
            graph[lastX][lastZ] = NONE;
            graph[lastY][lastZ] = NONE;
            break;

         case DELETEVSTRUCTURE:
            graph[lastX][lastZ] = DIRECTED;
            graph[lastY][lastZ] = DIRECTED;
            break;
      }
   }   
   
   /**
    * Checks the graph
    *
    * @return result of the check
    */
   public boolean check() {
      boolean ok = true;

      // Check if there are flags
      ok = !containsFlag();

      // If no flags are detected, keeps on testing
      if (ok) {
         // Compute the ancestral order
         ok = (computeOrder() != -1);

         if (ok) {
            // Creates a new graph removing directed arrows
            Graph newGraph = removesArcs();

            // Creates a pdag from this new graph
            Graph pdag = newGraph.cpdagToDag();

            // Keeps on working if it is a pdag
            ok = pdag.pDag;

            if (ok) {
               ok = containsStronglyProtectedArrows();
            }
         }
      }

      // return ok
      return ok;
   }

   /**
    * Tests ig the graph contains a single connected component
    *
    * @return result of the check
    */
   public boolean containsSingleComponent() {
      int marked[] = new int[numberVariables];
      int visited = 1;

      // Initialize all the positions to 0
      for (int i = 0; i < numberVariables; i++) {
         marked[i] = 0;
      }

      // Begins labelling node 0 as marked
      marked[0] = 1;

      // Considers all the nodes
      boolean stop = false;
      while (!stop) {
         stop = true;
         for (int i = 0; i < numberVariables - 1; i++) {
            for (int j = i + 1; j < numberVariables; j++) {
               if ( // Required to consider a visited node
                       (marked[i] == 1 || marked[j] == 1)
                       && // And to consider some non-visited node
                       (marked[i] == 0 || marked[j] == 0)
                       && // Required a link between  both nodes
                       (graph[i][j] != NONE || graph[j][i] != NONE)) {
                  marked[i] = 1;
                  marked[j] = 1;
                  visited++;
                  stop = false;
               }
            }
         }
      }

      // Return true if the previous loop finished with stop
      // equals to false
      return (!(visited < numberVariables));
   }

   /**
    * Tests if the graph do not contains undirected links
    *
    * @return
    */
   public boolean doNotContainsUndirectedLinks() {
      boolean doNotContains = true;
      boolean stop = false;

      // Check for undirected links
      for (int i = 0; i < numberVariables - 1 && !stop; i++) {
         for (int j = i + 1; j < numberVariables && !stop; j++) {
            if (graph[i][j] == UNDIRECTED) {
               doNotContains = false;
               stop = true;
            }
         }
      }

      // return the result
      return doNotContains;
   }

   /**
    * Gets graph data member
    *
    * @return graph
    */
   public int[][] getGraph() {
      return graph;
   }

   /**
    * Gets the number of arcs
    *
    * @return number of arcs
    */
   public int getNumberArcs() {
      return numberArcs;
   }

   /**
    * Gets the number of links
    *
    * @return
    */
   public int getNumberLinks() {
      return numberLinks;
   }
   
   /**
    * Gets the number of variables
    * @return 
    */
   public int getNumberVariables(){
      return numberVariables;
   }

   /**
    * Print information about the graph. Only for debugging purposes
    */
   public void print() {
      System.out.println("------------------ PRINT GRAPH ----------------------");
      for (int i = 0; i < numberVariables; i++) {
         for (int j = 0; j < numberVariables; j++) {
            System.out.print(graph[i][j] + "  ");
         }
         System.out.println();
      }
      System.out.println("-----------------------------------------------------");
   }

   /**
    * Print information about the graph. Only for debugging purposes
    */
   public void printOrder() {
      for (int i = 0; i < numberVariables; i++) {
         System.out.print(order[i] + " ");;
      }
      System.out.println();
   }

   /**
    * Counts the numbers of arcs and links.The count of the
    * links must be done on the upper part of the matrix to
    * avoid a double count
    */
   public void countArcsLinks() {
      for (int i = 0; i < numberVariables; i++) {
         for (int j = 0; j < numberVariables; j++) {
            switch (graph[i][j]) {
               case DIRECTED:
                  numberArcs++;
                  break;
               case UNDIRECTED:
                  if (j > i){
                     numberLinks++;
                  }
                  break;
            }
         }
      }
   }
   
   /**
    *Method to check if two nodes x and y are neighbor: there is a undirected link
    * between them
    * @param x
    * @param y
    * @return
    */
   public boolean isNeighbor(int x, int y){
      return (graph[x][y] == UNDIRECTED);
   }
   
   /**
    * Method for removing the directed links of a graph, producing a new one
    * @return graph
    */
   public void removeDirectedLinks(){      
      // Now remove al the directed links
      for(int i=0; i < numberVariables; i++){
         for(int j=0; j < numberVariables; j++){
            if (graph[i][j] == DIRECTED){
               graph[i][j]=NONE;
            }
         }
      }
   }

//////////////////////////////// PRIVATE METHODS ///////////////////////////
   /**
    * Computes the ancestral order of the nodes and computes the number of
    * connected components int the graph. The method modifies the order data
    * member, as well as numberComponents.
    *
    * @return the number of components
    */
   private int computeOrder() {
      // Gets space for order data member
      order = new int[numberVariables];

      // Sets initial values
      for (int i = 0; i < numberVariables; i++) {
         order[i] = 0;
      }

      // Makes the loop
      numberComponents = 0;
      boolean stopLoop = false;
      boolean stopFor = false;

      // Proceed with the nodes
      while (!stopLoop) {
         stopLoop = true;

         // Consider nodes
         for (int i = 0; i < numberVariables && !stopFor; i++) {
            if (order[i] == numberComponents) {
               for (int j = 0; j < numberVariables && !stopFor; j++) {
                  if (graph[i][j] == DIRECTED) {
                     // If the max order is not overcomed, set the order
                     if (numberComponents < (numberVariables - 1)) {
                        order[j] = (numberComponents + 1);
                        stopLoop = false;
                     } else {
                        stopFor = true;
                        numberComponents = 0;
                     }
                  }
               }
            }
         }

         // Add one to actualOrder
         if (!stopFor) {
            numberComponents++;
         }
      }

      // Return the value of stop
      return numberComponents - 1;
   }

   /**
    * Checks if the graph contains flags
    *
    * @return
    */
   private boolean containsFlag() {
      boolean ok = false;

      for (int i = 0; i < numberVariables && !ok; i++) {
         for (int j = 0; j < numberVariables && !ok; j++) {
            if (graph[i][j] == DIRECTED) {
               for (int k = 0; k < numberVariables && !ok; k++) {
                  if (graph[j][k] == UNDIRECTED && graph[i][k] != DIRECTED) {
                     ok = true;
                  }
               }
            }
         }
      }

      // Return the result
      return ok;
   }

   /**
    * Checks if the graph contains strongly protected arrows
    *
    * @return
    */
   private boolean containsStronglyProtectedArrows() {
      boolean protectedArrow = true;
      boolean stopExternalLoop = false, stopInternalLoop = false;

      // Consider every variable
      for (int i = 0; i < numberVariables && !stopExternalLoop; i++) {
         for (int j = 0; j < numberVariables && !stopExternalLoop; j++) {
            // Consider if the links between i and j (it it exists)
            // is protected
            if (graph[i][j] == DIRECTED) {
               protectedArrow = false;
               stopInternalLoop = false;

               // Considers the third variable
               for (int k = 0; k < numberVariables && !stopInternalLoop; k++) {
                  // Considers the different alternatives
                  if ( // SEVERAL CASES
                          (graph[k][i] == DIRECTED && graph[j][k] == NONE && graph[k][j] == NONE)
                          || (k != i && graph[k][j] == DIRECTED && graph[i][k] == NONE && graph[k][i] == NONE)
                          || (graph[i][k] == DIRECTED && graph[k][j] == DIRECTED)) {
                     protectedArrow = true;
                     stopInternalLoop = true;
                  }
               }

               stopInternalLoop = false;
               // Considers protected arrows respect to 4 nodes
               if (!protectedArrow) {
                  for (int k = 0; k < numberVariables && !stopInternalLoop; k++) {
                     for (int l = 0; l < numberVariables && !stopInternalLoop; l++) {
                        if (k != l && graph[i][k] == UNDIRECTED && graph[i][l] == UNDIRECTED
                                && graph[k][j] == DIRECTED && graph[l][j] == DIRECTED
                                && graph[k][l] == NONE && graph[l][k] == NONE) {
                           protectedArrow = true;
                           stopInternalLoop = true;
                        }
                     }
                  }

                  if (!protectedArrow) {
                     stopExternalLoop = true;
                  }
               }
            }
         }
      }

      // Return protected arrwo
      return protectedArrow;
   }

   /**
    * Method for converting the cpdag represented by the object into a dag. The
    * method returns a new graph changing the value of the pdag data member
    * according to the check
    *
    * @return a new graph
    */
   private Graph cpdagToDag() {
      Graph dag;

      // Array for storing processed nodes
      int marked[] = new int[numberVariables];

      // Sets 0 to all positions
      for (int i = 0; i < numberVariables; i++) {
         marked[i] = 0;
      }

      // Gets memory space for dag data member. Also marks
      // all the nodes as non processed
      dag = new Graph(numberVariables);

      // Process the nodes one by one
      int toProcess = numberVariables;
      dag.pDag = true;
      while (toProcess > 0 && dag.pDag) {
         dag.pDag = false;

         // Consider the variables
         for (int i = 0; i < numberVariables; i++) {
            // The node must be unprocessed, must be a sink and must
            // form a complete component with its neighbours and parents
            // of the node
            if (marked[i] == 0 && isSink(i, marked) && isComplete(i, marked)) {
               // This variable is marked as processed and the links
               // will be directed towards it
               toProcess--;
               marked[i] = 1;

               // The process must go on with another node
               dag.pDag = true;

               // Give direction to the links
               for (int j = 0; j < numberVariables; j++) {
                  if (marked[j] == 0 && graph[j][i] != NONE) { // Np bastaria si es NODIRIGIDO????
                     dag.graph[j][i] = DIRECTED;
                  }
               }
            }
         }
      }

      // return dag
      return dag;
   }

   /**
    * Method to generate a cpdag from dag. This method will return a new graph
    * with built from the current graph
    *
    * @return computed essential graph
    */
   private Graph dagToCpdag() {
      // Compute the order for dag
      computeOrder();

      // Creates a new graph
      Graph cpdag = new Graph(numberVariables);

      // Order the arrows
      int numberArrows = cpdag.orderArrows(this);

      // Now it is needed to detect essential arrows
      cpdag.detectEssentialArrows(numberArrows);

      // Return cpdag
      return cpdag;
   }

   /**
    * Method for detecting the essential arrows
    *
    * @param numberArrows
    */
   private void detectEssentialArrows(int numberArrows) {
      int aux, aux2 = numberArrows + 1, x = -1, y = -1;

      // Loop while numberArrows > 0
      while (numberArrows > 0) {
         aux = aux2;

         // Consider the variables
         for (int i = 0; i < numberVariables; i++) {
            for (int j = 0; j < numberVariables; j++) {
               if (graph[i][j] > 0 && graph[i][j] < aux) {
                  x = i;
                  y = j;
                  aux = graph[i][j];
               }
            }
         }

         // Set aux to 0
         aux = 0;

         // Consider the variables
         for (int i = 0; i < numberVariables; i++) {
            if (graph[i][x] == -1) {
               if (graph[i][y] == 0) {
                  for (int j = 0; j < numberVariables; j++) {
                     if (graph[j][y] > 0) {
                        graph[j][y] = -1;
                        numberArrows--;
                     }
                  }

                  i = numberVariables;
                  aux = 1;
               } else {
                  graph[i][y] = -1;
                  numberArrows--;
               }
            }
         }

         // Checks if aux == 0
         if (aux == 0) {
            for (int i = 0; i < numberVariables; i++) {
               if (graph[i][y] != 0 && i != x && graph[i][x] == 0) {
                  aux = 1;
                  i = numberVariables;
               }
            }

            if (aux == 1) {
               for (int i = 0; i < numberVariables; i++) {
                  if (graph[i][y] > 0) {
                     graph[i][y] = -1;
                     numberArrows--;
                  }
               }
            } else {
               for (int i = 0; i < numberVariables; i++) {
                  if (graph[i][y] > 0) {
                     graph[i][y] = -2;
                     numberArrows--;
                  }
               }
            }
         }
      }

      // Final stage
      for (int i = 0; i < numberVariables; i++) {
         for (int j = 0; j < numberVariables; j++) {
            if (graph[i][j] == -1) {
               graph[i][j] = 1;
            } else {
               if (graph[i][j] == -2) {
                  graph[i][j] = 2;
                  graph[j][i] = 2;
               }
            }
         }
      }
   }

   /**
    * Private method for testing if the component related to a node is complete.
    * Auxiliary method for cpdag2Dag
    *
    * @param node node to consider
    * @param processed list of processed nodes
    * @return
    */
   private boolean isComplete(int node, int[] processed) {
      boolean result = true;

      // Considers neighbours and parents of node
      for (int i = 0; i < numberVariables && result; i++) {
         // Only for non processed nodes and neighbours of node
         if (processed[i] == 0 && graph[i][node] == UNDIRECTED) {
            // Considers the parents
            for (int j = 0; j < numberVariables && result; j++) {
               if (j != i && processed[j] == 0 && graph[j][node] != NONE
                       && graph[i][j] == NONE && graph[j][i] == NONE) {
                  result = false;
               }
            }
         }
      }

      // Return result
      return result;
   }

   /**
    * Private method for testing if a node is a sink. This is an auxiliary
    * method for cpdag2Dag method
    *
    * @param node node to check
    * @param processed list of processed nodes
    * @return
    */
   private boolean isSink(int node, int processed[]) {
      boolean result = true;

      // Considers the rest of nodes
      for (int i = 0; i < numberVariables && result; i++) {
         // Only considers non processed nodes
         if (processed[i] == 0 && graph[node][i] == DIRECTED) {
            // Node is not a sink
            result = false;
         }
      }

      // return result
      return result;
   }

   /**
    * Method for ordering the arrows generating a new graph
    *
    * @param reference graph to consider
    */
   private int orderArrows(Graph reference) {
      int numberArrows = 0;
      int aux;

      // Consider all the components in dag
      for (int k = 0; k < numberComponents; k++) {
         for (int i = 0; i < numberVariables; i++) {
            if (reference.order[i] == k) {
               aux = 0;

               // Loop while aux > -1
               while (aux > -1) {
                  aux = -1;

                  for (int j = 0; j < numberVariables; j++) {
                     if (reference.graph[j][i] == DIRECTED && graph[j][i] == NONE) {
                        if (aux == -1) {
                           aux = j;
                        } else {
                           if (reference.order[j] > reference.order[aux]) {
                              aux = j;
                           }
                        }
                     }
                  }

                  // Increment numberArrows if needed
                  if (aux > -1) {
                     numberArrows++;
                     graph[aux][i] = numberArrows;
                  }
               }
            }
         }
      }

      // Return the number of arrows
      return numberArrows;
   }

   /**
    * Removes direct arcs
    *
    * @return graph without directed arcs
    */
   private Graph removesArcs() {
      Graph newGraph = new Graph(numberVariables);

      // Removes directed edges in graph
      for (int i = 0; i < numberVariables; i++) {
         for (int j = 0; j < numberVariables; j++) {
            if (graph[i][j] == DIRECTED) {
               newGraph.graph[i][j] = NONE;
            } else {
               newGraph.graph[i][j] = graph[i][j];
            }
         }
      }

      return newGraph;
   }
}
