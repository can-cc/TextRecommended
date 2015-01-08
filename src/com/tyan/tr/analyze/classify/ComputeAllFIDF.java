package com.tyan.tr.analyze.classify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tyan.tr.participle.ParticiplerByJeseg;
import com.tyan.tr.tool.MathTool;

public class ComputeAllFIDF {
	private String[] featureWords;
	private HashMap<String, Float> featureIDF;
	private List<String> fileList;
	
	public ComputeAllFIDF(String dirPath, String featurefile) throws FileNotFoundException, IOException {
		fileList = new ArrayList<String>();
		readDirs(dirPath);
		loadFeatureWord(featurefile);
		computeFeatureIDF3();
	}
	
	public void writeToFile(String resultFile) throws IOException{
		FileWriter fw = new FileWriter(resultFile, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw, true);
		for(String idf : featureIDF.keySet()){
			pw.print(idf);
			pw.print("::");
			pw.print(featureIDF.get(idf));
			pw.println();
		}
		pw.flush();
		pw.close();
		System.out.println("success");
	}

	public void readDirs(String filepath) throws FileNotFoundException,
			IOException {

		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("输入的参数应该为[文件夹名]");
				System.out.println("filepath: " + file.getAbsolutePath());
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "/" + filelist[i]);
					if (!readfile.isDirectory()) {
						fileList.add(readfile.getAbsolutePath());
					} else if (readfile.isDirectory()) {
						readDirs(filepath + "/" + filelist[i]);
					}
				}
			}
			System.out.println(fileList.size());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public void loadFeatureWord(String filePath) throws IOException {// mergedFeature(Jeseg).txt
		featureWords = null;
		List<String> featureWordsList = new ArrayList<String>();
		InputStreamReader is = new InputStreamReader(new FileInputStream(
				filePath), "UTF-8");
		BufferedReader br = new BufferedReader(is);
		String line = br.readLine();
		while (line != null) {
			featureWordsList.add(line);
			line = br.readLine();
		}
		br.close();
		featureWords = (String[]) featureWordsList
				.toArray(new String[featureWordsList.size()]);
	}

	public void computeFeatureIDF() throws FileNotFoundException, IOException {
		featureIDF = new HashMap<String, Float>();
		int containCount;
		int i = 0;
		for (String word : featureWords) {
			containCount = 0;
			for (String filepath : fileList) {
				String Text = readFiles(filepath);
				if (Text.indexOf(word) >= 0)
					containCount++;
			}
			Float idf = MathTool.log((fileList.size() / (1 + containCount)));
			featureIDF.put(word, idf);
			i++;
			System.out.println(i);
		}
	}
	
	public void computeFeatureIDF2() throws IOException{
		featureIDF = new HashMap<String, Float>();
		int[] intrecord = new int[featureWords.length];
		for(int i=0; i<featureWords.length; i++)
			intrecord[i] = 0;
		HashMap<String, Integer> record = new HashMap<String, Integer>();
		for(int i=0; i<featureWords.length; i++){
			record.put(featureWords[i], i);
		}
		for (String filepath : fileList) {
			String Text = readFiles(filepath);
			List<String> swords = ParticiplerByJeseg.getSpiltWords(Text);
			for(String sword : swords){
				if(record.containsKey(sword))
					intrecord[record.get(sword)] ++;
			}
		}
		for(int i=0; i<featureWords.length; i++){
			Float idf = MathTool.log((fileList.size() / (1 + intrecord[i])));
			featureIDF.put(featureWords[i], idf);
		}
	}
	
	public void computeFeatureIDF3() throws IOException{
		featureIDF = new HashMap<String, Float>();
		int[] intrecord = new int[featureWords.length];
		for(int i=0; i<featureWords.length; i++)
			intrecord[i] = 0;
		HashMap<String, Integer> record = new HashMap<String, Integer>();
		for(int i=0; i<featureWords.length; i++){
			record.put(featureWords[i], i);
		}
		for (String filepath : fileList) {
			String Text = readFiles(filepath);
			for (int i = 0; i < featureWords.length; i++) {
				if (Text.indexOf(featureWords[i]) >= 0)
					intrecord[i]++;
			}

		}
		for(int i=0; i<featureWords.length; i++){
			Float idf = MathTool.log((fileList.size() / (1 + intrecord[i])));
			featureIDF.put(featureWords[i], idf);
			System.out.println(featureWords[i]);
			System.out.println(intrecord[i]);
			System.out.println(idf);
			System.out.println("------------------");
		}
	}
	
	public static String readFiles(String file) throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
		BufferedReader br = new BufferedReader(is);
		String line = br.readLine();
		while (line != null) {
			sb.append(line).append("\n");
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ComputeAllFIDF t = new ComputeAllFIDF("/home/ub/Corpus/Reduced", "mergedFeature(Jeseg).txt");
		t.writeToFile("ALLIDF4");
/*		for(int i=0; i<100000000; i++)
			System.out.println(i);*/
	}
	
}
