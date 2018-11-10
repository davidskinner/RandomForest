import java.util.ArrayList;
import java.util.Random;

public class RandomForest extends SupervisedLearner
{
	DecisionTree[] decisionTreeArray;
	Node root;
	Matrix DecisionLabel;
	static Random rand = new Random((long) 10);
//	int decisionTrees;

	//put tree results in here
	Matrix treeMatrix = new Matrix();


	RandomForest(int decisionTrees)
	{
		this.decisionTreeArray = new DecisionTree[decisionTrees];
	}

	void train(Matrix ftrs, Matrix lbls)
	{
		DecisionLabel = new Matrix(lbls);
		plantForest(ftrs, lbls);
	}

	void plantForest(Matrix feat, Matrix lab)
	{
		for (int i = 0; i < decisionTreeArray.length; i++)
		{
			//resample the data before building tree
			Matrix featureBag = new Matrix();
			featureBag.copyMetaData(feat);

			Matrix labelBag = new Matrix();
			labelBag.copyMetaData(lab);

			//make a bag
			for (int j = 0; j < feat.rows(); j++)
			{
				int takeRandom = rand.nextInt(feat.rows());

				featureBag.takeRow(feat.row(takeRandom));
				labelBag.takeRow(lab.row(takeRandom));
			}

			decisionTreeArray[i] = new DecisionTree();
			decisionTreeArray[i].train(featureBag,labelBag);
		}
	}


	void predict(double[] in, double[] out)
	{
		Matrix democracy = new Matrix();
		democracy.copyMetaData(DecisionLabel);

		for (int i = 0; i < decisionTreeArray.length; i++)
		{
			double[] something = democracy.newRow();
			decisionTreeArray[i].predict(in, something);
		}

		for (int i = 0; i < democracy.cols(); i++)
		{
				out[i] = democracy.mostCommonValue(i);
		}
	}

	String name()
	{
		return "RandomForest";
	}

}
