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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeFeature {
	private String dirPath;
	private List<String> feature;
	
	public MergeFeature(String dirPath) {
		this.dirPath = dirPath;
		feature = new ArrayList<String>();
	}
	
	public void readFiles(String file) throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
	//	InputStreamReader is = new InputStreamReader(new FileInputStream(file));
		BufferedReader br = new BufferedReader(is);
		String line = br.readLine();
		while (line != null) {
			int space = line.indexOf(" ");
			String word = line.substring(0, space - 1);
			if( ! feature.contains(word) )
				feature.add(word);
			line = br.readLine();
		}
		br.close();
	}
	
	public ArrayList<String> readDir(){
		ArrayList<String> files = new ArrayList<String>();
		File file = new File(dirPath);
		if (!file.isDirectory()) {
			System.out.println("This is not a folder");
			System.out.println("filepath: " + file.getAbsolutePath());
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for(int i=0; i<filelist.length; i++){
				File readfile = new File(dirPath + "/" + filelist[i]);
				if(readfile.isDirectory())
					System.out.println("there is a folder");
				else if(readfile.isFile())
					files.add(filelist[i]);
			}
		}
		return files;
	}
	
	//正则过滤判断
	public boolean shouldFilter(String word){
		Pattern pattern = Pattern.compile("[(^[0-9]*.)(^[0-9]*)]*");
		Matcher matcher = pattern.matcher(word);
		return matcher.matches();
	}
	
	public void merge() throws FileNotFoundException, IOException{
		ArrayList<String> files = readDir();
		for(String filepath : files){
			readFiles(dirPath + "/" + filepath);
		}
	}
	
	public void show(){
		System.out.println(feature);
	}
	
	public void writeToFile() throws IOException{
		String filename = "mergedFeature(Jeseg).txt";
		FileWriter fw = new FileWriter(filename, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw, true);
		for(String word :feature)
			if(!shouldFilter(word))
				pw.println(word);
		pw.flush();
		pw.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		MergeFeature test = new MergeFeature("feature");
		test.merge();
		//test.show();
		test.writeToFile();
	}

}
