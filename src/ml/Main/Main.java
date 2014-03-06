package ml.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ml.data.*;
import ml.io.reader.ARFFReader;
import ml.learner.tree.DecisionTree;

public class Main {
	
	
	@SuppressWarnings("unchecked")
	private static List<Instance>[] partitionDataSet(DataSet data) {
		List<Instance>[] s = new ArrayList[2];
		s[0]=new ArrayList<Instance>();
		s[1] = new ArrayList<Instance>();
		for (Instance d:data.instances()) {
			int index = d.values(data.target()).toInt();
			s[index].add(d);
		}
		return s;
	}
	
	private static DataSet[] stratifySamples(DataSet data, int numSets, int size) {
		
		if (size>=data.instances().count()) {
			DataSet[] ds = new DataSet[1];
			ds[0] = data;
			return ds;
		}
		
		List<Instance>[] s = partitionDataSet(data);
		int total = (s[0].size()+s[1].size());
		double negRate = (double) s[0].size() / total ;
		int n =(int)( size * negRate);
		DataSet[] ds = new DataSet[numSets];
		
		for (int i = 0; i < numSets; i++) {
			List<Instance> ins = new  ArrayList<Instance>();
			Collections.shuffle(s[0]);
			Collections.shuffle(s[1]);
			
			//select negative examples
			for (int j = 0; j < n; j++) {
				ins.add(s[0].get(j));
			}
			
			//select positive examples
			for (int j=0; j<size-n; j++) {
				ins.add(s[1].get(j));
			}
	
			Collections.shuffle(ins);
			ds[i] = new DataSet(data.features(), new Instances(ins));
			ds[i].setRelation(data.relation());
		}
		
		return ds;
	}
	
	private static double[] stratifiedTest(DataSet train, DataSet test, int sampleSize) {
		ArrayList<Double> acc = new ArrayList<Double>();
		DataSet[] ds = stratifySamples(train, 10, sampleSize);
		for (int i=0; i<ds.length; i++) {
			DecisionTree dt = new DecisionTree();
			dt.train(ds[i], 4);
			double a = dt.test(test,false);
			acc.add(a);
		}
		Collections.sort(acc);
		double[] results = new double[3];
		results[0] = acc.get(0);
		results[1] = acc.get(acc.size()-1);
		results[2] =0;
		for (int i=0; i<acc.size(); i++) {
			results[2]=results[2] + acc.get(i);
		}
		results[2] = results[2] / acc.size();
		return results;
		
		
	}
	
	private static void doPartTwo(DataSet train, DataSet test) {
		int[] sizes = {25,50,100,200};
		for (int size:sizes) {
			double res[] = stratifiedTest(train, test, size);
			System.out.printf("%.5f %.5f %.5f\n", res[0], res[1], res[2]);
		}
	}
	
	private static void doPartThree(DataSet train, DataSet test) {
		int[] ms = {2,5,10,20};
		for (int m:ms) {
			DecisionTree dt = new DecisionTree();
			dt.train(train, m);
			double acc = dt.test(test);
			System.out.printf("%.5f\n", acc);
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			System.out.printf("usage: dt-learn [traing] [test] m\n");
			return;
		}

		DataSet test = null;
		DataSet train = null;
		int m = 0;

		try {
			ARFFReader reader = new ARFFReader(args[0]);
			train = reader.readDataSet();
			train.features().setTargetFeature(train.features().count() - 1);
		}
		catch (Exception ex) {
			System.out.printf("Error reading the traing file.\nDetails:" + ex.getMessage());
		}

		try {
			ARFFReader reader = new ARFFReader(args[1]);
			test = reader.readDataSet();
			test.features().setTargetFeature(test.features().count() - 1);
		}
		catch (Exception ex) {
			System.out.printf("Error running the test set.\nDetails:", ex.getMessage());
		}

		try {
			m = Integer.parseInt(args[2]);
		}
		catch (Exception ex) {
			System.out.printf("Error parsing the m parameter.\nDetails:", ex.getMessage());
		}
		
		if (args.length==3) {
			DecisionTree dt = new DecisionTree();
			try {
				dt.train(train, m);
				System.out.print(dt);
			}
			catch (Exception ex) {
				System.out.printf("Error training the decision tree.\nDetails:", ex.getMessage());
			}
			System.out.println();
			dt.test(test, true);
			//double accuracy = dt.test(test,true);
			//System.out.printf("%.5f\n", accuracy);
		}
		else {
			doPartTwo(train, test);
			System.out.print("\n");
			doPartThree(train,test);
			return;
		}
	}

}