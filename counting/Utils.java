package counting;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.math.BigDecimal;

import java.util.HashMap;

/**
 * Utility class: performs the computations required for estimating
 * the rations after performing the simulation
 * @author mgomez, jmpeña
 */
public class Utils {

   /**
    * Data members to store already computed numbers
    */
   private HashMap<Double, BigDecimal> steinskyValues;
   private HashMap<Double, BigDecimal> robinsonValues;
   private HashMap<Double, BigDecimal> robinsonCValues;
   private HashMap<Double, BigDecimal> bigAValues;
   private HashMap<Double, BigDecimal> smallAValues;

   /**
    * Class constructor
    */
   public Utils() {
      steinskyValues = new HashMap<Double, BigDecimal>();
      robinsonValues = new HashMap<Double, BigDecimal>();
      robinsonCValues = new HashMap<Double, BigDecimal>();
      bigAValues = new HashMap<Double, BigDecimal>();
      smallAValues = new HashMap<Double, BigDecimal>();
   }

   /**
    * Computes (2^(p-s)-p+s)^s
    *
    * @param p
    * @param s
    * @return result
    */
   public BigDecimal computeSteinskyPowValue(double p, double s) {
      BigDecimal result = BigDecimal.valueOf(2);
      result = result.pow((int)(p - s));
      result = result.add(BigDecimal.valueOf((int)(s - p)));
      result = result.pow((int)s);

      // Return result
      return result;
   }

   /**
    * Computes steinsky values
    *
    * @param p
    * @return
    */
   public BigDecimal computeSteinsky(double p) {
      // Result
      BigDecimal result, bin, iterationValue, powValue, steinskyPrev;

      // Declare primitive type variables
      double signValue;
      double key;

      // Check if the value is already computed
      if (steinskyValues.containsKey(p)) {
         result = steinskyValues.get(p);
      } else {
         // Base case
         if (p == 0) {
            result = BigDecimal.valueOf(1);
         } else {
            // Inductive case
            result = BigDecimal.valueOf(0);
            for (int s = 1; s <= p; s++) {
               signValue = sign(s + 1);
               powValue = computeSteinskyPowValue(p, s);

               // Computes the binomial factorial of p over s
               bin = computeBinomialCoefficient(p, (double)s);

               // Check if it is computed the value
               key = p - s;
               if (steinskyValues.containsKey(key)) {
                  steinskyPrev = steinskyValues.get(key);
               } else {
                  steinskyPrev = computeSteinsky(key);

                  // Stores the value
                  steinskyValues.put(key, steinskyPrev);
               }

               // Multiply all of these values
               iterationValue = powValue.multiply(bin);
               iterationValue = iterationValue.multiply(BigDecimal.valueOf(signValue));
               iterationValue = iterationValue.multiply(steinskyPrev);

               // Increments on result
               result = result.add(iterationValue);
            }

            // Store the result
            steinskyValues.put(p, result);
         }
      }

      // Return result
      return result;
   }

   /**
    * Computes robinson values
    *
    * @param p
    * @return
    */
   public BigDecimal computeRobinson(double p) {
      // Result
      BigDecimal result, bin, iterationValue, powValue, robinsonPrev;

      // Declare primitive type variables
      double signValue;
      double key;

      // Check if the value is already computed
      if (robinsonValues.containsKey(p)) {
         result = robinsonValues.get(p);
      } else {
         // Base case
         if (p == 0) {
            result = BigDecimal.valueOf(1);
         } else {
            // Inductive case
            result = BigDecimal.valueOf(0);
            for (int s = 1; s <= p; s++) {
               signValue = sign(s + 1);
               powValue = computesRobinsonPowValue(p, (double)s);

               // Computes the binomial factorial of p over s
               bin = computeBinomialCoefficient(p, (double)s);

               // Check if it is computed the value
               key = p - s;
               if (robinsonValues.containsKey(key)) {
                  robinsonPrev = robinsonValues.get(key);
               } else {
                  robinsonPrev = computeRobinson(key);

                  // Stores the value
                  robinsonValues.put(key, robinsonPrev);
               }

               // Multiply all of these values
               iterationValue = powValue.multiply(bin);
               iterationValue = iterationValue.multiply(BigDecimal.valueOf(signValue));
               iterationValue = iterationValue.multiply(robinsonPrev);

               // Increments on result
               result = result.add(iterationValue);
            }

            // Store the value computed
            robinsonValues.put(p, result);
         }
      }

      // Return result
      return result;
   }

   /**
    * Computes ratio value
    * @param p
    * @return
    */
   public BigDecimal computeRatio(double p) {
      return computeSteinsky(p).divide(computeRobinson(p), 10, BigDecimal.ROUND_HALF_EVEN);
   }

   /**
    * Computes ratioC value
    * @param p
    * @return
    */
   public BigDecimal computeRatioC(double p) {
      return computeSteinsky(p).divide(computeRobinsonC(p), 10, BigDecimal.ROUND_HALF_EVEN);
   }

   /**
    * Computes robinsonC value
    * @param p
    * @return
    */
   public BigDecimal computeRobinsonC(double p) {
      BigDecimal result, smallAValue, factorial;

      // Checks if the valua was previously computed
      if (robinsonCValues.containsKey(p)) {
         result = robinsonCValues.get(p);
      } else {
         // It is needed to compute the value
         if (smallAValues.containsKey(p)) {
            smallAValue = smallAValues.get(p);
         } else {
            smallAValue = computeSmallA(p);
         }

         // Now it is needed to compute the factorial of p
         factorial = computeFactorial(p);

         // Multiplies them
         result = smallAValue.multiply(factorial);

         // Stores the value
         robinsonCValues.put(p, result);
      }

      // Return result
      return result;
   }

   /**
    * Computes the value of bigA
    * @param p
    * @return result
    */
   public BigDecimal computeBigA(double p) {
      BigDecimal result, robinsonValue, factorial;

      // Checks if the valua was previously computed
      if (bigAValues.containsKey(p)) {
         result = bigAValues.get(p);
      } else {
         // It is needed to compute the value
         if (robinsonValues.containsKey(p)) {
            robinsonValue = robinsonValues.get(p);
         } else {
            robinsonValue = computeRobinson(p);
         }

         // Now it is needed to compute the factorial of p
         factorial = computeFactorial(p);

         // Divides them
         result = robinsonValue.divide(factorial, 10, BigDecimal.ROUND_HALF_EVEN);

         // Stores the value
         bigAValues.put(p, result);
      }

      // Return result
      return result;
   }

   /**
    *Computes the value of smallA
    * @param m
    * @return
    */
   public BigDecimal computeSmallA(double m) {
      // Result
      BigDecimal result, summ, smallAPrev, bigAPrev;

      // Declare primitive type variables
      int key;

      // Check if the value is already computed
      if (smallAValues.containsKey(m)) {
         result = smallAValues.get(m);
      } else {
         // Base case
         if (m == 0) {
            result = BigDecimal.valueOf(1);
         } else {
            // Inductive case
            result = computeBigA(m);
            summ = BigDecimal.valueOf(0);

            // Loop
            for (int k = 1; k < m; k++) {
               smallAPrev = computeSmallA(k);
               bigAPrev = computeBigA(m - k);
               bigAPrev = bigAPrev.multiply(new BigDecimal(k));

               // Increments on sum
               summ = summ.add(smallAPrev.multiply(bigAPrev));
            }

            // Now divide summ by m
            summ = summ.divide(new BigDecimal(m), 10, BigDecimal.ROUND_HALF_EVEN);

            // Now substract from result
            result = result.subtract(summ);

            // Store the result
            smallAValues.put(m, result);
         }
      }

      // Return result
      return result;
   }

   /**
    * Computes the binomial coefficient using bigdecimal values
    * @param n
    * @param k
    * @return
    */
   public BigDecimal computeBinomialCoefficient(double n, double k) {
      BigDecimal result = BigDecimal.valueOf(1);

      // Computes if needed
      for (int i = 1; i <= k; i++) {
         result = result.multiply(BigDecimal.valueOf(n - i + 1)).divide(BigDecimal.valueOf(i));
      }

      // return result
      return result;
   }

   /**
    * Computes the factorial of a number
    * @param n
    * @return
    */
   public static BigDecimal computeFactorial(double n) {
      BigDecimal result = BigDecimal.valueOf(1);

      // Computes if needed
      for (int i = 2; i <= n; i++) {
         result = result.multiply(BigDecimal.valueOf(i));
      }

      // Return result
      return result;
   }

   /**
    * Computes the ratios and prints their values
    * @param numVariables
    * @param numSamples
    * @param directedGraphs
    * @param connectedGraphs
    */
   public void computeValues(int numVariables, int numSamples, int directedGraphs, int connectedGraphs) {
      double ratio1, ratio2, ratio3, ratio4, ratio5;
      ratio1 = compute_nEGs_nDAGsRatio(numVariables, numSamples, directedGraphs);
      ratio2 = compute_nEDAGs_nEGs(directedGraphs, numSamples);
      ratio3 = compute_nEGsC_nDAGsC(numVariables, directedGraphs, connectedGraphs);
      ratio4 = compute_nEGsC_nEGs(connectedGraphs, numSamples);
      ratio5 = compute_nDAGsC_nDAGs(numVariables);
      System.out.println("nEGs/nDAGs --- nEDAGs/nEGs --- nEGsC/nDAGsC --- nEGsC/nEGs --- nDAGsC/nDAGs");
      System.out.println(ratio1 + "  " + ratio2 + "  " + ratio3 + "  " + ratio4 + "  " + ratio5);
   }

   /**
    * Computes nEGs/nDAGs ratio
    * @param numVariables
    * @param numSamples
    * @param directedGraphs
    * @return
    */
   public double compute_nEGs_nDAGsRatio(double numVariables, double numSamples, double directedGraphs) {
      BigDecimal samples = new BigDecimal(numSamples);
      BigDecimal directed = new BigDecimal(directedGraphs);
      BigDecimal result = samples.multiply(computeRatio(numVariables));
      result = result.divide(directed, 60, BigDecimal.ROUND_HALF_EVEN);

      // Return the result
      return result.doubleValue();
   }

   /**
    * Computes nEDAGs/nEGs ratio
    * @param directedGraphs
    * @param numSamples
    * @return
    */
   public double compute_nEDAGs_nEGs(double directedGraphs, double numSamples) {
      return (double) directedGraphs / numSamples;
   }

   /**
    * Computes the nEGsC/nDAGsC ratio
    * @param numVariables
    * @param directedGraphs
    * @param connectedGraphs
    * @return
    */
   public double compute_nEGsC_nDAGsC(double numVariables, double directedGraphs, double connectedGraphs) {
      BigDecimal result = computeRatioC(numVariables);

      // Now multiply by the number of connected graphs
      result = result.multiply(new BigDecimal(connectedGraphs));

      // Finally divide by the number of directed graphs
      result = result.divide(new BigDecimal(directedGraphs), 10, BigDecimal.ROUND_HALF_EVEN);

      // Finalyy return result
      return result.doubleValue();
   }

   /**
    * Computes nEGsC/nEGs ratio
    * @param connectedGraphs
    * @param numSamples
    * @return
    */
   public double compute_nEGsC_nEGs(double connectedGraphs, double numSamples) {
      return (double) connectedGraphs / numSamples;
   }

   /**
    * Computes nDAGsC/nDAGs ratio
    * @param numVariables
    * @return
    */
   public double compute_nDAGsC_nDAGs(double numVariables) {
      BigDecimal result =
         computeRobinsonC(numVariables).divide(computeRobinson(numVariables), 10, BigDecimal.ROUND_HALF_EVEN);
      return result.doubleValue();
   }
   
   

   /**
    * Computes the value of -1^number
    *
    * @param number
    * @return
    */
   private double sign(int number) {
      double res;
      if (number % 2 == 0) {
         res = 1;
      } else {
         res = -1;
      }

      // Return res
      return res;
   }

   /**
    * Computes (2^(s*(p-s)))
    *
    * @param p
    * @param s
    * @return
    */
   private BigDecimal computesRobinsonPowValue(double p, double s) {
      BigDecimal result = BigDecimal.valueOf(2);
      result = result.pow((int)(s * (p - s)));

      // Return result
      return result;
   }

   /**
    * Main method for computing the ratios computed in the
    * Mathemathica notebook
    */
   public static void main(String args[]) {
      if (args.length != 4) {
         System.out.printf("\n Program use: ");
         System.out.printf("\n java counting.Utils n_vars n_samples nEDAGs nEGsC\n");
         System.exit(0);
      }

      // En caso de llamarse de forma correcta, se leen los argumentos
      int numVariables = Integer.parseInt(args[0]);
      int numSamples = Integer.parseInt(args[1]);
      int directedGraphs = Integer.parseInt(args[2]);
      int connectedGraphs = Integer.parseInt(args[3]);

      // Creates a clas object
      Utils object = new Utils();

      // Computes the required values
      object.computeValues(numVariables, numSamples, directedGraphs, connectedGraphs);

      /*for(int i=2; i < 11; i++){
         System.out.print(object.computeSteinsky(i)+"  ");
         System.out.print(object.computeRobinson(i)+"  ");
         System.out.print(object.computeRobinsonC(i)+"  ");
         System.out.print(object.computeRatio(i)+"  ");
         System.out.print(object.computeRatioC(i)+"  ");
         System.out.print(object.computeSmallA(i)+"  ");
         System.out.println(object.computeBigA(i));
         System.out.println();
      }*/
   }
}
