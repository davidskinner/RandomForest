// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

public class Main
{
	public static void log(String str)
	{
		System.out.println(str);
	}

	static void test(SupervisedLearner learner, String challenge)
	{
		// Load the training data
		String fn = "src/data/" + challenge;

		//load training features
		Matrix trainFeatures = new Matrix();
		trainFeatures.loadARFF(fn + "_train_feat.arff");

		//load training labels
		Matrix trainLabels = new Matrix();
		trainLabels.loadARFF(fn + "_train_lab.arff");

		// Train the model based on the training data, build_tree etc.
		learner.train(trainFeatures, trainLabels);

		// Load the test data
		Matrix testFeatures = new Matrix();
		testFeatures.loadARFF(fn + "_test_feat.arff");

		//load the test labels
		Matrix testLabels = new Matrix();
		testLabels.loadARFF(fn + "_test_lab.arff");

		// Measure and report accuracy between actual values determined by learner and actual values
		int misclassifications = learner.countMisclassifications(testFeatures, testLabels);

		System.out.println("Misclassifications by " + learner.name() + " at " + challenge + " = " + Integer.toString(misclassifications) + "/" + Integer.toString(testFeatures.rows()));
	}

	public static void testLearner(SupervisedLearner learner)
	{
		test(learner, "hep");
		test(learner, "vow");
		test(learner, "soy");
	}

	public static void main(String[] args)
	{
//		testLearner(new BaselineLearner());
		testLearner(new DecisionTree());
		//testLearner(new RandomForest(50);

	}
}
