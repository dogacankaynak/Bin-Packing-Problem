/* Heuristic
 * call format:
 * java Heuristic seed instancesToSolve.extension iterationLimit swapCount outputFile.extension
 *
 * instance k is named: instanceToSolve[k].extension
 * outputFile (solution) k is named: outputFile[k].extension
 */
 import java.util.ArrayList;
 import java.util.Random;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.PrintWriter;

public class Heuristic {

	public static void main(String[] args) {
		String[] argsV2 = new String[]{"
		", "modelData100.txt", "150000", "3",
										"modelData100HeuSoln.txt"};

    	mainV2(argsV2);
	}

    public static void mainV2(String[] args) {
    	long seed = Long.parseLong(args[0]);
    	String inFileName = args[1], outFileName = args[4];
    	int iterationLimit = Integer.parseInt(args[2]), swaps = Integer.parseInt(args[3]);

    	Random r = new Random(seed);

    	//get the instances ready
    	ArrayList<File> instances = getInstanceFiles(inFileName);

    	for(int i = 0; i<instances.size(); i++) {
    		//read file
    		String[] lines = readFile(instances.get(i).getName());

    		//set up parameters
    		int n = getValueOfN(lines[0]);
    		double[] weights = getValuesOfW(lines[1]);
    		double C = getValueOfC(lines[2]);

    		//array will represent the order of items going into bins consecutively
    		int[] initial = new int[weights.length];

    		//bin and item indecies start at 0, printed version starts at 1
    		for(int j = 0; j<initial.length; j++) initial[j] = j;

    		//best solution observed
    		int[] bestSoln = new int[initial.length];
    		System.arraycopy(initial, 0, bestSoln, 0, initial.length);
    		int bestScore = calcScore(bestSoln, weights, C);

    		//shuffle the array
    		initial = swapManyTimes(initial, r, initial.length);

    		//check again for the best
    		if(bestScore > (initial, weights, C)) {
    			bestScore = calcScore(initial, weights, C);
    			System.arraycopy(initial, 0, bestSoln, 0, initial.length);
    		} else {
    			System.arraycopy(bestSoln, 0, initial, 0, initial.length);
    		}

    		//algorithm variables
    		int iterationNumber = 0;
    		while(iterationNumber++ < iterationLimit) {
    			int[] currSoln = swapManyTimes(initial, r, swaps);
    			int score = calcScore(currSoln, weights, C);
    			if(score < bestScore) {
    				bestScore = score;
    				System.arraycopy(currSoln, 0, bestSoln, 0, currSoln.length);
    				System.arraycopy(currSoln, 0, initial, 0, currSoln.length);
    			}
    		}

    		//write the solution into file
    		String toWrite = fileFormat(bestSoln, weights, C, bestScore);
    		String dirToWrite = outFileName.substring(0, outFileName.indexOf(".")) + "[" + (i+1) + "]" +
    			outFileName.substring(outFileName.indexOf("."));
    		writeFile(dirToWrite, toWrite);
    	}
    }

    public static String fileFormat(int[] assignment, double[] weights, double C, int score) {
    	String ret = "Heuristic has found solution with objective value: " + score + "\n";
    	double currLoad = 0;
    	int usedBins = 1;
    	for(int i = 0; i<assignment.length; i++) {
    		if(currLoad + weights[assignment[i]] > C) {
    			usedBins++;
    			currLoad = weights[assignment[i]];
    			ret += "Item " + (assignment[i]+1) + " is in bin " + usedBins + "\n";
    		} else {
    			currLoad += weights[assignment[i]];
    			ret += "Item " + (assignment[i]+1) + " is in bin " + usedBins + "\n";
    		}
    	}
    	return ret;
    }

    public static int[] swapManyTimes(int[] assignment, Random r, int times) {
    	int[] ret = new int[assignment.length];
    	System.arraycopy(assignment, 0, ret, 0, ret.length);
    	for(int i = 0; i<times; i++) {
    		int index1 = r.nextInt(ret.length);
    		int index2 = r.nextInt(ret.length);
    		while(index1 == index2) index2 = r.nextInt(ret.length);
    		int tmp = ret[index1];
    		ret[index1] = ret[index2];
    		ret[index2] = tmp;
    	}
    	return ret;   
    }

    public static int calcScore(int[] assignment, double[] weights, double C) {
    	//calculates the score by putting items in the order of assignment and opening new
    	//bins as the next item cannot be put into the current bin
    	int ret = 0;
    	double currLoad = weights[assignment[0]];
    	for(int i = 1; i<assignment.length; i++) {
    		if(currLoad + weights[assignment[i]] > C) {
    			ret++;
    			currLoad = weights[assignment[i]];
    		} else {
    			currLoad += weights[assignment[i]];
    		}
    	}
    	return ret + 1;
    }

    public static ArrayList<File> getInstanceFiles(String nameFormat) {
    	String currDir = System.getProperty("user.dir"), extension = nameFormat.substring(nameFormat.indexOf(".")+1),
    			fnames = nameFormat.substring(0, nameFormat.indexOf("."));
    	File folder = new File(currDir);
		File[] listOfFiles = folder.listFiles();

		ArrayList<File> ret = new ArrayList<File>();

		for(int i = 0; i<listOfFiles.length; i++) {
			if(listOfFiles[i].isDirectory()) continue;
			else if(checkIfInstance(listOfFiles[i].getName(), fnames, extension)) ret.add(listOfFiles[i]);
		}

		return ret;
    }

    public static boolean checkIfInstance(String fname, String instName, String extension) {
    	if(fname.substring(fname.indexOf(".")+1).compareTo(extension) != 0) return false;
    	else if(fname.indexOf("[") < 0) return false;
    	else if(fname.substring(0,fname.indexOf("[")).compareTo(instName) != 0) return false;
    	return true;
    }

    public static double getValueOfC(String line) {
    	return Double.parseDouble(line.substring(line.indexOf("=") + 2, line.indexOf(";")));
    }

    public static double[] getValuesOfW(String line) {
    	String[] strValues = line.substring(line.indexOf("[")+1,line.indexOf("]")).split(",");
    	double[] ret = new double[strValues.length];
    	for(int i = 0; i<ret.length; i++) ret[i] = Double.parseDouble(strValues[i]);
    	return ret;
    }

    public static int getValueOfN(String line) {
    	return Integer.parseInt(line.substring(line.indexOf("=") + 2, line.indexOf(";")));
    }

    public static String[] readFile(String dir) {
    	String[] ret = new String[3];

    	try(BufferedReader in = new BufferedReader(new FileReader(dir))) {

    		String line;
    		int i = 0;

    		while((line = in.readLine()) != null) {
    			ret[i++] = line;
    		}

    	} catch(Exception e) {
    		e.printStackTrace();
    	}

    	return ret;
    }

    public static void writeFile(String dir, String cont) {
    	try(PrintWriter pw = new PrintWriter(new File(dir))) {
    		pw.write(cont);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
