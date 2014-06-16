/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * Class for performing the simulation with the Markov Chain
 * Monte Carlo
 * @author mgomez, jmpeña
 */
public class SampleGenerator implements Runnable{
   /**
    * Data member of MCMC class for updating the properties
    * of the graph
    */
   private MCMC simulation;
   
   /**
    * Data member to store the number of variables
    */
   private final int numberVariables;
   /**
    * Data member to store the graph
    */
   private Graph graph;
   /**
    * Data member to store the number of iterations
    */
   private final int numberOperations;
   /**
    * Data member to store the number of samples
    */
   private final int numberSamples;
   /**
    * Data member to store the id
    */
   private final int id;
   
   /**
    * Data member to accumulate the results
    */
   private double eq;
   private double addLink;
   private double deleteLink;
   private double addArc;
   private double deleteArc;
   private double addVStructure;
   private double deleteVStructure;
   private boolean directed;
   private boolean connected;
   private boolean directedConnected;
   /**
    * Random number generator
    */
   private Random generator;
   
   /**
    * Class constructor
    *
    * @param numVars
    * @param numIterations (operations on every graph)
    * @param numSamples (graphs to generate)
    * @param id
    * @param debugMode
    */
   SampleGenerator(int numVars, int numIterations, int numSamples, int id,
      MCMC simulation) {
      // Sets the data members
      this.numberVariables = numVars;
      this.numberOperations = numIterations;
      this.numberSamples = numSamples;
      this.id=id;
      this.simulation=simulation;

      // Gives initial values to the data members
      eq = 0;
      addLink = 0;
      deleteLink = 0;
      addArc = 0;
      deleteArc = 0;
      addVStructure = 0;
      deleteVStructure = 0;
      directed = false;
      connected = false;
      directedConnected = false;

      // Initialize graph to null
      graph = null;
      
      // Creates the random number generator
      generator=new Random();
   }

   /**
    * Run method
    */
   @Override
   public void run() {
      int operationCode;

      // Identifiers for the nodes to operate with
      int x, y, z;

      // Makes a new graph
      graph = new Graph(numberVariables);
      
      // Sets a crono at the beginning
      double initialTime=System.currentTimeMillis();

      // Make as many iterations as stored in numIterations
      for (int i = 0; i < numberOperations; i++) {
         operationCode=generator.nextInt(Operations.values().length);

         switch (Operations.values()[operationCode]) {
            case EQUAL:
               eq++;
               break;

            case ADDLINK:
               // Select nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 

               // Perform the change if x and y are not the
               // same node
               if (graph.isAddLinkPossible(x, y)) {
                  graph.addLink(x, y);

                  // Check the graph
                  if (graph.check() == false) {
                     // The operation must be reverted
                     graph.undo();
                  } else {
                     // Add one to the counter of hits after adding link
                     addLink++;
                  }
               }
               break;

            case DELETELINK:
               // Select nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 

               // Perform the change if both nodes are not the same
               if (graph.isDeleteLinkPossible(x, y)) {
                  graph.deleteLink(x, y);

                  // Check the graph
                  if (graph.check() == false) {
                     graph.undo();
                  } else {
                     // Add to the corresponding counter
                     deleteLink++;
                  }
               }
               break;

            case ADDARC:
               // Select nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 

               // Perform the change if both nodes are not the same
               if (graph.isAddArcPossible(x, y)) {
                  graph.addArc(x, y);

                  // Check the graph
                  if (graph.check() == false) {
                     graph.undo();
                  } else {
                     // Add the corresponding counter
                     addArc++;
                  }
               }
               break;

            case DELETEARC:
               // Select nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 

               // Perform the change if both nodes are not the same
               if (graph.isDeleteArcPossible(x, y)) {
                  graph.deleteArc(x, y);

                  // Check the graph
                  if (graph.check() == false) {
                     graph.undo();
                  } else {
                     // Add the corresponding counter
                     deleteArc++;
                  }
               }
               break;

            case ADDVSTRUCTURE:
               // Select the nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 
               z = generator.nextInt(numberVariables); 

               // Perform the change if both nodes are not the same
               if (graph.isAddVStructurePossible(x, y, z)) {
                  graph.addVStructure(x, y, z);

                  // Check the graph
                  if (graph.check() == false) {
                     graph.undo();
                  } else {
                     // Add the corresponding counter
                     addVStructure++;
                  }
               }
               break;

            case DELETEVSTRUCTURE:
               // Select the nodes
               x = generator.nextInt(numberVariables); 
               y = generator.nextInt(numberVariables); 
               z = generator.nextInt(numberVariables); 

               // Perform the change if both nodes are not the same
               if (graph.isDeleteVStructurePossible(x, y, z)) {
                  graph.deleteVStructure(x, y, z);

                  // Check the graph
                  if (graph.check() == false) {
                     graph.undo();
                  } else {
                     // Add the corresponding counter
                     deleteVStructure++;
                  }
               }
               break;
         }
      }
      
      // Creates a sample object
      Sample newSample=new Sample(graph,System.currentTimeMillis()-initialTime,id);
      
      // Compute the probs for every operation   
      eq = eq / numberOperations;
      addLink = addLink / numberOperations;
      deleteLink = deleteLink / numberOperations;
      addArc = addArc / numberOperations;
      deleteArc = deleteArc / numberOperations;
      addVStructure = addVStructure / numberOperations;
      deleteVStructure = deleteVStructure / numberOperations;
      
      // Sets the data members for the sample
      newSample.setEqualProb(eq);
      newSample.setAddArcProb(addArc);
      newSample.setDeleteArcProb(deleteArc);
      newSample.setAddLinkProb(addLink);
      newSample.setDeleteLinkProb(deleteLink);
      newSample.setAddVStructureProb(addVStructure);
      newSample.setDeleteVStructureProb(deleteVStructure);
      newSample.setNumberOperations(numberOperations);
      newSample.setNumberSamples(numberSamples);
      
      // Now store the object
      newSample.writeObject();


      // Check if all the links are directed
      directed = graph.doNotContainsUndirectedLinks();

      // Check if there is a single connected component
      connected = graph.containsSingleComponent();

      // Check if both conditions are present
      if (directed && connected) {
         directedConnected=true;
      }
      
      // At the end update the values to the simulation object
      updateValues();
   }
   
   /**
    * Method for updating the values of the simulation
    */
   private void updateValues(){
      simulation.setIterEqual(eq);
      simulation.setIterAddLink(addLink);
      simulation.setIterDeleteLink(deleteLink);
      simulation.setIterAddArc(addArc);
      simulation.setIterDeleteArc(deleteArc);
      simulation.setIterAddVStructure(addVStructure);
      simulation.setIterDeleteVStructure(deleteVStructure);
      if (connected){
         simulation.addCumConnected();
      }
      if (directed){
         simulation.addCumDirected();
      }
      if (directedConnected){
         simulation.addCumDirectedConnected();
      }
   }
}
