/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counting;

import java.io.*;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Iterator;

/**
 * Class for storing and managing samples
 * @author mgomez, jmpeña
 */
public class Sample implements Serializable {
   /**
    * Data member to store a Graph
    */
   private Graph graph;
   /**
    * Data member to store the graph without directed links in order
    * to compute the components and the maximal cliques
    */
   private Graph transformedGraph;
   
   /**
    * Data member to store the set of cliques. There will be a list of
    * cliques for every component
    */
   private ArrayList<ArrayList<HashSet<Integer>>> cliques;

   /**
    * Data member to store the list of biggest cliques. There will be
    * a clique (biggest) for every component
    */
   private ArrayList<HashSet<Integer>> biggestCliques;

   /**
    * Data member to store the list of connected components. There will be
    * a list of components
    */
   private ArrayList<HashSet<Integer>> components;
   
   /**
    * Data member to identify the graph in the set of graphs
    */
   private int id;
   /**
    * Data member to store the number of operations required for generating the
    * graph
    */
   private int numberOperations;
   /**
    * Data member to store the number of variables for the graphs generated
    * during the experiment. This is needed to store the serialized objects in a
    * file with the proper name
    */
   private int numberVariables;
   /**
    * Data member to store the number of samples generated for the experiment
    * where this graph is generated
    */
   private int numberSamples;
   /**
    * Data member to store the time required for generating the graph
    */
   private double generationTime;
   /**
    * Data member to store the number of operations used for the generation
    */
   private double equalProb;
   private double addLinkProb;
   private double deleteLinkProb;
   private double addArcProb;
   private double deleteArcProb;
   private double addVStructureProb;
   private double deleteVStructureProb;
   /**
    * Data member to store the number of arcs and its prob
    */
   private int numberArcs;
   private double arcsProb;
   /**
    * Data member to store the number of links and its prob
    */
   private int numberLinks;
   private double linksProb;
   /**
    * Data member to store if the graph contains a single component
    */
   private boolean connected;
   /**
    * Data member to store if the graph contains undirected links
    */
   private boolean undirected;
   /**
    * Data member to store the estimation of the upper bound for the
    * number of graphs represented by the cliques
    */
   private BigDecimal upperBound;
   /**
    * Data member to store the estimation of the lower bound for the 
    * number of graphs represented byt the cliques
    */
   private BigDecimal lowerBound;

   /**
    * Class constructor
    *
    * @param graph
    * @param generationTime
    * @param id for the sample
    */
   public Sample(Graph graph, double generationTime, int id) {
      // Sets the data members
      this.graph = graph;
      this.generationTime = generationTime;
      this.numberVariables = graph.getNumberVariables();
      
      // Initializes graphsBound
      upperBound=new BigDecimal(1);
      lowerBound=new BigDecimal(1);

      // Sets the id
      this.id = id;
   }

   /**
    * Method for setting the number of equal operations required for generation
    * the sample
    *
    * @param prob
    */
   public void setEqualProb(double prob) {
      equalProb = prob;
   }

   /**
    * Returns equalOps
    *
    * @return
    */
   public double getEqualProb() {
      return equalProb;
   }

   /**
    * Method for setting the number of addLink operations required for
    * generation the sample
    *
    * @param prob
    */
   public void setAddLinkProb(double prob) {
      addLinkProb = prob;
   }

   /**
    * Gets addLinkOps
    *
    * @return
    */
   public double getAddLinkProb() {
      return addLinkProb;
   }

   /**
    * Method for setting the number of deleteLink operations required for
    * generation the sample
    *
    * @param ops
    */
   public void setDeleteLinkProb(double prob) {
      deleteLinkProb = prob;
   }

   /**
    * Gets deleteLinkOps
    *
    * @return
    */
   public double getDeleteLinkProb() {
      return deleteLinkProb;
   }

   /**
    * Method for setting the number of addArc operations required for generation
    * the sample
    *
    * @param ops
    */
   public void setAddArcProb(double prob) {
      addArcProb = prob;
   }

   /**
    * Gets addArcOps
    *
    * @return
    */
   public double getAddArcProb() {
      return addArcProb;
   }

   /**
    * Method for setting the number of deleteArc operations required for
    * generation the sample
    *
    * @param ops
    */
   public void setDeleteArcProb(double prob) {
      deleteArcProb = prob;
   }

   /**
    * Gets deleteArcOps
    *
    * @return
    */
   public double getDeleteArcProb() {
      return deleteArcProb;
   }

   /**
    * Method for setting the number of addVStruncture operations required for
    * generation the sample
    *
    * @param ops
    */
   public void setAddVStructureProb(double prob) {
      addVStructureProb = prob;
   }

   /**
    * Gets addVStructureOps
    *
    * @return
    */
   public double getAddVStructureProb() {
      return addVStructureProb;
   }

   /**
    * Method for setting the number of deleteVStruncture operations required for
    * generation the sample
    *
    * @param ops
    */
   public void setDeleteVStructureProb(double prob) {
      deleteVStructureProb = prob;
   }

   /**
    * Gets deleteVStructureOps
    *
    * @return
    */
   public double getDeleteVStructureProb() {
      return deleteVStructureProb;
   }

   /**
    * Gets arcsProb
    *
    * @return
    */
   public double getArcsProb() {
      return arcsProb;
   }

   /**
    * Gets linksProb
    *
    * @return
    */
   public double getLinksProb() {
      return linksProb;
   }

   /**
    * Gets connected
    *
    * @return
    */
   public boolean getConnected() {
      return connected;
   }

   /**
    * Gets undirected
    *
    * @return
    */
   public boolean getUndirected() {
      return undirected;
   }

   /**
    * Gets generationTime
    *
    * @return
    */
   public double getGenerationTime() {
      return generationTime;
   }
   
   /**
    * Gets the upper bound
    */
   public BigDecimal getUpperBound(){
      return upperBound;
   }
   
   /**
    * Gets the lower bound
    */
   public BigDecimal getLowerBound(){
      return lowerBound;
   }

   /**
    * Method for setting the number of operations required for generating the
    * graph
    *
    * @param ops
    */
   public void setNumberOperations(int ops) {
      numberOperations = ops;
   }

   /**
    * Method for setting the number of samples for the experiment
    */
   public void setNumberSamples(int samples) {
      this.numberSamples = samples;
   }

   /**
    * Gets the information about the graph
    */
   public void process() {
      // Gets information about the number of arcs and
      // links
      graph.countArcsLinks();
      numberArcs = graph.getNumberArcs();
      numberLinks = graph.getNumberLinks();

      // Computes the probabilities of arcs and links
      arcsProb = (double) numberArcs;
      linksProb = (double) numberLinks;

      // The same for the existance of a single
      if (graph.containsSingleComponent()) {
         connected = true;
      }

      // The presence of undirected links is easy to
      // get
      if (numberLinks != 0) {
         undirected = true;
      }
      
      // Compute the set of components: only if there are undirected
      // links
      if (undirected){
         computeComponents();
      
         // Compute the set of cliques for every component
         computeCliques();
      
         // Prints the list of components and cliques
         //printComponentsAndCliques();
      
          // Compute the bound
          computeBounds();
      }
   }

   /**
    * Method for serializing sample objects
    */
   public void writeObject() {
      // Compose the name of the file
      String pathName = new String("./graphs/samples-" + numberVariables + "-iter-" + numberOperations+ "-samp-" + numberSamples + "/");
      String fileName = new String(pathName + "sample-" + id+".txt");
      try {
         // Check if the directory is present. If this is not the case, creates
         // it
         File folder = new File(pathName);
         if (!folder.exists()) {
            folder.mkdir();
         }
         FileOutputStream file = new FileOutputStream(fileName);
         ObjectOutputStream stream = new ObjectOutputStream(file);
         stream.writeObject(this);
         stream.close();
         file.close();
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Problem creating sample serialized file");
         System.exit(0);
      }
   }

   /**
    * Print info about every sample
    */
   public void printInfo() {
      System.out.println("------------------ GRAPH -----------------------");
      graph.print();
      System.out.println("................................................");
      System.out.println("Equal prob: " + equalProb);
      System.out.println("Add arc prob: " + addArcProb);
      System.out.println("Delete arc prob: " + deleteArcProb);
      System.out.println("Add link prob: " + addLinkProb);
      System.out.println("Delete link prob: " + deleteLinkProb);
      System.out.println("Add v-structure prob: " + addVStructureProb);
      System.out.println("Delete v-structure prob: " + deleteVStructureProb);
      System.out.println("Connected: " + connected);
      System.out.println("Undirected links: " + undirected);
      System.out.println("Number arcs: " + numberArcs);
      System.out.println("Number links: " + numberLinks);
      System.out.println("Arcs prob: " + arcsProb);
      System.out.println("Links prob: " + linksProb);
      System.out.println("Upper bound: "+ upperBound);
      System.out.println("Lower bound: "+ lowerBound);
      System.out.println();
   }

   /**
    * Method for reading sample objects
    *
    * @param file
    */
   public static Sample readObject(String fileName) {
      Sample object = null;
      try {
         FileInputStream file = new FileInputStream(fileName);
         ObjectInputStream flujo = new ObjectInputStream(file);
         object = (Sample) flujo.readObject();
         flujo.close();
         file.close();
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Problem reading sample file " + fileName);
         System.exit(0);
      }

      // Return object
      return object;
   }
   
   /**
    * Compute the list of connected components in the graph
    */
   private void computeComponents(){
      // Makes a copy of the graph
      transformedGraph=new Graph(graph);
      
      // Removes directed links
      if (graph.getNumberArcs() != 0){
         transformedGraph.removeDirectedLinks();
      }
      
      // Now get the components. First at all, get space for the list
      // of components
      components=new ArrayList<HashSet<Integer>>();
      
      // Considers every node (not contained in previous components) and
      // get the component where the node belongs to
      for(int i=0; i < transformedGraph.getNumberVariables(); i++){
         // Check if the node belongs to a component
         if (!isContainedInComponent(i)){            
            // Get the component for i node
            getComponentForNode(i);
         }
      }
   }
   
   /**
    * Private method for computing the cliques for every component
    */
   private void computeCliques(){
      // Gets space for cliques data member
      cliques=new ArrayList<ArrayList<HashSet<Integer>>>();

      for(int i=0; i < components.size(); i++){
         computeCliquesForComponent(components.get(i));
      }
   }
   
   /**
    * Private method for checking if a node belongs to a component
    * @param node
    * @return boolean result
    */
   private boolean isContainedInComponent(int node){
      boolean contained=false;
            
      // Considers every component
      for(Set<Integer> component : components){
         if (component.contains(node)){
            contained=true;
            break;
         }
      }
      
      // Return contained
      return contained;
   }
   
   /**
    * Private method for computing the cliques for a component
    * @param component
    */
   private void computeCliquesForComponent(Set<Integer> component){
      ArrayList<Integer> potentialClique;
      ArrayList<Integer> candidates;
      ArrayList<Integer> alreadyFound;
      
      // Initializes potentialClique list
      potentialClique = new ArrayList<Integer>();

      // Initializes the list of candidates
      candidates = new ArrayList<Integer>();

      // Gets space for list of already found cliques
      alreadyFound = new ArrayList<Integer>();

      // Add all the vertices to candidates
      candidates.addAll(component);

      // Now find the cliques for the component
      ArrayList<HashSet<Integer>> componentCliques=new ArrayList<HashSet<Integer>>();
      
      // It is needed to find the ciques if the number of nodes in the component
      // is bigger than one
      if (component.size() > 1){
         findCliques(componentCliques, potentialClique, candidates, alreadyFound);
      }
      
      // Anywat, the component cliques are inserted into cliques
      cliques.add(componentCliques);
   }
                                                   
   
   /**
    * Private method for getting the component where the node is contained
    * @param node
    */
   private void getComponentForNode(int node){

      // Marks all the nodes already considered
      boolean[] mark = new boolean[transformedGraph.getNumberVariables()];

      // Now creates a queue for the nodes in the component
      Queue<Integer> queue = new LinkedList<Integer>();
      
      // Add the current node to queue
      queue.add(node);
      
      // Mark the node as visited
      HashSet<Integer> visited=new HashSet<Integer>();
      visited.add(node);

      // Considers all the neighbor
      while (!queue.isEmpty()) {
         // Get the current node
         Integer current = queue.remove();

         // Considers the rest of nodes
         for (int j = 0; j < transformedGraph.getNumberVariables(); j++){
            // Check if it is connected to current
            if (transformedGraph.isNeighbor(current,j) && !visited.contains(j)) {
               mark[j] = true;
               visited.add(j);
               queue.add(j);
            }
         }
      }
      
      // Now store the set of nodes marked as visited
      components.add(visited);
   }
   
   /**
     * Find the cliques
     * @param componentCliques
     * @param potentialClique
     * @param candidates
     * @param alreadyFound
     */
   private void findCliques(ArrayList<HashSet<Integer>> componentCliques, 
                            ArrayList<Integer> potentialClique, ArrayList<Integer> candidates,
                            ArrayList<Integer> alreadyFound){
      ArrayList<Integer> candidatesArray = new ArrayList<Integer>(candidates);
      
      // Checks if alreadyFound is contained in candidates
      if (!end(candidates, alreadyFound)) {
         // Checks every node in candidate
         for (Integer candidate : candidatesArray) {
            ArrayList<Integer> newCandidates = new ArrayList<Integer>();
            ArrayList<Integer> newAlreadyFound = new ArrayList<Integer>();

            // Add candidate to potentialClique
            potentialClique.add(candidate);
            candidates.remove(candidate);

            // Creates newCandidates by removing nodes in candidates not
            // connected to candidate node
            for (Integer newCandidate : candidates) {
               if (transformedGraph.isNeighbor(candidate, newCandidate)) {
                  newCandidates.add(newCandidate);
               }
            }

            // Create new_already_found by removing nodes in already_found
            // not connected to candidate node
            for (Integer newFound : alreadyFound) {
               if (transformedGraph.isNeighbor(candidate, newFound)) {
                  newAlreadyFound.add(newFound);
               } // of if
            } // of for

            // If newCandidates and newAlreadyFound are empty
            if (newCandidates.isEmpty() && newAlreadyFound.isEmpty()) {
               // potentialClique is maximal clique
               componentCliques.add(new HashSet<Integer>(potentialClique));
            } 
            else {
               // recursive call
               findCliques(componentCliques,potentialClique, newCandidates, newAlreadyFound);
            }

            // Move candidate node from potentialClique to alreadyFound;
            alreadyFound.add(candidate);
            potentialClique.remove(candidate);
         }
      }
   }
   
    /**
     * Computes the bound taking into account the biggest clique in every
     * component
     */
    private void computeBounds(){
        // Computes upper and lower bounds
        computeLowerBound();
        computeUpperBound();
    }
    
   /**
    * Computes the lower bound taking into account the biggest clique in every
    * component
    */
   private void computeLowerBound(){
       // First at all, get the biggest cliques for every component
       getBiggestCliques();
//System.out.println("Calculados cliques de mayor tamaño.......");       
       // Now compute the bound
       int size;
       for(HashSet<Integer> clique : biggestCliques){
           size=clique.size();
           // There will be a biggest clique for every component and we
           // consider all of them except if they contains a single clique
           if (size > 1){
//System.out.println("Valor inicial de lowerBound: "+lowerBound.doubleValue());               
//System.out.println("Considerando clique de tam: "+size);
               lowerBound=lowerBound.multiply(Utils.computeFactorial(size));
//System.out.println("Acumulado: "+lowerBound.doubleValue());               
           }
       }
   }
   
    /**
     * Computes the lower bound taking into account the biggest clique in every
     * component
     */
    private void computeUpperBound(){
        // Considers the cliques of every component
        // Considers now every component
        
        for(ArrayList<HashSet<Integer>> cliquesForComponent : cliques){
            for(HashSet<Integer> clique : cliquesForComponent){
                if (clique.size() > 1){
                    upperBound=upperBound.multiply(Utils.computeFactorial(clique.size()));
                }
            }
        }
    }
   
   /**
    * Gets the biggest cliques for every component. If there are no cliques
    * or there is a single one, nothing more to do. In the first case introduces
    * an empty clique. 
    */
   private void getBiggestCliques(){
       // Gets space for biggestCliques
       biggestCliques=new ArrayList<HashSet<Integer>>();
       
       // Considers now every component
       for(ArrayList<HashSet<Integer>> cliquesForComponent : cliques){
           // Gets space for the biggest clique
           HashSet<Integer> clique=new HashSet<Integer>();
           
           // If there is a single clique or none, nothing more to do
           if (cliquesForComponent.size() == 0){
               // add clique (empty) to biggestCliques
               biggestCliques.add(clique);
           }
           else{
               // It there is a single one, just add it
               if (cliquesForComponent.size() == 1){
                   clique=cliquesForComponent.get(0);
                   
                   // Add it to biggestCliques
                   biggestCliques.add(clique);
               }
               else{
                   // Considers one by one in order to determine its size
                   int size=0;
                   int index=0;
                   int i=0;
                   for(HashSet<Integer> cliqueInComponent : cliquesForComponent){
                       if (cliqueInComponent.size() > size){
                           size=cliqueInComponent.size();
                           index=i;
                       }
                       
                       // Adds one to i
                       i++;
                   }
                   
                   // Now add the biggesClique
                   biggestCliques.add(cliquesForComponent.get(index));
               }
           }
       }
   }
   
   /**
    * Private method checking if a node in alreadyFound is connected to all
    * the nodes in candidates
    * @param candidates
    * @param alreadyFound
    * @return result of the check
    */
   private boolean end(List<Integer> candidates, List<Integer> alreadyFound) {
      boolean end = false;
      int edgeCounter;

      // Considers every node in alreadyFound
      for (Integer found : alreadyFound) {
         edgeCounter = 0;

         // Considers all the nodes in candidates
         for (Integer candidate : candidates) {
            // Checks if both of them are neighbor
            if (transformedGraph.isNeighbor(found, candidate)) {
               edgeCounter++;
            }
         }

         // Return true if edgeCounter is equals to the size of
         // candidates
         if (edgeCounter == candidates.size()) {
            end = true;
         }
      }

      // Return the boolen result of the check
      return end;
   }   
   
   /**
    * Private method for printing the components of a graph
    */
   private void printComponentsAndCliques(){
      System.out.println("-------------------- Components for graph --------------------");
      graph.print();
      System.out.println("-------------------- Transformed graph ----------------------- ");
      transformedGraph.print();
      System.out.println("--------------------------------------------------------------");
      System.out.println("-------------------- Components ------------------------------");
      System.out.println("Numero de componentes: "+components.size());
      int i=0;
      for(HashSet<Integer> component : components){
         for(Integer node : component){
            System.out.print(node+" ");
         }
         System.out.println();
          
         // Now makes a new loop for printing the cliques of the component
         printCliques(i);
         System.out.println();
          
         // Add one to i
         i++;
      }
   }
   
   /**
    * Prints the cliques for a given component
    * @param indexComponent
    */
   private void printCliques(int indexComponent){
      // Gets the cliques for the component: only if there are cliques
      ArrayList<HashSet<Integer>> cliquesForComponent=cliques.get(indexComponent);
      
      if (cliquesForComponent.size() != 0){
         // Considers every clique
         System.out.println("  ................... CLIQUES .....................");
         for(HashSet<Integer> clique : cliquesForComponent){
            Iterator<Integer> iterator=clique.iterator();
            System.out.print("    ");
            while(iterator.hasNext()){
               System.out.print(iterator.next()+" ");
            }
            System.out.println();
         }
      }
   }
}
