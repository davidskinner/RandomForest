import java.util.ArrayList;
import java.util.Random;

public class RandomForest extends SupervisedLearner
{
	DecisionTree[] decisionTreeArray;
	Node root;
	Matrix DecisionFeature = new Matrix();
	static Random rand = new Random();
//	int decisionTrees;

	//put tree results in here
	Matrix treeMatrix = new Matrix();


	RandomForest(int decisionTrees)
	{
		this.decisionTreeArray = new DecisionTree[decisionTrees];
	}

	void train(Matrix ftrs, Matrix lbls)
	{
		DecisionFeature = new Matrix(ftrs);
		root = plantForest(ftrs, lbls);
	}

	int pickDividingColumn(Matrix feat)
	{
		//pick a random column to divide on
		return rand.nextInt(feat.cols());
	}

	double pickPivotRow(Matrix feat, int col)
	{
		//pick a random row to divide on
		int row = rand.nextInt(feat.rows());
		return feat.row(row)[col];
	}

	Node plantForest(Matrix feat, Matrix lab)
	{
		//resample the data before building tree
		Matrix featureBag = new Matrix();
		featureBag.copyMetaData(feat);

		Matrix labelBag = new Matrix();
		labelBag.copyMetaData(lab);

		//make a bag
		for (int i = 0; i < feat.rows(); i++)
		{
			int takeRandom = rand.nextInt(feat.rows());

			featureBag.takeRow(feat.row(takeRandom));
			labelBag.takeRow(lab.row(takeRandom));
		}

		//for each bag, build a decision tree on the bag
	//take most common or average value for each attribute from each tree

		DecisionFeature = new Matrix(featureBag);

		if (featureBag.rows() != labelBag.rows())
			throw new RuntimeException("mismatching features and labels");

		Matrix feat_a = new Matrix();
		Matrix feat_b = new Matrix();
		Matrix lab_a = new Matrix();
		Matrix lab_b = new Matrix();

		Matrix featTemp = new Matrix(featureBag);
		Matrix labCopy = new Matrix(labelBag);

		int col = 0;
		double pivot = 0;

		for (int patience = 12; patience > 0; patience--)
		{
			col = pickDividingColumn(featureBag);
			pivot = pickPivotRow(featureBag, col);

			int codeValue = featureBag.valueCount(col);

			feat_a = new Matrix(featureBag);
			feat_b = new Matrix(featureBag);
			lab_a = new Matrix(labelBag);
			lab_b = new Matrix(labelBag);

			feat_a.copyMetaData(featureBag);
			feat_b.copyMetaData(featureBag);
			lab_a.copyMetaData(labelBag);
			lab_b.copyMetaData(labelBag);

			featTemp.copy(featureBag);
			labCopy.copy(labelBag);

			int firstPosition = 0;
			while(featTemp.rows() !=0)
			{
				//if the data is continuous
				if (codeValue == 0)
				{
					if (featTemp.row(firstPosition)[col] < pivot)
					{

						feat_a.takeRow(featTemp.removeRow(firstPosition));
						lab_a.takeRow(labCopy.removeRow(firstPosition));
					} else
					{
						feat_b.takeRow(featTemp.removeRow(firstPosition));
						lab_b.takeRow(labCopy.removeRow(firstPosition));
					}
				} else //if the data is categorical
				{
					if (featTemp.row(firstPosition)[col] == pivot)
					{
						feat_a.takeRow(featTemp.removeRow(firstPosition));
						lab_a.takeRow(labCopy.removeRow(firstPosition));
					} else
					{
						feat_b.takeRow(featTemp.removeRow(firstPosition));
						lab_b.takeRow(labCopy.removeRow(firstPosition));
					}
				}
			}

			if (feat_a.rows() != 0 && feat_b.rows() != 0)
				break;
		}

		if (feat_a.rows() == 0 || feat_b.rows() == 0)
		{
			return new LeafNode(labelBag);
		}

//		//make the node
		Node a = plantForest(feat_a, lab_a);
		Node b = plantForest(feat_b, lab_b);
		return new InteriorNode(a, b, col, pivot);
	}


	void predict(double[] in, double[] out)
	{
		Node n = root;

		while (true)
		{
			//if n is not a leaf node then branch
			if (!n.isLeaf())
			{
				InteriorNode something = (InteriorNode) n;

				if (DecisionFeature.valueCount(n.attribute) == 0)
				{
					//if  continuous
					if (in[something.attribute] < something.pivot)
					{
						n = something.a;

					} else
						n = something.b;
				} else // if categorical
				{
					if (in[n.attribute] == n.pivot)
					{
						n = something.a;

					} else
						n = something.b;
				}

			} else
			{
				//
				for (int i = 0; i < n.getLabel().length; i++)
				{
					out[i] = n.getLabel()[i];
				}
				//break after copying leaf node
				break;
			}
		}
	}

	String name()
	{
		return "RandomForest";
	}

}
