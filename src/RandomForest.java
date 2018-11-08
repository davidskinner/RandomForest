import java.util.Random;

public class RandomForest extends SupervisedLearner
{
	Node root;
	Matrix DecisionFeature = new Matrix();
	static Random rand = new Random((long)22.0);

	RandomForest(int decisionTrees)
	{
		//resample data before training
		//increase patience
		//increase size of ensemble


//		for (int i = 0; i < decisionTrees; i++)
//		{
//			newdata = resambple(data);
//		}
	}



	void train(Matrix feat, Matrix lab)
	{
//		root = plantForest(feat, lab);
	}

	String name()
	{
		return "RandomForest";
	}

//	Node plantForest(Matrix feat, Matrix lab)
//	{
//		DecisionFeature = new Matrix(feat);
//
//		if (feat.rows() != lab.rows())
//			throw new RuntimeException("mismatching features and labels");
//
//		Matrix feat_a = new Matrix();
//		Matrix feat_b = new Matrix();
//		Matrix lab_a = new Matrix();
//		Matrix lab_b = new Matrix();
//
//		Matrix featTemp = new Matrix(feat);
//		Matrix labCopy = new Matrix(lab);
//
//		int col = 0;
//		double pivot = 0;
//
//		for (int patience = 8; patience > 0; patience--)
//		{
//			col = pickDividingColumn(feat);
//			pivot = pickPivotRow(feat, col);
//
//			int codeValue = feat.valueCount(col);
//
//			feat_a = new Matrix(feat);
//			feat_b = new Matrix(feat);
//			lab_a = new Matrix(lab);
//			lab_b = new Matrix(lab);
//
//			feat_a.copyMetaData(feat);
//			feat_b.copyMetaData(feat);
//			lab_a.copyMetaData(lab);
//			lab_b.copyMetaData(lab);
//
//			featTemp.copy(feat);
//			labCopy.copy(lab);
//
//			int firstPosition = 0;
//			while(featTemp.rows() !=0)
//			{
//				//if the data is continuous
//				if (codeValue == 0)
//				{
//					if (feat.row(firstPosition)[col] < pivot)
//					{
//
//						feat_a.takeRow(featTemp.removeRow(firstPosition));
//						lab_a.takeRow(labCopy.removeRow(firstPosition));
//					} else
//					{
//						feat_b.takeRow(featTemp.removeRow(firstPosition));
//						lab_b.takeRow(labCopy.removeRow(firstPosition));
//					}
//				} else //if the data is categorical
//				{
//					if (feat.row(firstPosition)[col] == pivot)
//					{
//						feat_a.takeRow(featTemp.removeRow(firstPosition));
//						lab_a.takeRow(labCopy.removeRow(firstPosition));
//					} else
//					{
//						feat_b.takeRow(featTemp.removeRow(firstPosition));
//						lab_b.takeRow(labCopy.removeRow(firstPosition));
//					}
//				}
//			}
//
//			if (feat_a.rows() != 0 && feat_b.rows() != 0)
//				break;
//		}
//
//		if (feat_a.rows() == 0 || feat_b.rows() == 0)
//		{
//			return new LeafNode(lab);
//		}
//
//		//make the node
//		Node a = build_tree(feat_a, lab_a);
//		Node b = build_tree(feat_b, lab_b);
//		return new InteriorNode(a, b, col, pivot);
//	}


	void predict(double[] in, double[] out)
	{
		Node n = root;

		while (true)
		{
			//if n is not a leaf node then branch
			if (!n.isLeaf())
			{
				if (DecisionFeature.valueCount(n.attribute) == 0)
				{
					//if  continuous
					if (in[n.attribute] < n.pivot)
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
