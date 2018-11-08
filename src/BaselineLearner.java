// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------


class BaselineLearner extends SupervisedLearner
{
	double[] mode;

	String name()
	{
		return "Baseline";
	}

	//takes in a set of features and labels and trains the learner
	void train(Matrix features, Matrix labels)
	{

		mode = new double[labels.cols()];

		for (int i = 0; i < labels.cols(); i++)
		{
			//if the values are continuous, then take the mean
			if (labels.valueCount(i) == 0)
				mode[i] = labels.columnMean(i);
				// values are categorical and you take the most common value
			else
				mode[i] = labels.mostCommonValue(i);
		}
	}

	void predict(double[] in, double[] out)
	{
		Vec.copy(out, mode);
	}
}
