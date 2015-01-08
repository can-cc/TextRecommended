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


public class ComputeTFIDF {
	private String[] featureWords;
	private List<String> fileList;
	private HashMap<String, Float> featureIDF;
	private String dirPath;
	HashMap<String, List<String>> classesfilelist;
	
	public String[] getFeatureWords(){
		return featureWords;
	}
	
	public ComputeTFIDF(String dirPath, String featurefile) throws FileNotFoundException, IOException {
		fileList = new ArrayList<String>();
		this.dirPath = dirPath;
		readDirs(dirPath);
		loadFeatureWord(featurefile);
	}
	
	public void loadFeatureWord(String filePath) throws IOException{
		featureWords = null;
		List<String> featureWordsList = new ArrayList<String>();
		InputStreamReader is = new InputStreamReader(
				new FileInputStream(filePath), "UTF-8");
		BufferedReader br = new BufferedReader(is);
		String line = br.readLine();
		while(line != null){
			featureWordsList.add(line);
			line = br.readLine();
		}
		br.close();
		featureWords = (String[]) featureWordsList.toArray(new String[featureWordsList.size()]);
	}
	
	public void readDirs(String filepath) throws FileNotFoundException,IOException {
		
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
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
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
	
	public static String[] participle(String file) throws IOException {
		String text = ComputeTFIDF.readFiles(file);
		List<String> words = ParticiplerByJeseg.getSpiltWords(text);
		String[] participledWords = (String[]) words.toArray(new String[words.size()]);
		return participledWords;
	}
	
	public HashMap<String, Float> computeTextIF(String[] words) {
		HashMap<String, Float> wordTF = new HashMap<String, Float>();
		int wordNum = words.length;
		int wordcount;
		for(String word : featureWords){
			wordcount = 0;
			Float wordtf;
			for(int i=0; i<wordNum; i++){
				if( word.equals(words[i]) ){
					wordcount++;
				}
			}
			//wordtf =  ((float)wordcount / (float)wordNum);
			wordtf =  (float) (wordcount);
			wordTF.put(word, wordtf);
		}
		//System.out.println(wordNum);
		return wordTF;
	}
	
	public void computeFeatureIDF() throws FileNotFoundException, IOException{
		featureIDF = new HashMap<String, Float>();
		int containCount;
		for(String word : featureWords){
			containCount = 0;
			for(String filepath : fileList){
				String Text = readFiles(filepath);
				if( Text.indexOf(word) >= 0 )
					containCount++;
			}
			Float idf = MathTool.log( ( fileList.size() / ( 1 + containCount ) ) );
			featureIDF.put(word, idf);
		}
	}
	
	public HashMap<String, Float> computeTextTFIDF(String filePath) throws FileNotFoundException, IOException{
		if(featureIDF == null)
			computeFeatureIDF();
		HashMap<String, Float> TFIDF = new HashMap<String, Float>();
		String[] words = participle( filePath );
		HashMap<String, Float> wordTF = computeTextIF(words);
		for(String word: wordTF.keySet()){
			float idf = featureIDF.get(word);
			Float tfidf = wordTF.get(word) * idf ;
			TFIDF.put(word, tfidf);
		}
		//System.out.println(TFIDF);
		//System.out.println(fileList.size());
		return TFIDF;
	}
	
	public void generateAllTFIDF() throws FileNotFoundException, IOException{
		for(String filePath : fileList){
			computeTextTFIDF(filePath);
		}
	}
	
	public void generateLibSvmFormat() throws FileNotFoundException, IOException{
		HashMap<String, String> classinfo = new HashMap<String, String>();
		int incrlable = 1;
		for(String classes: classesfilelist.keySet()){
			classinfo.put(classes, incrlable + "");
			List<String> files = classesfilelist.get(classes);
			for(String file : files){
				 HashMap<String, Float> TFIDF = computeTextTFIDF(file);
				 writeToTrainFile(TFIDF, incrlable + "");
			}
			incrlable++;
		}
		writeClassInfo(classinfo);
	}
	
	public void writeToTrainFile(HashMap<String, Float> TFIDF, String lable) throws IOException{
		String trainfilename = "generatedTrain.txt";
		FileWriter fw = new FileWriter(trainfilename, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw, true);
		pw.print(lable);
		for(int i=0; i<featureWords.length; i++){
			pw.print(" " + i + ":" + TFIDF.get(featureWords[i]).toString());
		}
		pw.println();
		pw.flush();
		pw.close();
		System.out.println("write success");
	}
	
	public void writeClassInfo(HashMap<String, String> classinfo) throws IOException{
		String trainfilename = "trainClassInfo.txt";
		FileWriter fw = new FileWriter(trainfilename, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw, true);
		for(String classname : classinfo.keySet()){
			pw.println(classname + ":" + classinfo.get(classname));
		}
		pw.close();
	}
	
	public void loadClassesInfo(){
		classesfilelist = new HashMap<String, List<String>>();
		File file = new File(dirPath);
		String[] filelist = file.list();
		for (int i = 0; i < filelist.length; i++) {
			File readfile = new File(dirPath + "/" + filelist[i]);
			if (readfile.isDirectory()) {
				List<String> eachfilelist = new ArrayList<String>();
				String[] filelist2 = readfile.list();
				for (int j = 0; j < filelist2.length; j++) {
					File readfile2 = new File(dirPath + "/" + filelist[i] + "/"
							+ filelist2[j]);
					if (readfile2.isFile())
						eachfilelist.add(readfile2.getAbsolutePath());
				}
				classesfilelist.put(filelist[i], eachfilelist);
			}
		}
	}

	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ComputeTFIDF test = new ComputeTFIDF("/home/ub/Corpus/Sample", "mergedFeature(Jeseg).txt");
		//test.generateAllTFIDF();
		//test.computeTextTFIDF("/home/ub/LKXJ.txt");
		test.loadClassesInfo();
		test.generateLibSvmFormat();
	}
}
