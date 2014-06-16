/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Class for performing the simulation with the Markov Chain
 * Monte Carlo
 * @author mgomez, jmpeña
 */
public class MCMC {
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
    * Class constructor
    *
    * @param numVars
    * @param numIterations (operations on every graph)
    * @param numSamples (graphs to generate)
    * @param firstId
    * @param debugMode
    */
   MCMC(int numVars, int numIterations, int numSamples, int firstId) {
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
         //fileGlobal = new FileOutputStream(globalInfoFileName);
         //pFileGlobal = new PrintStream(fileGlobal);
         //fileArcsLinks = new FileOutputStream(arcsLinksFileName);
         //pFileArcsLinks = new PrintStream(fileArcsLinks);
      } catch (Exception e) {
         System.out.println("Problem opening output files.....");
         System.exit(-1);
      }
      
      // Creates the executor service
      ExecutorService executor=Executors.newCachedThreadPool();

      // Take samples to consider
      for (int i = 0; i < numberSamples; i++) {
         // Takes a sample throug the corresponding thread
         executor.execute(new SampleGenerator(numberVariables, numberOperations,
                                              numberSamples, i, this));

         // Stores the number of arcs and links for every sample
         //pFileArcsLinks.printf("%s", getNumberArcsLinks());

         // To show the progress of the simulation, orint information
         // about the progress
         if (i % 100 == 0) {
            info = showProgress(i+firstId, System.currentTimeMillis() - clock_init);
            System.out.println(info);

            // Open the file
            //pFileGlobal.printf("%s", info);
         }
      }
      
      // Stops the executor
      executor.shutdown();
      
      // Waits until the end
      while(!executor.isTerminated());
      
      // Shows everything has finished
      System.out.println("Simulation just finished");

      // Close files
      //pFileGlobal.close();
      //pFileArcsLinks.close();
   }
   
   /**
    * Method for adding the value of iterEqual
    * @param eq 
    */
   public synchronized void setIterEqual(double eq){
      iterEqual+=eq;
   }
   
   /**
    * Method for adding the value of addLink
    * @param addLink 
    */
   public synchronized void setIterAddLink(double addLink){
      iterAddLink+=addLink;
   }
   
   /**
    * Method for adding the value of deleteLink
    * @param deleteLink
    */
   public synchronized void setIterDeleteLink(double deleteLink){
      iterDeleteLink+=deleteLink;
   }
   
   /**
    * Method for adding the value of addArc
    * @param addArc
    */
   public synchronized void setIterAddArc(double addArc){
      iterAddArc+=addArc;
   }
   
   /**
    * Method for adding the value of deleteArc
    * @param deleteArc
    */
   public synchronized void setIterDeleteArc(double deleteArc){
      iterDeleteArc+=deleteArc;
   }
   
   /**
    * Method for adding the value of addVStructure
    * @param addVStructure
    */
   public synchronized void setIterAddVStructure(double addVStructure){
      iterAddVStructure+=addVStructure;
   }
   
   /**
    * Method for adding the value of deleteVStructure
    * @param deleteVStructure
    */
   public synchronized void setIterDeleteVStructure(double deleteVStructure){
      iterDeleteVStructure+=deleteVStructure;
   }
   
   /**
    * Method for incrementing cumDirected
    */
   public synchronized void addCumDirected(){
      cumDirected++;
   }
   
   /**
    * Method for incrementing cumconnected
    */
   public synchronized void addCumConnected(){
      cumConnected++;
   }
   
   /**
    * Method for incrementing cumDirectedConnected
    */
   public synchronized void addCumDirectedConnected(){
      cumDirectedConnected++;
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
         counter++;
      } 
      else {
         message=message.concat(messageInfo);
      }

      // Return message
      return message;
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
      MCMC object = new MCMC(numVars, numIterations, numSamples, firstId);

      // Perform the simulation
      object.simulate();
   }   
}
