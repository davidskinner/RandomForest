import java.util.Random;

abstract class Node
{
	int attribute; // which attribute to divide on
	double pivot; // which value to divide on
	Node a;
	Node b;

	abstract boolean isLeaf();

	abstract double[] getLabel();
}

//abstract boolean isInterior();

class InteriorNode extends Node
{
	int attribute; // which attribute to divide on
	double pivot; // which value to divide on
	Node a;
	Node b;

	InteriorNode(Node a, Node b, int attribute, double pivot)
	{
		this.a = a;
		this.b = b;
		this.attribute = attribute;
		this.pivot = pivot;
	}

	boolean isLeaf()
	{
		return false;
	}


	double[] getLabel()
	{
		return new double[0];
	}
}

class LeafNode extends Node
{
	public double[] label;

	double[] getLabel()
	{
		return label;
	}

	LeafNode(Matrix labels)
	{
		label = new double[labels.cols()];
		for (int i = 0; i < labels.cols(); i++)
		{
			if (labels.valueCount(i) == 0)
				label[i] = labels.columnMean(i);
			else
				label[i] = labels.mostCommonValue(i);
		}
	}

	boolean isLeaf()
	{
		return true;
	}
}

class DecisionTree extends SupervisedLearner
{
	Node root;
	Matrix feat1;


	int pickDividingColumn(Matrix feat, Matrix lab, Random rand)
	{
		//pick a random column to divide on
		int columnToDivideOn = rand.nextInt(feat.cols());
		return columnToDivideOn;
	}

	double pickPivot(Matrix feat, Matrix lab, Random rand, int col)
	{
		int row = rand.nextInt(feat.rows());
		double pivot = feat.row(row)[col];
		return pivot;
	}

	String name()
	{
		return "DecisionTree";
	}

	Node build_tree(Matrix feat, Matrix lab)
	{

		feat1 = new Matrix(feat);
		Random rand = new Random();

		if (feat.rows() != lab.rows())
			throw new RuntimeException("mismatching features and labels");

		feat1 = new Matrix(feat);

		Matrix feat_a = new Matrix();
		Matrix feat_b = new Matrix();
		Matrix lab_a = new Matrix();
		Matrix lab_b = new Matrix();

		Matrix featCopy = new Matrix(feat);
		Matrix labCopy = new Matrix(lab);

		int col = 0;
		double pivot = 0;

		for (int patience = 9; patience > 0; patience--)
		{
			col = pickDividingColumn(feat, lab, rand);
			pivot = pickPivot(feat, lab, rand, col);

			int codeValue = feat.valueCount(col);

			feat_a = new Matrix(feat);
			feat_b = new Matrix(feat);
			lab_a = new Matrix(lab);
			lab_b = new Matrix(lab);

			feat_a.copyMetaData(feat);
			feat_b.copyMetaData(feat);
			lab_a.copyMetaData(lab);
			lab_b.copyMetaData(lab);

			featCopy.copy(feat);
			labCopy.copy(lab);

			for (int i = 0; i < feat.rows(); i++) // while
			{
				//if the data is continuous
				if (codeValue == 0)
				{
					if (feat.row(i)[col] < pivot)
					{
						feat_a.takeRow(feat.removeRow(i));
						lab_a.takeRow(lab.removeRow(i));
					} else
					{
						feat_b.takeRow(feat.removeRow(i));
						lab_b.takeRow(lab.removeRow(i));
					}
				} else                    //if the data is categorical
				{
					if (feat.row(i)[col] == pivot)
					{
						feat_a.takeRow(feat.removeRow(i));
						lab_a.takeRow(lab.removeRow(i));
					} else
					{
						feat_b.takeRow(feat.removeRow(i));
						lab_b.takeRow(lab.removeRow(i));
					}
				}
			}
			if (feat_a.rows() != 0 || feat_b.rows() != 0)
				break;
		}

		if (feat_a.rows() == 0 || feat_b.rows() == 0)
		{
			return new LeafNode(lab);
		}

		//make the node
		Node a = build_tree(feat_a, lab_a);
		Node b = build_tree(feat_b, lab_b);
		return new InteriorNode(a, b, col, pivot);
	}


	void train(Matrix feat, Matrix lab)
	{
		root = build_tree(feat, lab);
	}

	void predict(double[] in, double[] out)
	{
		Node n = root;

		while (true)
		{
			//if n is not a leaf node then branch
			if (!n.isLeaf())
			{
				if (feat1.valueCount(n.attribute) == 0)
				{
					//if  continuous
					if (in[n.attribute] <= n.pivot)
					{
						n = n.a;

					} else
						n = n.b;
				} else // if categorical
				{
					if (in[n.attribute] == n.pivot)
					{
						n = n.a;

					} else
						n = n.b;
				}

			} else
			{
				for (int i = 0; i < n.getLabel().length; i++)
				{
					out[i] = n.getLabel()[i];
				}

				//break after copying leaf node
				break;
			}
		}

	}

}