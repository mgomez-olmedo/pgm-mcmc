/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;

/**
 * Class for processing the samples once generated
 *
 * @author mgomez, jmpeña
 */
public class SampleViewer {

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
    * Data member to store the sample id
    */
   private int sampleId;

   /**
    * Class constructor
    *
    * @param numVars
    * @param numIterations (operations on every graph)
    * @param numSamples (graphs to process)
    * @param sampleId
    */
   SampleViewer(int numVars, int numIterations, int numSamples, int sampleId) {
      // Sets the data members
      this.numberVariables = numVars;
      this.numberIterations = numIterations;
      this.numberSamples = numSamples;
      this.sampleId=sampleId;
   }

   /**
    * Method for processing the samples: serialized objects of Sample class
    * stored in a certain folder with the name including the number of
    * variables, the number of iterations and the number of samples
    */
   public void viewData() {
      // Compose the name of the folder to look for
      String folderName = new String("./graphs/samples-" + numberVariables + "-iter-" + numberIterations + "-samp-" + numberSamples + "/");
      Sample sample;

      // List the content of the folder
      // Compound the prefix
      String prefix = new String("sample-"+sampleId+".");
      System.out.println("Prefix: "+prefix);
      System.out.println("Folder name: "+folderName);
      File dir;
      File[] files = null;


      // Look for files with this prefix
      try {
         dir = new File(folderName);
         files = dir.listFiles(new FilesFilter(prefix, ""));
         System.out.println("Archivos encontrados: "+files.length);
         
         // Must be only one or cero
         if (files.length != 0){
            System.out.println("Data about ..... "+files[0].getName());
            
            // Read the data
            sample=Sample.readObject(folderName+files[0].getName());
            sample.process();
            sample.printInfo();
         }
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(0);
      }
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
      if (args.length != 4) {
         System.out.printf("\n Program use: ");
         System.out.printf("\n java counting.ProcessSamples n_vars n_its n_samples sample_id\n");
         System.exit(0);
      }

      // Gets the values for the arguments
      int numVars = Integer.parseInt(args[0]);
      int numIterations = Integer.parseInt(args[1]);
      int numSamples = Integer.parseInt(args[2]);
      int sampleId=Integer.parseInt(args[3]);

      System.out.println("Variables: " + numVars + "  iterations: " + numIterations + "  samples: " + numSamples + "  sample id: " + sampleId+"\n");

      // Creates an object for performing the experiment
      SampleViewer object = new SampleViewer(numVars, numIterations, numSamples,sampleId);

      // Perform the simulation
      object.viewData();
   }
}
