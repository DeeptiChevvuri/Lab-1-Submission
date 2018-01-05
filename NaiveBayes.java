import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by: Deepti Chevvuri Date: 2/5/2017.
 */
public class NaiveBayes {
	public HashMap<Integer, Double> priors = new HashMap<Integer, Double>();// priors
																			// (each
																			// news
																			// group or label)
	public HashMap<Integer, String> classMap = new HashMap<Integer, String>();// label
																				// id
																				// and
																				// respective
																				// label
																				// name
	public HashMap<Integer, Document> documentsMap = new HashMap<Integer, Document>();// all
																						// documents
																						// and
																						// respective
																						// document
																						// ids
	public HashMap<Integer, ArrayList<Integer>> documentClassification = new HashMap<Integer, ArrayList<Integer>>();// all
																													// label
																													// id
																													// with
																													// their
																													// respective
																													// documentid
																													// collections
	public HashMap<Integer, Document> testDocumentsMap = new HashMap<Integer, Document>();// all
																							// test
																							// documents
																							// and
																							// respective
																							// document
																							// ids
	public HashMap<Integer, ArrayList<Integer>> testdocumentClassification = new HashMap<Integer, ArrayList<Integer>>();// all
																														// labe
																														// lid
																														// with
																														// their
																														// respective
																														// test
																														// documentid
																														// collections

	double MLE[][];
	double BE[][];

	class Word {
		String word;
		int wordId;
		int count;

		public Word(int wordId, int count) {
			this.wordId = wordId;
			this.count = count;
		}
	}

	public class Document {
		int documentId, totalWordsCount;
		ArrayList<Word> wordList = new ArrayList<Word>();
		int label;

	}

	StringTokenizer stringTokenizer;

	public void calculatePriors() {
		// reads the label (class information) and calculates prior for every
		// class
	}

	public NaiveBayes(String vocabFile, String mapFile, String trainingData, String trainingLabel,
			String testTrainingData, String testLabel) {
		HashMap<Integer, Integer> tempDocLabelMap = new HashMap<Integer, Integer>();

		try {
			// reads the labels
			readMap(mapFile);

			// reads the training data document labels
			tempDocLabelMap = readLabels(trainingLabel, tempDocLabelMap);

			// reads the training data from training data file
			tempDocLabelMap = readTrainingData(trainingData, tempDocLabelMap);

			// read the test data document labels
			tempDocLabelMap = readTestLabels(testLabel, tempDocLabelMap);

			// read the test data file from training data file
			tempDocLabelMap = readTestTrainingData(testTrainingData, tempDocLabelMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// reads the labels
	private void readMap(String mapFile) {
		try {
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(mapFile));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				stringTokenizer = new StringTokenizer(sCurrentLine, ",");
				while (stringTokenizer.hasMoreTokens()) {
					classMap.put(Integer.parseInt(stringTokenizer.nextToken()), stringTokenizer.nextToken());
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// reads the training data document labels
	private HashMap<Integer, Integer> readLabels(String trainingLabel, HashMap<Integer, Integer> tempDocLabelMap) {

		try {
			int i = 1;
			String sCurrentLine;
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(trainingLabel));
			while ((sCurrentLine = br.readLine()) != null) {
				int label = Integer.parseInt(sCurrentLine);
				tempDocLabelMap.put(i, label);
				ArrayList<Integer> docList = documentClassification.get(label);
				if (docList == null) {
					documentClassification.put(label, new ArrayList<Integer>());
				}
				documentClassification.get(label).add(i);
				i++;
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tempDocLabelMap;
	}

	// reads the training data from training data file
	private HashMap<Integer, Integer> readTrainingData(String trainingData, HashMap<Integer, Integer> tempDocLabelMap) {
		String sCurrentLine;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(trainingData));
			while ((sCurrentLine = br.readLine()) != null) {
				stringTokenizer = new StringTokenizer(sCurrentLine, ",");
				int docId, wordId, count;
				while (stringTokenizer.hasMoreTokens()) {
					docId = Integer.parseInt(stringTokenizer.nextToken());
					wordId = Integer.parseInt(stringTokenizer.nextToken());
					count = Integer.parseInt(stringTokenizer.nextToken());
					Word word = new Word(wordId, count);
					Document document = documentsMap.get(docId);
					if (document == null) {
						document = new Document();
						document.documentId = docId;
						document.totalWordsCount = 0;
						document.label = tempDocLabelMap.get(docId);
						documentsMap.put(docId, document);
					}
					document.wordList.add(word);
					document.totalWordsCount += count;
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tempDocLabelMap;

	}

	// read the test data document labels
	private HashMap<Integer, Integer> readTestLabels(String testLabel, HashMap<Integer, Integer> tempDocLabelMap) {
		try {
			String sCurrentLine;
			int i = 1;
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(testLabel));
			while ((sCurrentLine = br.readLine()) != null) {
				int label = Integer.parseInt(sCurrentLine);
				tempDocLabelMap.put(i, label);
				ArrayList<Integer> docList = testdocumentClassification.get(label);
				if (docList == null) {
					testdocumentClassification.put(label, new ArrayList<Integer>());
				}
				testdocumentClassification.get(label).add(i);
				i++;
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tempDocLabelMap;
	}

	// read the test data file from training data file
	private HashMap<Integer, Integer> readTestTrainingData(String testTrainingData,
			HashMap<Integer, Integer> tempDocLabelMap) {
		try {
			String sCurrentLine;
			BufferedReader br = null;
			int docId, wordId, count;
			br = new BufferedReader(new FileReader(testTrainingData));
			while ((sCurrentLine = br.readLine()) != null) {
				stringTokenizer = new StringTokenizer(sCurrentLine, ",");
				while (stringTokenizer.hasMoreTokens()) {
					docId = Integer.parseInt(stringTokenizer.nextToken());
					wordId = Integer.parseInt(stringTokenizer.nextToken());
					count = Integer.parseInt(stringTokenizer.nextToken());
					Word word = new Word(wordId, count);
					Document document = testDocumentsMap.get(docId);
					if (document == null) {
						document = new Document();
						document.documentId = docId;
						document.totalWordsCount = 0;
						document.label = tempDocLabelMap.get(docId);
						testDocumentsMap.put(docId, document);
					}
					document.wordList.add(word);
					document.totalWordsCount += count;
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tempDocLabelMap;
	}

	// to compute the priors
	private void priors() {
		int totalDocuments = documentsMap.size();
		Iterator indices = documentClassification.keySet().iterator();
		while (indices.hasNext()) {
			int index = (int) indices.next();
			priors.put(index, documentClassification.get(index).size() / (totalDocuments * 1.0));
		}
		System.out.println("Class Priors-");
		for (Entry<Integer, Double> entry : priors.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}

	// to compute the estimates
	public void computeMLEandBE() {
		Iterator indices = documentClassification.keySet().iterator();
		int totalVocabulary = 61188;
		MLE = new double[documentClassification.size()][totalVocabulary];
		BE = new double[documentClassification.size()][totalVocabulary];
		while (indices.hasNext()) {
			int index = (int) indices.next();
			ArrayList<Integer> documentList = documentClassification.get(index);
			int wordCount[] = new int[totalVocabulary];
			int totalWordCount = 0;
			for (int i = 0; i < documentList.size(); i++) {
				Document doc = documentsMap.get(documentList.get(i));
				for (int j = 0; j < doc.wordList.size(); j++) {
					Word word = doc.wordList.get(j);
					totalWordCount += word.count;
					wordCount[word.wordId - 1] += word.count;
				}
			}

			for (int i = 0; i < totalVocabulary; i++) {
				MLE[index - 1][i] = wordCount[i] / (double) totalWordCount;
				BE[index - 1][i] = (wordCount[i] + 1) / (double) (totalWordCount + totalVocabulary);
			}
		}
		System.out.println();
	}

	// to preprocess before computing the performnace matrices
	public void performance(String type, String metric) {
		HashMap<Integer, ArrayList<Integer>> estimatedClassification = new HashMap<Integer, ArrayList<Integer>>();

		HashMap<Integer, Document> documentsMapCurrent = type.equals("training") ? documentsMap : testDocumentsMap;
		double est[][] = metric.equals("BE") ? BE : MLE;
		Iterator indices = documentsMapCurrent.keySet().iterator();
		while (indices.hasNext()) {
			int docClassificationIndex = -1;
			Document doc = documentsMapCurrent.get(indices.next());
			double maximumEstimate = Integer.MIN_VALUE;
			Iterator classifierIterator = documentClassification.keySet().iterator();
			while (classifierIterator.hasNext()) {
				int label = (int) classifierIterator.next();
				double estimate = 0;
				estimate += Math.log(priors.get(label));
				ArrayList<Word> wordList = doc.wordList;
				for (Word word : wordList) {
					estimate += (word.count * Math.log(est[label - 1][word.wordId - 1]));
				}
				if (maximumEstimate < estimate) {
					maximumEstimate = estimate;
					docClassificationIndex = label;
				}
			}
			ArrayList<Integer> docs = estimatedClassification.get(docClassificationIndex);
			if (docs == null)
				estimatedClassification.put(docClassificationIndex, new ArrayList<Integer>());
			estimatedClassification.get(docClassificationIndex).add(doc.documentId);
		}
		// to print overall accuracy and class accuracies
		printAccuracies(type, metric, estimatedClassification, documentsMapCurrent.size());
		// to print the confusion matrix
		printConfusionMatrix(type, estimatedClassification);

	}

	// to print overall accuracy and class accuracies
	private void printAccuracies(String type, String metric,
			HashMap<Integer, ArrayList<Integer>> estimatedClassification, Integer size) {
		try {
			Iterator classifierIterator = documentClassification.keySet().iterator();

			int globalCorrectClassified = 0;
			double classAccuracy[] = new double[documentClassification.keySet().size()];
			while (classifierIterator.hasNext()) {
				int labelId = (int) classifierIterator.next();
				ArrayList<Integer> trueClassification = type.equals("training") ? documentClassification.get(labelId)
						: testdocumentClassification.get(labelId); // documentClassification.get(label);
				ArrayList<Integer> estimateClassification = estimatedClassification.get(labelId);

				ArrayList<Integer> correctlyClassified = new ArrayList<>(estimateClassification);
				correctlyClassified.retainAll(trueClassification);

				int correctClassifiedCount = correctlyClassified.size();
				globalCorrectClassified += correctClassifiedCount;
				classAccuracy[labelId - 1] = (double) correctClassifiedCount / trueClassification.size();

			}
			System.out.println();
			System.out.println(type + " - " + metric);
			System.out.println("Overall Accuracy: " + (double) globalCorrectClassified / size);
			System.out.println("Class Accuracies");
			for (int i = 0; i < classAccuracy.length; i++) {
				System.out.println("Class " + (i + 1) + "-" + classAccuracy[i]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// to print the confusion matrix
	private void printConfusionMatrix(String type, HashMap<Integer, ArrayList<Integer>> estimatedClassification) {
		System.out.println("\nConfusion Matrix:");
		int classCount = documentClassification.keySet().size();
		int confusionMatrix[][] = new int[classCount][classCount];
		for (int i = 0; i < confusionMatrix.length; i++) {
			ArrayList<Integer> actualClassification = type.equals("training") ? documentClassification.get(i + 1)
					: testdocumentClassification.get(i + 1); // documentClassification.get(i+1);
			for (int j = 0; j < confusionMatrix.length; j++) {
				ArrayList<Integer> estimateClassification = estimatedClassification.get(j + 1);
				ArrayList<Integer> tempClassification = new ArrayList<>(estimateClassification);
				tempClassification.retainAll(actualClassification);
				confusionMatrix[i][j] = tempClassification.size();
			}
		}
		for (int i = 0; i < confusionMatrix.length; i++) {
			System.out.println();
			for (int j = 0; j < confusionMatrix.length; j++) {
				System.out.print("\t\t" + confusionMatrix[i][j]);
			}
		}
	}

	public static void main(String args[]) {
		NaiveBayes classifier = new NaiveBayes("vocabulary.txt", "map.csv", "train_data.csv", "train_label.csv",
				"test_data.csv", "test_label.csv");
		// calculating the priors
		classifier.priors();
		// Computing MLE and BE
		classifier.computeMLEandBE();
		// Estimating the BE performance of the classifier for the training set
		classifier.performance("training", "BE");
		// Estimating the BE performance of the classifier for the test set
		classifier.performance("test", "BE");
		// Estimating the MLE performance of the classifier for the test set
		classifier.performance("test", "MLE");
		System.out.println();
	}
}
