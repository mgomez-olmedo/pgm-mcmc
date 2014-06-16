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
public class MCMCDebug {
   /**
    * Data member to store the number of variables
    */
   private int numberVariables;
   /**
    * Data member to store the graph
    */
   private Graph graph;
   /**
    * Data member to store the number of iterations
    */
   private int numberOperations;
   /**
    * Data member to store the number of samples
    */
   private int numberSamples;
   
   /**
    * Data member to store the first id
    */
   private int firstId;
   
   /**
    * Data member to accumulate the results
    */
   private double iterEqual;
   private double iterAddLink;
   private double iterDeleteLink;
   private double iterAddArc;
   private double iterDeleteArc;
   private double iterAddVStructure;
   private double iterDeleteVStructure;
   private int cumDirected;
   private int cumConnected;
   private int cumDirectedConnected;
   /**
    * Random number generator
    */
   private Random generator;
   /**
    * Counter for controlling info output
    */
   private static int counter = 0;
   
   /**
    * Only for testing purposes with respect to previous
    * implementations
    */
   Scanner operations;
   boolean debugMode;

   /**
    * Class constructor
    *
    * @param numVars
    * @param numIterations (operations on every graph)
    * @param numSamples (graphs to generate)
    * @param firstId
    * @param debugMode
    */
   MCMCDebug(int numVars, int numIterations, int numSamples, int firstId, boolean debugMode) {
      // Sets the data members
      this.numberVariables = numVars;
      this.numberOperations = numIterations;
      this.numberSamples = numSamples;
      this.firstId=firstId;

      // Gives initial values to the data members
      iterEqual = 0;
      iterAddLink = 0;
      iterDeleteLink = 0;
      iterAddArc = 0;
      iterDeleteArc = 0;
      iterAddVStructure = 0;
      iterDeleteVStructure = 0;
      cumDirected = 0;
      cumConnected = 0;
      cumDirectedConnected = 0;

      // Initialize graph to null
      graph = null;
      
      // Creates the random number generator
      generator=new Random();
      
      // Sets debug mode
      this.debugMode=debugMode;
      if (debugMode){
         try{
           File file=new File("operations");
           operations=new Scanner(file);
         }catch(Exception e){};
      }
   }

   /**
    * Performs the simulation
    */
   void simulate() {
      String info;
      FileOutputStream fileGlobal, fileArcsLinks;
      PrintStream pFileGlobal = null, pFileArcsLinks = null;

      // Forms the names of the output files
      String globalInfoFileName;
      String arcsLinksFileName;

      globalInfoFileName = "counting_" + numberVariables + "_" + numberOperations + ".txt";
      arcsLinksFileName = "arcsLinks_" + numberVariables + "_" + numberOperations + ".txt";
      System.out.println("Simulation files " + globalInfoFileName + "  " + arcsLinksFileName);

      // Controls the time of execution
      double clock_init = System.currentTimeMillis();

      // Open both files
      try {
         fileGlobal = new FileOutputStream(globalInfoFileName);
         pFileGlobal = new PrintStream(fileGlobal);
         fileArcsLinks = new FileOutputStream(arcsLinksFileName);
         pFileArcsLinks = new PrintStream(fileArcsLinks);
      } catch (Exception e) {
         System.out.println("Problem opening output files.....");
         System.exit(-1);
      }

      // Take samples to consider
      for (int i = 1; i <= numberSamples; i++) {

         // Takes a sample
         takeSample(i);
//System.out.println("Finalizada generacion muestra: "+i);

         // Stores the number of arcs and links for every sample
         pFileArcsLinks.printf("%s", getNumberArcsLinks());

         // To show the progress of the simulation, orint information
         // about the progress
         if (i % 100 == 0) {
            info = showProgress(i+firstId, System.currentTimeMillis() - clock_init);
            System.out.println(info);

            // Open the file
            pFileGlobal.printf("%s", info);
         }
      }

      // Close files
      pFileGlobal.close();
      pFileArcsLinks.close();
      if (debugMode){
         try{
            operations.close();
         }
         catch(Exception e){};
      }
   }

///////////////////////// PRIVATE METHODS ///////////////////////////
   /**
    * Generates a graph
    * @param identifier for the sample
    */
   void takeSample(int id) {
      int operationCode;
      double eq = 0, addLink = 0, deleteLink = 0, addArc = 0, deleteArc = 0,
              addVStructure = 0, deleteVStructure = 0;

      // Boolean flags for connected components and 
      // existence of undirected links
      boolean noUndirectedLinks, connected;

      // Identifiers for the nodes to operate with
      int x, y, z;

      // Makes a new graph
      graph = new Graph(numberVariables);
      
      // Sets a crono at the beginning
      double initialTime=System.currentTimeMillis();

      // Make as many iterations as stored in numIterations
      for (int i = 0; i < numberOperations; i++) {
         operationCode=getRandomInt(Operations.values().length);

//System.out.println("Codigo de operacion: "+operationCode);

         switch (Operations.values()[operationCode]) {
            case EQUAL:
//System.out.println("Equal: "+eq);               
               eq++;
               break;

            case ADDLINK:
               // Select nodes
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);
//System.out.println("Add link: "+x+"  "+y);
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
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);
//System.out.println("Delete link: "+x+"  "+y);
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
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);
//System.out.println("Add arc: "+x+"  "+y);
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
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);

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
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);
               z = getRandomInt(numberVariables);

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
               x = getRandomInt(numberVariables);
               y = getRandomInt(numberVariables);
               z = getRandomInt(numberVariables);

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

      // Now increment the global data members with these results
      iterEqual += eq;
      iterAddLink += addLink;
      iterDeleteLink += deleteLink;
      iterAddArc += addArc;
      iterDeleteArc += deleteArc;
      iterAddVStructure += addVStructure;
      iterDeleteVStructure += deleteVStructure;
      
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
      noUndirectedLinks = graph.doNotContainsUndirectedLinks();
      if (noUndirectedLinks) {
         cumDirected++;
      }

      // Check if there is a single connected component
      connected = graph.containsSingleComponent();
      if (connected) {
         cumConnected++;
      }

      // Check if both conditions are present
      if (noUndirectedLinks && connected) {
         cumDirectedConnected++;
      }
   }

   /**
    * Private method for showing the progress of the simulation
    *
    * @param iter current iteration
    * @param time current running time
    */
   String showProgress(int iter, double time) {
      String message = new String();
      String messageInfo=new String();
      String header = null;

      // The first time the header information will be printed
      if (counter == 0) {
         header=showHeaderInformation();
      }

      // Show the actual data
      messageInfo =
              cumDirected + " / "
              + iter + " - " +
              + cumConnected + " / "
              + iter + " - " +
              + cumDirectedConnected + " / "
              + cumDirected + " - " +
              + cumDirectedConnected + " / "
              + cumConnected + " = "
              + cumDirected / (double) iter + " "
              + cumConnected / (double) iter + "  "
              + cumDirectedConnected / (double) cumDirected + "  "
              + cumDirectedConnected / (double) cumConnected + "  "
              + time + "  "
              + iterEqual / (double) iter + " "
              + iterAddLink / (double) iter + "  "
              + iterDeleteLink / (double) iter + "  "
              + iterAddArc / (double) iter + "  "
              + iterDeleteArc / (double) iter + "  "
              + iterAddVStructure / (double) iter + "  "
              + iterDeleteVStructure / (double) iter + "  "
              + (iterEqual + iterAddLink + iterDeleteLink
              + iterAddArc + iterDeleteArc + iterAddVStructure
              + iterDeleteVStructure) / (double) iter + "\n";

      if (counter == 0) {
         message=message.concat(header);
         message=message.concat(messageInfo);
         counter++;
      } 
      else {
         message=message.concat(messageInfo);
      }

      // Return message
      return message;
   }
   
   /**
    * Gets a random number according to the debug mode
    * @param limit
    * @return 
    */
   int getRandomInt(int limit){
      int result=-1;
      if (!debugMode){
         result=generator.nextInt(numberVariables);
      }
      else{
         result=operations.nextInt();
      }
      
      return result;
   }

   /**
    * Gets the number of arcs and links
    * @return 
    */
   String getNumberArcsLinks() {
      String message;
      int numberArcs, numberLinks;
      double total;
      graph.countArcsLinks();
      numberArcs = graph.getNumberArcs();
      numberLinks = graph.getNumberLinks();
      total = numberArcs + numberLinks;
      message = numberArcs + "    " + numberLinks + "    " + numberArcs / total + "    " + numberLinks / total + "\n";
      //graph.print();
      //cout << "Arcs: " << graph.getNumberArcs() << "  Links: " << graph.getNumberLinks() << endl;
      return message;
   }

   /**
    * Auxiliar method for showProgress
    */
   String showHeaderInformation() {
      String header=new String();

      header = "--------------------- Columns information ---------------\n";
      header = header + "Columns meanings: \n";
      header = header + "(1) : no undirected links / iter\n";
      header = header + "(2) : connected components / iter\n";
      header = header + "(3) : no undirected links and connected components / no undirect links\n";
      header = header + "(4) : no undirected links and connected components / connected components\n";
      header = header + "(5) : prob. no undirected links\n";
      header = header + "(6) : prob. connected components\n";
      header = header + "(7) : prob. no undirected links and connected components (respect no undirect links)\n";
      header = header + "(8) : prob. no undirected links and connected components (respect connected components)\n";
      header = header + "(9) : execution time\n";
      header = header + "(10) : prob. equals\n";
      header = header + "(11) : prob. add link\n";
      header = header + "(12) : prob. delete link\n";
      header = header + "(13) : prob. add arc\n";
      header = header + "(14) : prob. delete arc\n";
      header = header + "(15) : prob. add v structure\n";
      header = header + "(16) : prob. delete v structure\n";
      header = header + "(17) : prob. valid structure\n";
      header = header + "-----------------------------------------------------------------\n";

      // Return header
      return header;
   }
   
   /** 
    * Main method
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      // Check the correct number of arguments
      if (args.length != 4) {
         System.out.printf("\n Program use: ");
         System.out.printf("\n Java counting.MCMC n_vars n_its n_samples first_id\n");
         System.exit(0);
      }

      // Gets the values for the arguments
      int numVars = Integer.parseInt(args[0]);
      int numIterations = Integer.parseInt(args[1]);
      int numSamples = Integer.parseInt(args[2]);
      int firstId= Integer.parseInt(args[3]);

      System.out.println("Variables: " + numVars + "  iterations: " + numIterations + "  samples: " + numSamples + "\n");

      // Creates an object for performing the experiment
      MCMCDebug object = new MCMCDebug(numVars, numIterations, numSamples, firstId,true);

      // Perform the simulation
      object.simulate();
   }   
}
