
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.text.DecimalFormat;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;

public class KNearest_neighbour {

	// specify the names of both train and test datasets and store them in a string
    private static final String datasetTrain = "breast_cancer_train.csv";
    private static final String datasetTest = "breast_cancer_test.csv";

    public static void main(String[] args) throws IOException {

    	// set up a Scanner object for both train and test datasets (for reading the values)
        Scanner readTrain = new Scanner(new File(datasetTrain));
        Scanner readTest = new Scanner(new File(datasetTest));
        // set up an arraylist of row objects (another class)
        ArrayList<row> trainValues = new ArrayList<row>();
        ArrayList<row> testValues = new ArrayList<row>();

        int k = readInput(); // call the readInput function to ask the user to enter the value of k

        /*
        // call the loadDataSet function with the Scanner object as a parameter and store
        // the returned arrayList in the previously created arrayList of row objects
        */ 
        trainValues = loadDataSet(readTrain);
        testValues = loadDataSet(readTest);

        /*
        // call the normalize function with the trainValues arrayList as a parameter
        // the normalize function is used to return an updated arraylist with centered 
        // and rescaled values
        */ 
        trainValues = normalize(trainValues);
        /*
        // tp = true positive, tn = true negative, fp = false positive, fn = false negative
        // pYes = predicted yes, pNo = predicted no, aYes = actual Yes, aNo = actual No
        // values are being declared, will be used later on to construct a confusion matrix
        // and indicate performance
        */
            int tp = 0; int tn = 0; int fp = 0; int fn = 0;
            int pYes = 0; int pNo = 0; int aYes = 0; int aNo = 0;

            DecimalFormat df = new DecimalFormat("#.##"); // DecimalFormat object for rounding up the performance indicator values

        /*
        // Compare each row in the test dataset to every single row in the train dataset
        // For every row, calculate the distance between that test dataset row and each train dataset row
        // Update each train dataset row's distance property by their  euclidean distance (row.dist) to that test dataset row
        // Once all rows have been compared, sort the arrayList by their euclidean distance (lowest-highest)
        // row.get(0).dist will now store the lowest euclidean distance between that test row and corresponding row from test dataset
        // After sorting is complete, get the lowest euclidean distances that correspond to each row in the test dataset
        // depending on k
        // k = 1 = get.(0).cla returns the lowest euclidean distance row from the train dataset to the test row
        // k = 3 = get.(0).cla, get.(1).cla, get(2).cla would return three lowest euclidean distances
        // In such case that k is more than 1, a vote was set up to increase either value of a or b depending on what
        // the class of that row is. This way the most common class out of k can be voted and used as a predicted class
        */
        for (int i = 0; i < testValues.size(); i++) {
            for (int j = 0; j < trainValues.size(); j++) {
                double distance = getEuclideanDistance(testValues.get(i), trainValues.get(j)); // store the calculated distance
                trainValues.get(j).setDistance(distance); // set that distance for each row (distance between the test dataset row and particular train dataset row)
                }
                
            Collections.sort(trainValues, new Comparator<row>() {
              @Override
              public int compare(row c1, row c2) {
                  return Double.compare(c1.getDistance(), c2.getDistance()); // sort the arrayList by the distance attribute of the object (lowest-highest)
              }
            });

            int a = 0;
            int b = 0;

            for (int l = 0; l < k; l++) { // get k (closest by euclidean distance) nearest neighbours
            	if (trainValues.get(l).cla.equals("\"malign\"")) {
            		a++; // if one of the classes that have the lowest euclidean distance equal to "malign", increase a by one
            	} else { // otherwise, increase b by one
            		b++;
            	}
            }

            	if (a > b) { // if there were more votes for a (malign), predicted class for that test dataset row is malign
            		if (testValues.get(i).cla.equals("\"malign\"")) {
            			tp++; // increase true positive by one (both predicted and actual)
            		} else if (testValues.get(i).cla != "\"malign\"") {
            			fp++; // increase false positive by one (predicted but not actual)
            		}
            		pYes++; // predicted yes increment by one
            		testValues.get(i).setPredictedClass("\"malign\"");
            	} else { // otherwise, the predicted class is benign
            		testValues.get(i).setPredictedClass("\"benign\"");
            		if (testValues.get(i).cla.equals("\"benign\"")) {
            			tn++; // increase true negative (both predicted and actual)
            		} else if (testValues.get(i).cla != "\"benign\"") {
            			fn++; // increase false negative (predicted but not actual)
            		}
            		pNo++; // predicted no increment by one
            	}
            }

            // for loop used to count the actual malign and benign classes from whole dataset
            for (int i = 0; i < testValues.size(); i++) {
            	if (testValues.get(i).cla.equals("\"malign\"")) {
            		aYes++;
            	} else {
            		aNo++;
            	}
            }

            System.out.println("The value of k is " + k);
            System.out.println("\n" + "Confusion matrix");
            System.out.printf("total" + ": " + (tn + fp + fn + tp) + "     |" + "Predicted: benign" + " |" + "Predicted: malign" + " |");
            System.out.println("");
            System.out.printf("%s%11d%14d%16d\n","Actual: benign |", tn, fp, aNo);
            System.out.printf("%s%10d%16d%14d\n","Actual: malign |", fn, tp, aYes);
            System.out.printf("%27d%15d\n", pNo, pYes);

            System.out.println("\n");
            System.out.println("Accuracy: " + df.format((double) (tp + tn) / testValues.size()));
            System.out.println("Sensitivity: " + df.format((double) tp / aYes));
            System.out.println("Precision: " + df.format((double) tp / pYes));
            System.out.println("Specificity: " + df.format((double) tn / aNo));

            //writeDataSet(testValues); // uncomment, then edit the file name in writeDataSet function to write to csv file
    }

    public static int readInput() {
    	Scanner reader  = new Scanner(System.in);  // set up Scanner to read input from keyboard
                
                int nnK = 0;
                
                do {
                System.out.println("Please enter the number of nearest neighbours K below (must be a positive integer)");
                
                while (!reader.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a positive integer.");
                    reader.next();
                }
                
                nnK = reader.nextInt();
                } while (nnK <= 0);

                return nnK;
    }
    

    public static ArrayList<row> loadDataSet(Scanner scanner) {
        
        ArrayList<row> inputValues = new ArrayList<row>();
        
        scanner.nextLine(); // skip the first line (header is unecessary)
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine(); // store each row from the csv document in a single string
            String[] lines = line.split(","); // split up the whole row into different values by a comma
            // create a new object for every row with each property being the column (pid, clump_thickness, etc). Store this in an arrayList
            inputValues.add(new row((int) Double.parseDouble(lines[0]), (int) Double.parseDouble(lines[1]), (int) Double.parseDouble(lines[2]), (int) Double.parseDouble(lines[3])
                    , (int) Double.parseDouble(lines[4]), (int) Double.parseDouble(lines[5]), (int) Double.parseDouble(lines[6]), (int) Double.parseDouble(lines[7])
                    , (int) Double.parseDouble(lines[8]), (int) Double.parseDouble(lines[9]), lines[10]));
            }
        
        return inputValues; // return the arrayList of row objects
    }

    // method used to write data to a csv file (comma separated)
    public static void writeDataSet(ArrayList<row> testValues) throws IOException {

        String outputFile = "predictions.csv"; // name of the file to create. Make sure no such file exists first

        CSVWriter csvOutput = new CSVWriter(new FileWriter(outputFile, true), ',');

        String[] topRow = "PID, CLUMP_THICKNESS, CELL_SIZE, CELL_SHAPE, MARG_ADESION, EPIT_CELL_SIZE, BARE_NUCLEI, BLAND_CHROM, N_NUCLEOLI, MITOSES, CLASS, PREDICTED_CLASS".split(",");
        csvOutput.writeNext(topRow);
        for (int i = 0; i < testValues.size(); i++) { // for each row in the test dataset, store the row's properties in a csv file 
            String ro = (int) testValues.get(i).pid + ", " + (int) testValues.get(i).ct + ", "
            + (int) testValues.get(i).csi + ", " + (int) testValues.get(i).csh + ", " + (int) testValues.get(i).ma
            + ", " + (int) + testValues.get(i).ec + ", " + (int) testValues.get(i).bn + ", " + (int) testValues.get(i).bc + ", "
            + (int) testValues.get(i).nn
            + ", " + (int) + testValues.get(i).mit + ", " + testValues.get(i).cla + ", " + testValues.get(i).pcla;
            ro = ro.replace("\"", ""); // remove the quotation marks from the string (for classes) 
            String[] row = ro.split(","); // split the columns by a comma
            csvOutput.writeNext(row); // write the data  
        }
        csvOutput.close(); // end stream
    }
    
    public static double getEuclideanDistance(row testValues, row trainValues) {
    	// calculate the euclidean distance between the test dataset row and the train dataset row
            double distance = Math.pow((testValues.ct - trainValues.ct), 2) + Math.pow((testValues.csi - trainValues.csi), 2)
                    + Math.pow((testValues.csh - trainValues.csh), 2) + Math.pow((testValues.ma - trainValues.ma), 2)
                    + Math.pow((testValues.ec - trainValues.ec), 2) + Math.pow((testValues.bn - trainValues.bn), 2)
                    + Math.pow((testValues.bc - trainValues.bc), 2) + Math.pow((testValues.nn - trainValues.nn), 2)
                    + Math.pow((testValues.mit- trainValues.mit), 2);

            return Math.sqrt(distance); // return the square root of that value
        }

        /*
        // This function is used to center and rescale each of the values by column 
        // It takes in a non-centered/non-rescaled arrayList of row objects 
        // and returns centered and rescaled arrayList of row objects
        */
        public static ArrayList<row> normalize(ArrayList<row> trainValues) {
    	double sumCt = 0; double sumCsi = 0; double sumCsh = 0; double sumMa = 0; double sumEc = 0; double sumBn = 0; double sumBc = 0; double sumNn = 0; double sumMit = 0; 
    	double meanCt = 0; double meanCsi = 0; double meanCsh = 0; double meanMa = 0; double meanEc = 0; double meanBn = 0; double meanBc = 0; double meanNn = 0; double meanMit = 0;
    	
    	// calculate the sum (used to calculate mean) for each column 
    	for (int x = 0; x < trainValues.size(); x++) {
    		sumCt = sumCt + trainValues.get(x).ct; // ct = clump_thickness
    		sumCsi = sumCsi + trainValues.get(x).csi; // csi = cell_size
    		sumCsh = sumCsh + trainValues.get(x).csh; // csh = cell_shape
    		sumMa = sumMa + trainValues.get(x).ma; // ma = marg_adesion
    		sumEc = sumEc + trainValues.get(x).ec; // ec = epit_cell_size
    		sumBn = sumBn + trainValues.get(x).bn; // bn = bare_nuclei
    		sumBc = sumBc + trainValues.get(x).bc; // bc = bland_chrom
    		sumNn = sumNn + trainValues.get(x).nn; // nn =n_nucleoli
    		sumMit = sumMit + trainValues.get(x).mit; // mit = mitoses
            }
            // calculate the mean for each column
            meanCt = sumCt/trainValues.size();
            meanCsi = sumCsi/trainValues.size();
            meanCsh = sumCsh/trainValues.size();
            meanMa = sumMa/trainValues.size();
            meanEc = sumEc/trainValues.size();
            meanBn = sumBn/trainValues.size();
            meanBc = sumBc/trainValues.size();
            meanNn = sumNn/trainValues.size();
            meanMit = sumMit/trainValues.size();

            // go through each value again, minus the mean and square it by two, update the value
        for (int x = 0; x < trainValues.size(); x++) {
    		trainValues.get(x).ct = Math.pow(trainValues.get(x).ct - meanCt, 2);
    		trainValues.get(x).csi = Math.pow(trainValues.get(x).csi - meanCsi, 2);
    		trainValues.get(x).csh = Math.pow(trainValues.get(x).csh - meanCsh, 2);
    		trainValues.get(x).ma = Math.pow(trainValues.get(x).ma - meanMa, 2);
    		trainValues.get(x).ec = Math.pow(trainValues.get(x).ec - meanEc, 2);
    		trainValues.get(x).bn = Math.pow(trainValues.get(x).bn - meanBn, 2);
    		trainValues.get(x).bc = Math.pow(trainValues.get(x).bc - meanBc, 2);
    		trainValues.get(x).nn = Math.pow(trainValues.get(x).nn - meanNn, 2);
    		trainValues.get(x).mit = Math.pow(trainValues.get(x).mit - meanMit, 2);
            }

            sumCt = 0; sumCsi = 0; sumCsh = 0; sumMa = 0; sumEc = 0; sumBn = 0; sumBc = 0; sumNn = 0; sumMit = 0;
        
        // store a new sum for each column due to updated values
        for (int x = 0; x < trainValues.size(); x++) {
    		sumCt = sumCt + trainValues.get(x).ct;
    		sumCsi = sumCsi + trainValues.get(x).csi;
    		sumCsh = sumCsh + trainValues.get(x).csh;
    		sumMa = sumMa + trainValues.get(x).ma;
    		sumEc = sumEc + trainValues.get(x).ec;
    		sumBn = sumBn + trainValues.get(x).bn;
    		sumBc = sumBc + trainValues.get(x).bc;
    		sumNn = sumNn + trainValues.get(x).nn;
    		sumMit = sumMit + trainValues.get(x).mit;
            }

            // store the centered and rescaled values by minusing the column's mean and then dividing it by its column's standard deviation
            for (int x = 0; x < trainValues.size(); x++) {
            	trainValues.get(x).ct = ((trainValues.get(x).ct - (meanCt)) / Math.sqrt(sumCt/trainValues.size()));
            	trainValues.get(x).csi = ((trainValues.get(x).csi - (meanCsi)) / Math.sqrt(sumCsi/trainValues.size()));
            	trainValues.get(x).csh = ((trainValues.get(x).csh - (meanCsh)) / Math.sqrt(sumCsh/trainValues.size()));
            	trainValues.get(x).ma = ((trainValues.get(x).ma - (meanMa)) / Math.sqrt(sumMa/trainValues.size()));
            	trainValues.get(x).ec = ((trainValues.get(x).ec - (meanEc)) / Math.sqrt(sumEc/trainValues.size()));
            	trainValues.get(x).bn = ((trainValues.get(x).bn - (meanBn)) / Math.sqrt(sumBn/trainValues.size()));
            	trainValues.get(x).bc = ((trainValues.get(x).bc - (meanBc)) / Math.sqrt(sumBc/trainValues.size()));
            	trainValues.get(x).nn = ((trainValues.get(x).nn - (meanNn)) / Math.sqrt(sumNn/trainValues.size()));
            	trainValues.get(x).mit = ((trainValues.get(x).mit - (meanMit)) / Math.sqrt(sumMit/trainValues.size()));
            }
            return trainValues; // return the centered and rescaled arrayList of rows
    }      
}