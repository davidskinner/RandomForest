// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

abstract class SupervisedLearner 
{
	/// Return the name of this learner
	abstract String name();

	/// Train this supervised learner
	abstract void train(Matrix features, Matrix labels);

	/// Make a prediction
	abstract void predict(double[] in, double[] out);

	/// Measures the misclassifications with the provided test data
	int countMisclassifications(Matrix features, Matrix labels)
	{
		if(features.rows() != labels.rows())
			throw new IllegalArgumentException("Mismatching number of rows");

		double[] predictions = new double[labels.cols()];
		int misclassifications = 0;

		for(int i = 0; i < features.rows(); i++)
		{
			double[] feat = features.row(i);
			predict(feat, predictions);
			double[] lab = labels.row(i);

			for(int j = 0; j < lab.length; j++)
			{
				//check if the trained set got the right value
				if(predictions[j] != lab[j])
				{
					misclassifications++;
				}
			}
		}
		return misclassifications;
	}
}
