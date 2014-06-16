package counting;

import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;


/**
 * Class for generating a table with the results of the simulation
 */
public class ResultsGenerator {
    // Enumeration for column names

    private enum ColumnNames {
        DGProp, // Directed graphs proportion (respect to graphs)
        CGProp, // Connected graphs proportion (respect to graphs)
        CDGPropD, // Connected and directed graphs proportion (respect to directed graphs)
        CDGPropC, // Connected and directed graphs proportion (respect to connected graphs)
        Time,
        Eq, // average probabilities for the operations
        ALink,
        DLink,
        AArc,
        DArc,
        AVStr,
        DVStr,
        Valid,
        LinksProp, // average percentage of links in the whole set of graphs
        ArcsProp, // average percentage of arcs in the whole set of graphs
        LowerBound, // lower bound of the number of graphs represented by the essential graphs
        UpperBound, // upper bpund of the number of graphs represented by the eseential graph
        NEGS_NDAGS, // nEGs / nDAGs
        NEDAGS_NEGS, // nEDAGs/nEGs
        NEGSC_NDAGSC, // nEGsC/nDAGsC
        NEGSC_NEGS, // nEGsC/nEGs/nDAGs
        NDAGSC_NDAGS // nDAGsC
    }

    /**
    * Data members description
    */

    /**
     * Data member to store the list of columns to display
     */
    private ArrayList<Boolean> columnsToDisplay;

    /**
     * Data member to store the number of columns to display
     */
    private int numberOfColumns;

    /**
     * Data member to store the initial number of variables
     */
    private int numVarsIni;

    /**
     * Data member to store the final number of variables
     */
    private int numVarsEnd;

    /**
     * Data member to store the number of operations
     */
    private int numOperations;

    /**
     * Data member to store the number of graphs
     */
    private int numGraphs;

    /**
     * Data member to store the name of the result file
     */
    private String fileName;

    /**
     * Data member to store the file of the results
     */
    private FileOutputStream file;

    /**
     * Stream for writing to the file
     */
    private PrintStream pFile;

    /**
     * Stores the array list with the samples processors
     */
    private ArrayList<SamplesProcessor> processors;

    /**
     * Class constructor
     * @param numVarsIni initial number of variables
     * @param numVarsEnd final number of variables
     * @param numIterations number of operations to produce a graph
     * @param numSamples number of generated graphs
     * @param columnsToDisplay list showing the columns to display
     */
    public ResultsGenerator(int numVarsIni, int numVarsEnd, int numIterations, int numSamples,
                            ArrayList<Boolean> columnsToDisplay) {
        // Sets numVarsIni
        this.numVarsIni = numVarsIni;

        // Sets numVarsEnd
        this.numVarsEnd = numVarsEnd;

        // Sets numOperations
        numOperations = numIterations;

        // Sets numGraphs
        numGraphs = numSamples;

        // Sets the list of columns to display
        this.columnsToDisplay = columnsToDisplay;

        // Gets the number of columns
        getNumberOfColumns();

        // Generate the file for the results
        generateFile();

        // Process the results
        processResults();

        // Now generate the table
        String table=generateLatexTable();
        
        // Finally, print the table
        System.out.println("TABLE: -------------------------------------- ");
        System.out.println(" Columns: ");
        for (int i = 0; i < columnsToDisplay.size(); i++) {
            if (columnsToDisplay.get(i)){
               System.out.println("  " + (ColumnNames.values())[i].toString() + " : " + columnsToDisplay.get(i));
            }
        }
        System.out.println();
        System.out.println(table);
    }

    /**
     * Private method for analyzing the list of columns to display
     * in order to determine its number
     */
    private void getNumberOfColumns() {
        numberOfColumns = 0;
        for (int i = 0; i < columnsToDisplay.size(); i++) {
            if (columnsToDisplay.get(i)) {
                numberOfColumns++;
            }
        }
    }

    /**
     * Private method for generating and openning the file with the results
     */
    private void generateFile() {
        // Composte the name of the results file
        fileName = "result" + numVarsIni + "-" + numVarsEnd + "-" + numOperations + "-" + numGraphs + ".txt";

        // Open the file
        try {
            file = new FileOutputStream(fileName);
            pFile = new PrintStream(file);
        } catch (Exception e) {
            System.out.println("Problem opening output file.....");
            System.exit(-1);
        }
    }

    /**
     * Private method for processing the results for the files
     */
    private void processResults() {
        SamplesProcessor processor;

        // Gets stapce for the list of processors
        processors = new ArrayList<SamplesProcessor>();

        // Considers every number of variables
        for (int n = numVarsIni; n <= numVarsEnd; n++) {
            System.out.println("------------- Variables "+n+" ------------");
            // Creates the processor for n
            processor = new SamplesProcessor(n, numOperations, numGraphs, false);

            // Process the results
            processor.process();

            // Store the corresponding processor
            processors.add(processor);
        }
    }

    /**
     * Private method for generating the latex table with the results
     */
    private String generateLatexTable() {
        String table = generateLatexTableHeader();

        // Now generates the lines for every number of nodes
        for (int i = numVarsIni; i <= numVarsEnd; i++) {
            // Generate the results for this number of vars
            table=table.concat(generateResultsForNumberOfNodes(i));
        }
        table=table.concat("\\end{tabular}\n");

        // Return table
        return table;
    }

    /**
     *Private method for generating the first two lines of the table
     * @return two first lines
     */
    private String generateLatexTableHeader() {
        String result = "\\begin{tabular}{|c|";
        String columnNames = " Nodes & ";

        for (int i = 0, j=0; i < columnsToDisplay.size(); i++) {
            if (columnsToDisplay.get(i)) {
                result=result.concat("c|");
                columnNames=columnNames.concat((ColumnNames.values())[i].name());
                if (j < numberOfColumns-1){
                   columnNames=columnNames+" & ";
                }
                j++;
            }
        }
        result=result.concat("}\\hline\n");
        columnNames=columnNames.concat("\\\\\\hline\n");
        result=result.concat(columnNames);

        // Return the header
        return result;
    }

    /**
     * Generates the row of results for a given number of nodes
     * @param number of nodes
     * @return line of results
     */
    private String generateResultsForNumberOfNodes(int number) {
        String result = ""+number + " & ";
        ColumnNames name;
        Utils utilObject=new Utils();
        double directed, connected, connectedDirected, graphs;
        double generationTime, equalProb, delLinkProb, addLinkProb;
        double addArcProb, delArcProb, addVStructProb, delVStructProb;
        double validOpProb, linksProb, arcsProb;
        double lowerBound, upperBound;
        double ratio;

        // Gets the processor for this number of nodes
        SamplesProcessor processor = processors.get(number-numVarsIni);

        // Gets the number of graphs
        graphs = processor.getNumberSamples();

        // Considers every column to display
        for (int i = 0, j=0; i < columnsToDisplay.size(); i++) {
            if (columnsToDisplay.get(i)) {
                // Considers the corresponding value
                name = (ColumnNames.values())[i];
                switch (name) {
                case DGProp:
                    directed = processor.getCumDirected();
                    result=result.concat(String.format("%6.5f", (directed / graphs)));
                    break;
                case CGProp:
                    connected = processor.getCumConnected();
                    result=result.concat(String.format("%6.5f", (connected / graphs)));
                    break;
                case CDGPropD:
                    connectedDirected = processor.getCumDirectedConnected();
                    directed = processor.getCumDirected();
                    result=result.concat(String.format("%6.5f", (connectedDirected / directed)));
                    break;
                case CDGPropC:
                    connectedDirected = processor.getCumDirectedConnected();
                    connected = processor.getCumConnected();
                    result=result.concat(String.format("%6.5f", (connectedDirected / connected)));
                    break;
                case Time:
                    generationTime = processor.getGenerationTime();
                    result=result.concat(String.format("%8.4e", generationTime/(3600000.0)));
                    break;
                case Eq:
                    equalProb = processor.getGlobalEqualProb();
                    result=result.concat(String.format("%6.5f", equalProb/graphs));
                    break;
                case ALink:
                    addLinkProb = processor.getGlobalAddLinkProb();
                    result=result.concat(String.format("%6.5f", addLinkProb/graphs));
                    break;
                case DLink:
                    delLinkProb = processor.getGlobalDeleteLinkProb();
                    result=result.concat(String.format("%6.5f", delLinkProb/graphs));
                    break;
                case AArc:
                    addArcProb = processor.getGlobalAddArcProb();
                    result=result.concat(String.format("%6.5f", addArcProb/graphs));
                    break;
                case DArc:
                    delArcProb = processor.getGlobalDeleteArcProb();
                    result=result.concat(String.format("%6.5f", delArcProb/graphs));
                    break;
                case AVStr:
                    addVStructProb = processor.getGlobalAddVStructureProb();
                    result=result.concat(String.format("%6.5f", addVStructProb/graphs));
                    break;
                case DVStr:
                    delVStructProb = processor.getGlobalDeleteVStructureProb();
                    result=result.concat(String.format("%6.5f", delVStructProb/graphs));
                    break;
                case Valid:
                    validOpProb =
                        processor.getGlobalEqualProb() + processor.getGlobalAddArcProb() +
                        processor.getGlobalDeleteArcProb() + processor.getGlobalAddLinkProb() +
                        processor.getGlobalDeleteLinkProb() + processor.getGlobalAddVStructureProb() +
                        processor.getGlobalDeleteVStructureProb();
                    result=result.concat(String.format("%6.5f", validOpProb/graphs));
                    break;
                case LinksProp: linksProb=processor.getLinksProb();
                    result=result.concat(String.format("%6.5f", linksProb/graphs));
                    break;
                case ArcsProp: arcsProb=processor.getArcsProb();
                    result=result=result.concat(String.format("%6.5f", arcsProb/graphs));
                    break;
                case LowerBound: lowerBound=processor.getLowerBound().doubleValue();
                    result=result.concat(String.format("%8.4e", lowerBound/graphs));
                    break;
                case UpperBound: upperBound=processor.getUpperBound().doubleValue();
                    result=result.concat(String.format("%8.4e", upperBound/graphs));
                    break;
                case NEGS_NDAGS: directed = processor.getCumDirected();
                    ratio=utilObject.compute_nEGs_nDAGsRatio(number, graphs, directed);
                    result=result.concat(String.format("%6.5f", ratio));
                    break;
                case NEDAGS_NEGS: directed=processor.getCumDirected();
                    ratio=utilObject.compute_nEDAGs_nEGs(directed,graphs);
                    result=result.concat(String.format("%6.5f", ratio));
                    break;
                case NEGSC_NDAGSC: connected=processor.getCumConnected();
                    directed=processor.getCumDirected();
                    ratio=utilObject.compute_nEGsC_nDAGsC(number, directed, connected);
                    result=result.concat(String.format("%6.5f", ratio));
                    break;
                case NEGSC_NEGS: connected=processor.getCumConnected();
                    ratio=utilObject.compute_nEGsC_nEGs(connected, graphs);
                    result=result.concat(String.format("%6.5f", ratio));
                    break;
                case NDAGSC_NDAGS: ratio=utilObject.compute_nDAGsC_nDAGs(number);
                    result=result.concat(String.format("%6.5f", ratio));
                    break;
                }

                if (j != numberOfColumns-1) {
                    result=result.concat(" & ");
                } 
                else {
                    result=result.concat("\\\\\\hline\n");
                }
                j++;
            }
        }
        // return result
        return result;
    }

    /**
     * Main method
     */
    public static void main(String args[]) {
        // Check the correct number of arguments
        if (args.length < 4) {
            System.out.printf("\n Program use: ");
            System.out.printf("\n java counting.ResultsGenerator n_vars_ini n_vars_end n_its n_samples [-r c1 ... cn] \n");
            // Print the number identifying the columns to generate
            for (int i = 0; i < ColumnNames.values().length; i++) {
                System.out.println("  " + i + " - " + (ColumnNames.values())[i].toString());
            }
            System.exit(0);
        }

        // Gets the space for the array of columns to be displayed
        ArrayList<Boolean> columnsToDisplay = new ArrayList<Boolean>();

        // Gets the values for the arguments
        int numVarsIni = Integer.parseInt(args[0]);
        int numVarsEnd = Integer.parseInt(args[1]);
        int numIterations = Integer.parseInt(args[2]);
        int numSamples = Integer.parseInt(args[3]);
        boolean all = true;

        // Check if -r option is present
        if (args.length >= 4) {
            // Set all to false
            all = false;

            // Read the number of the columns to remove();
            ArrayList<Integer> columnsToRemove = new ArrayList<Integer>();
            for (int i = 5; i < args.length; i++) {
                columnsToRemove.add(Integer.parseInt(args[i]));
            }

            // Now compose all the columns to display
            boolean toInclude;
            for (int i = 0; i < ColumnNames.values().length; i++) {
                // Sets true toInclude
                toInclude = true;
                // Check if the column is include in columnsToRemove
                for (int j = 0; j < columnsToRemove.size(); j++) {
                    if (columnsToRemove.get(j) == i) {
                        toInclude = false;
                        break;
                    }
                }
                // Sets the value of the flag to columnsToDisplay
                columnsToDisplay.add(toInclude);
            }
        }

        // Prints the columns to be displayed
        System.out.println("-------------------- COLUMNS TO DISPLAY ----------------------");
        for (int i = 0; i < columnsToDisplay.size(); i++) {
            System.out.println("  " + (ColumnNames.values())[i].toString() + " : " + columnsToDisplay.get(i));
        }

        // Creates and object of the class
        ResultsGenerator object =
            new ResultsGenerator(numVarsIni, numVarsEnd, numIterations, numSamples, columnsToDisplay);
    }
}
