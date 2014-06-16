/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Class for processing the samples once generated
 *
 * @author mgomez, jmpeña
 */
public class SamplesProcessor {

   /**
    * Data member to store the number of var
    */
   private int numberVariables;

   /**
    * Data member to store the number of iterations
    */
   private int numberIterations;

   /**
    * Data member to store the number of samples
    */
   private int numberSamples;

   /**
    * Debug flag for presenting the graphs as long as they are processed
    */
   private boolean verbose = false;

   /**
    * Data member to store the number of processed files
    */
   private int processedFiles = 0;

   /**
    * Data members for storing the data about the processed graphs
    */
   private double globalEqualProb = 0;
   private double globalAddLinkProb = 0;
   private double globalDeleteLinkProb = 0;
   private double globalAddArcProb = 0;
   private double globalDeleteArcProb = 0;
   private double globalAddVStructureProb = 0;
   private double globalDeleteVStructureProb = 0;
   private double cumDirected = 0;
   private double cumConnected = 0;
   private double cumDirectedConnected = 0;
   private double arcsProb = 0;
   private double linksProb = 0;
   private double generationTime = 0;
   private BigDecimal upperBound;
   private BigDecimal lowerBound;

   /**
    * HasMap for storing the number of graphs with each number of links and arcs
    */
   private TreeMap<Double, Integer> linksInGraphs;
   private TreeMap<Double, Integer> arcsInGraphs;
   private TreeMap<Double, Integer> lowerBoundInGraphs;

   /**
    * Class constructor
    *
    * @param numVars
    * @param numIterations (operations on every graph)
    * @param numSamples (graphs to process)
    * @param verbose mode
    */
   SamplesProcessor(int numVars, int numIterations, int numSamples, boolean verbose) {
      // Sets the data members
      this.numberVariables = numVars;
      this.numberIterations = numIterations;
      this.numberSamples = numSamples;
      this.verbose = verbose;
      upperBound = new BigDecimal(0);
      lowerBound = new BigDecimal(0);
      linksInGraphs = new TreeMap<Double, Integer>();
      arcsInGraphs = new TreeMap<Double, Integer>();
      lowerBoundInGraphs = new TreeMap<Double, Integer>();
   }

   /**
    * Method for processing the samples: serialized objects of Sample class
    * stored in a certain folder with the name including the number of
    * variables, the number of iterations and the number of samples
    */
   public void process() {
      // Compose the name of the folder to look for
      String folderName = new String("./graphs/samples-" + numberVariables + "-iter-" + numberIterations + "-samp-" + numberSamples + "/");
      Sample sample;
      // File for arcs and links info
      FileOutputStream fileArcsLinks, fileLowerBound;
      PrintStream pFileArcsLinks = null, pFileLowerBound = null;
      
      // Open fileArcsLink
      try {
         fileArcsLinks = new FileOutputStream("arcsLinks-"+numberVariables + "-iter-" + numberIterations + "-samp-" + numberSamples + ".txt");
         pFileArcsLinks = new PrintStream(fileArcsLinks);
         fileLowerBound = new FileOutputStream("lowerBound-"+numberVariables + "-iter-" + numberIterations + "-samp-" + numberSamples + ".txt");
         pFileLowerBound= new PrintStream(fileLowerBound);
      } catch (Exception e) {
         System.out.println("Problem opening arcsLinks file....");
         System.exit(-1);
      }

      // List the content of the folder
      // Compound the prefix
      String prefix = new String("sample-");
      File dir;
      File[] files = null;

      // Look for files with this prefix
      try {
         dir = new File(folderName);
         files = dir.listFiles(new FilesFilter(prefix, ""));

         // Read the object comming from every file
         for (int i = 0; i < files.length; i++) {
            sample = Sample.readObject(folderName + files[i].getName());

            // Now process the data of this sample
            process(sample);
            if (verbose) {
               sample.printInfo();
            }
            
            pFileArcsLinks.printf(String.format(Locale.US,"%f  %f\n", sample.getArcsProb(),sample.getLinksProb()));
            pFileLowerBound.printf(String.format(Locale.US,"%f\n",sample.getLowerBound()));
         }

         // Show the final information
         if (verbose) {
            showFinalInformation();
         }
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(0);
      }
      
      // Close the file
      pFileArcsLinks.close();
      pFileLowerBound.close();
   }

   /**
    * Method for processing every sample
    *
    * @param sample
    */
   public void process(Sample sample) {
      boolean connected;

      // Be sure the data about the graph are computed
      sample.process();

      // Increments the counter of processed files
      processedFiles++;

      // Gets the data about the sample
      globalEqualProb += sample.getEqualProb();
      globalAddArcProb += sample.getAddArcProb();
      globalDeleteArcProb += sample.getDeleteArcProb();
      globalAddLinkProb += sample.getAddLinkProb();
      globalDeleteLinkProb += sample.getDeleteLinkProb();
      globalAddVStructureProb += sample.getAddVStructureProb();
      globalDeleteVStructureProb += sample.getDeleteVStructureProb();
      arcsProb += sample.getArcsProb();
      linksProb += sample.getLinksProb();

      // Gets info about number of links and arcs
      double links = sample.getLinksProb();
      if (!linksInGraphs.containsKey(links)) {
         linksInGraphs.put(links, 1);
      } else {
         int counter = linksInGraphs.get(links);
         linksInGraphs.put(links, counter + 1);
      }
      double arcs = sample.getArcsProb();
      if (!arcsInGraphs.containsKey(arcs)) {
         arcsInGraphs.put(arcs, 1);
      } else {
         int counter = arcsInGraphs.get(arcs);
         arcsInGraphs.put(arcs, counter + 1);
      }
      double lowerBoundInGraph = sample.getLowerBound().doubleValue();
      if (!lowerBoundInGraphs.containsKey(lowerBoundInGraph)) {
         lowerBoundInGraphs.put(lowerBoundInGraph, 1);
      } else {
         int counter = lowerBoundInGraphs.get(lowerBoundInGraph);
         lowerBoundInGraphs.put(lowerBoundInGraph, counter + 1);
      }

      // The same for the bound
      upperBound = upperBound.add(sample.getUpperBound());
      lowerBound = lowerBound.add(sample.getLowerBound());

      // Increments the generation time
      generationTime += sample.getGenerationTime();

      // Gets information about connected and undirected links
      connected = sample.getConnected();
      if (connected) {
         cumConnected++;
      }

      // The same for undirected
      if (!sample.getUndirected()) {
         cumDirected++;

         // If it is connected to, add the corresponding counter
         if (connected) {
            cumDirectedConnected++;
         }
      }
   }

   /**
    * Gets numberIterations value
    *
    * @return
    */
   public int getNumberIterations() {
      return numberIterations;
   }

   /**
    * Gets numberSamples value
    *
    * @return
    */
   public int getNumberSamples() {
      return numberSamples;
   }

   /**
    * Gets globalEqualProb value
    *
    * @return
    */
   public double getGlobalEqualProb() {
      return globalEqualProb;
   }

   /**
    * Gets globalAddLink value
    *
    * @return
    */
   public double getGlobalAddLinkProb() {
      return globalAddLinkProb;
   }

   /**
    * Gets globalDeleteLinkProb value
    *
    * @return
    */
   public double getGlobalDeleteLinkProb() {
      return globalDeleteLinkProb;
   }

   /**
    * Gets getGlobalAddArcProb
    *
    * @return
    */
   public double getGlobalAddArcProb() {
      return globalAddArcProb;
   }

   /**
    * Gets globalDeleteArc value
    *
    * @return
    */
   public double getGlobalDeleteArcProb() {
      return globalDeleteArcProb;
   }

   /**
    * Gets globalAddVStructureProb value
    *
    * @return
    */
   public double getGlobalAddVStructureProb() {
      return globalAddVStructureProb;
   }

   /**
    * Gets globalDeleteVStructureProb value
    *
    * @return
    */
   public double getGlobalDeleteVStructureProb() {
      return globalDeleteVStructureProb;
   }

   /**
    * Gets getCumDirected value
    *
    * @return
    */
   public double getCumDirected() {
      return cumDirected;
   }

   /**
    * Gets cumConnected value
    *
    * @return
    */
   public double getCumConnected() {
      return cumConnected;
   }

   /**
    * Gets getCumDirectedConnected value
    *
    * @return
    */
   public double getCumDirectedConnected() {
      return cumDirectedConnected;
   }

   /**
    * Gets getsArcsProb value
    *
    * @return
    */
   public double getArcsProb() {
      return arcsProb;
   }

   /**
    * Gets getLinksProb value
    *
    * @return
    */
   public double getLinksProb() {
      return linksProb;
   }

   public double getGenerationTime() {
      return generationTime;
   }

   /**
    * Gets upperBound value
    *
    * @return
    */
   public BigDecimal getUpperBound() {
      return upperBound;
   }

   /**
    * Gets lowerBound value
    *
    * @return
    */
   public BigDecimal getLowerBound() {
      return lowerBound;
   }

   /**
    * Auxiliar method for showing the final information
    */
   private void showFinalInformation() {
      System.out.println("Directed graphs - generated graphs");
      System.out.println(cumDirected + " / " + processedFiles + " --- Prob: "
         + (double) cumDirected / (double) processedFiles);
      System.out.println();

      System.out.println("Connected graphs - generated graphs");
      System.out.println(cumConnected + " / " + processedFiles + " --- Prob: "
         + (double) cumConnected / (double) processedFiles);
      System.out.println();

      System.out.println("Connected and directed graphs - directed graphs");
      System.out.println(cumDirectedConnected + " / " + cumDirected + " --- Prob: "
         + (double) cumDirectedConnected / (double) cumDirected);
      System.out.println();

      System.out.println("Connected and directed graphs - connected graphs");
      System.out.println(cumDirectedConnected + " / " + cumConnected + " --- Prob: "
         + (double) cumDirectedConnected / (double) cumConnected);
      System.out.println();

      System.out.println("Complete generation time: " + generationTime);
      System.out.println();

      System.out.println("Equal operations - generated graphs");
      System.out.println(globalEqualProb + " / " + processedFiles + " --- Prob: "
         + (double) globalEqualProb / (double) processedFiles);
      System.out.println();

      System.out.println("Add link operations - generated graphs");
      System.out.println(globalAddLinkProb + " / " + processedFiles + " --- Prob: "
         + (double) globalAddLinkProb / (double) processedFiles);
      System.out.println();

      System.out.println("Delete link operations - generated graphs");
      System.out.println(globalDeleteLinkProb + " / " + processedFiles + " --- Prob: "
         + (double) globalDeleteLinkProb / (double) processedFiles);
      System.out.println();

      System.out.println("Add arc operations - generated graphs");
      System.out.println(globalAddArcProb + " / " + processedFiles + " --- Prob: "
         + (double) globalAddArcProb / (double) processedFiles);
      System.out.println();

      System.out.println("Delete arc operations - generated graphs");
      System.out.println(globalDeleteArcProb + " / " + processedFiles + " --- Prob: "
         + (double) globalDeleteArcProb / (double) processedFiles);
      System.out.println();

      System.out.println("Add v-structure operations - generated graphs");
      System.out.println(globalAddVStructureProb + " / " + processedFiles + " --- Prob: "
         + (double) globalAddVStructureProb / (double) processedFiles);
      System.out.println();

      System.out.println("Delete v-structure operations - generated graphs");
      System.out.println(globalDeleteVStructureProb + " / " + processedFiles + " --- Prob: "
         + (double) globalDeleteVStructureProb / (double) processedFiles);
      System.out.println();

      System.out.println("Proportion valid operations - generated graphs");
      System.out.println((double) (globalEqualProb + globalAddArcProb
         + globalDeleteArcProb + globalAddLinkProb
         + globalDeleteLinkProb + globalAddVStructureProb
         + globalDeleteVStructureProb) / (double) processedFiles);
      System.out.println();

      System.out.println("Proportion arcs - generated graphs");
      System.out.println((double) arcsProb / (double) processedFiles);
      System.out.println();

      System.out.println("Proportion links - generated graphs");
      System.out.println((double) linksProb / (double) processedFiles);
      System.out.println();

      // Shows information about the bounds
      System.out.println("Upper bound - generated graphs");
      System.out.println(upperBound.divide(new BigDecimal(processedFiles), 10, BigDecimal.ROUND_HALF_EVEN));
      System.out.println();
      System.out.println("Lower bound - generated graphs");
      System.out.println(lowerBound.divide(new BigDecimal(processedFiles), 10, BigDecimal.ROUND_HALF_EVEN));
      System.out.println();

      // Shows information about links
      System.out.println("\nLinks info: ----------------------");
      for (double links : linksInGraphs.keySet()) {
         System.out.println("  " + links + " ---- " + linksInGraphs.get(links));
      }
      // Shows information about arcs
      System.out.println("\nArcs info: ----------------------");
      for (double arcs : arcsInGraphs.keySet()) {
         System.out.println("  " + arcs + " ---- " + arcsInGraphs.get(arcs));
      }
      System.out.println("Files: " + processedFiles);
      // Shows information about lowerBound
      System.out.println("\nLower bound: ----------------------");
      for (double lower : lowerBoundInGraphs.keySet()) {
         System.out.println("  " + lower + " ---- " + lowerBoundInGraphs.get(lower));
      }
      System.out.println("\nFiles: " + processedFiles);
   }

   /**
    * Auxiliar class for filtering the files with a given extension and for a
    * given net
    *
    * @author mgomez
    */
   class FilesFilter implements FileFilter {

      private String extension;
      private String prefix;

      public FilesFilter(String prefix, String extension) {
         this.prefix = prefix;
         this.extension = extension;
      }

      public boolean accept(java.io.File f) {
         if (!f.isDirectory()) {
            String name = f.getName();
            return name.endsWith(extension) && name.startsWith(prefix);
         }
         return false;
      }
   }

   /**
    * Main method
    *
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      // Check the correct number of arguments
      if (args.length < 3) {
         System.out.printf("\n Program use: ");
         System.out.printf("\n java counting.ProcessSamples n_vars n_its n_samples [-verbose] \n");
         System.exit(0);
      }

      // Gets the values for the arguments
      int numVars = Integer.parseInt(args[0]);
      int numIterations = Integer.parseInt(args[1]);
      int numSamples = Integer.parseInt(args[2]);
      boolean verbose = false;

      if (args.length == 4) {
         verbose = args[3].equals("-verbose");
      }

      System.out.println("Variables: " + numVars + "  iterations: " + numIterations + "  samples: " + numSamples + "\n");

      // Creates an object for performing the experiment
      SamplesProcessor object = new SamplesProcessor(numVars, numIterations, numSamples, verbose);

      // Perform the simulation showing the final information
      object.process();
   }
}
