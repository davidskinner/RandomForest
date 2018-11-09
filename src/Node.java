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

	double[] getLabel()
	{
		return label;
	}

	boolean isLeaf()
	{
		return true;
	}
}

class DecisionTree extends SupervisedLearner
{
	static Random rand = new Random(14);
	Node root;
	Matrix DecisionFeature = new Matrix();

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

	String name()
	{
		return "DecisionTree";
	}

	Node build_tree(Matrix ftrs, Matrix lbls)
	{
		//hang on to this for later
		// DecisionFeature = new Matrix(ftrs);

		if (ftrs.rows() != lbls.rows())
			throw new RuntimeException("mismatching features and labels");

		//initialize features and labels matrices
		Matrix featuresA = new Matrix();
		Matrix featuresB = new Matrix();
		Matrix labelsA = new Matrix();
		Matrix labelsB = new Matrix();

		//initialize temp variables/ copies of the passed
		// in features and labels outside of loop
		Matrix featTemp = new Matrix();
		Matrix labTemp = new Matrix();

		//col
		int column = 0;
		double pivot = 0;

		for (int patience = 12; patience > 0; patience--)
		{
			//			Main.log(Integer.toString(patience));
			column = pickDividingColumn(ftrs);
			pivot = pickPivotRow(ftrs, column);

			int codeValue = ftrs.valueCount(column);

			featuresA = new Matrix(ftrs);
			featuresB = new Matrix(ftrs);
			labelsA = new Matrix(lbls);
			labelsB = new Matrix(lbls);

			featuresA.copyMetaData(ftrs);
			featuresB.copyMetaData(ftrs);
			labelsA.copyMetaData(lbls);
			labelsB.copyMetaData(lbls);

			featTemp.copy(ftrs);
			labTemp.copy(lbls);

			int zerothPosition = 0;

			while (featTemp.rows() != 0)
			{
				//				Main.log(Integer.toString(featuresA.rows());
				//				Main.log(Integer.toString(featuresA.rows()));
				//if the data is continuous
				if (codeValue == 0)
				{
					if (featTemp.row(zerothPosition)[column] < pivot)
					{
						featuresA.takeRow(featTemp.removeRow(zerothPosition));
						labelsA.takeRow(labTemp.removeRow(zerothPosition));

						//						Vec.copy(featuresA.newRow(), featTemp.row(zerothPosition));
						//						Vec.copy(labelsA.newRow(), labTemp.row(zerothPosition));

					} else
					{

						featuresB.takeRow(featTemp.removeRow(zerothPosition));
						labelsB.takeRow(labTemp.removeRow(zerothPosition));

						//						Vec.copy(featuresB.newRow(), featTemp.row(zerothPosition));
						//						Vec.copy(labelsB.newRow(), labTemp.row(zerothPosition));
					}
				} else //if the data is categorical
				{
					if (featTemp.row(zerothPosition)[column] == pivot)
					{
						featuresA.takeRow(featTemp.removeRow(zerothPosition));
						labelsA.takeRow(labTemp.removeRow(zerothPosition));

						//						Vec.copy(featuresA.newRow(), featTemp.row(zerothPosition));
						//						Vec.copy(labelsA.newRow(), labTemp.row(zerothPosition));
					} else
					{
						featuresB.takeRow(featTemp.removeRow(zerothPosition));
						labelsB.takeRow(labTemp.removeRow(zerothPosition));

						//						Vec.copy(featuresB.newRow(), featTemp.row(zerothPosition));
						//						Vec.copy(labelsB.newRow(), labTemp.row(zerothPosition));
					}
				}
			}

			//			Main.log(Integer.toString(featuresA.rows()));
			//			Main.log(Integer.toString(featuresB.rows()));
			if (featuresA.rows() != 0 && featuresB.rows() != 0)
				break;
		}

		if (featuresA.rows() == 0 || featuresB.rows() == 0)
		{
			return new LeafNode(lbls);
		}

		//make the node
		Node a = build_tree(featuresA, labelsA);
		Node b = build_tree(featuresB, labelsB);
		return new InteriorNode(a, b, column, pivot);
	}


	void train(Matrix feat, Matrix lab)
	{
		DecisionFeature = new Matrix(feat);
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