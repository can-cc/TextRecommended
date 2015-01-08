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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyan.tr.participle.ParticiplerByIK;
import com.tyan.tr.participle.ParticiplerByJeseg;
import com.tyan.tr.participle.ParticiplerByStanford;

/**
 * @author ub
 *
 */
public class ReduceFeatureByCHI {
	/**A表示包含t且属于cj类的文档数;
	 * B表示包含t但是不属于cj类的文档数;
	 * C表示属于cj类但是不包含t的文档数;
	 * D表示既不属于cj类也不包含t的文档数。*/
	private int N;
	private int A;
	private int B;
	private int C;
	private int D;
	
//	private String classname;
//	private String dirPath;
	private String dirs[];
	private int classN;
	
//	private ArrayList<String> eachAllFile;
	private HashMap<String, ArrayList<String>> allFile;
//	private List<String> eachAllWord;
//	private HashMap<String, Float> allCHI;
	
	public Float computeCHI(String word, String classpath) throws FileNotFoundException, IOException{
		int a=0, b=0, c=0, d=0;
		//complute A and C
		ArrayList<String> eachAllFile = allFile.get(classpath);
		for(String filePath : eachAllFile){
			String filetext = readFiles(filePath);
			if( filetext.indexOf(word) >= 0)
				a++;
			else
				c++;
		}
		//complute B and D
		for(String eachclasspath : allFile.keySet() ){
			if( eachclasspath.equals(classpath) )
				continue;
			ArrayList<String> eachAllFile2 = allFile.get(eachclasspath);
			for(String filePath : eachAllFile2){
				String filetext = readFiles(filePath);
				if( filetext.indexOf(word) >= 0)
					b++;
				else
					d++;
			}
		}
		//compute chi
		int temp = a*d -b*c;
		Float chi = (float) (((N*temp*temp) / (1+(a+b)*(c+d)))) ;
		System.out.println(chi);
		return chi;
	}
	
	public void proccessClasses() throws Exception{
		
		for(String classpath : allFile.keySet() ){
			HashMap<String, Float> eachAllCHI = new HashMap<String, Float>();
			ArrayList<String> eachAllFile = allFile.get(classpath);
			List<String> eachAllWord = getEachAllWord(eachAllFile);
			FilterOffInvalid(eachAllWord);
			for(String word : eachAllWord){
				Float chiValue = computeCHI(word, classpath);
				eachAllCHI.put(word, chiValue);
		//		eachAllWord.remove(word);
				
			}
			writerToFile(classpath, eachAllCHI);
		}
	}
	
	public ReduceFeatureByCHI() {
		N = 0;
	}
	
	public void addDir(String dirs[]){
		this.dirs = dirs;
		this.classN = dirs.length;
	}
	
	public void getAllFile() {
		allFile = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < dirs.length; i++) {
			ArrayList<String> files = readClassesDir(dirs[i]);
			allFile.put(dirs[i], files);
		}

	}
	
	public void writerToFile(String classpath, HashMap<String, Float> eachAllCHI) throws IOException{
		int i = classpath.lastIndexOf("/");
		String filename = classpath.substring(i) + ".txt";
		String result = "feature/" + filename;
		FileWriter fw = new FileWriter(result, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw, true);

		List<Map.Entry<String, Float>> info = new ArrayList<Map.Entry<String, Float>>(
				eachAllCHI.entrySet());
		Collections.sort(info, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> obj1,
					Map.Entry<String, Float> obj2) {
				if (obj2.getValue() > obj1.getValue()) {
					return 1;
				} else if (obj2.getValue() < obj1.getValue()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		for (int j = 0; j < 1000; j++) {

			// System.out.println(info.get(j).getKey() + ": " +
			// info.get(j).getValue());
			pw.print(info.get(j).getKey() + " ");
			pw.println(info.get(j).getValue());
		}
		pw.flush();
		pw.close();
	}
	
	public ArrayList<String> readClassesDir(String dirPath){
		ArrayList<String> files = new ArrayList<String>();
		File file = new File(dirPath);
		if (!file.isDirectory()) {
			System.out.println("This is not a folder");
			System.out.println("filepath: " + file.getAbsolutePath());
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int j = 0; j < filelist.length; j++) {
				File readfile = new File(dirPath + "/" + filelist[j]);
				if (!readfile.isDirectory()) {
					// System.out.println("filepath: " +
					// readfile.getAbsolutePath());
					files.add(readfile.getAbsolutePath());
					N++;
				} else if (readfile.isDirectory()) {
					ArrayList<String> subfiles = readClassesDir(files + "/" + filelist[j]);
					files.addAll(subfiles);
				}
			}
		}
		return files;
	}
	
	public List<String> getEachAllWord(ArrayList<String> eachAllFile) throws Exception{
		//for (String classDir : eachAllFile.keySet()){
		List<String> eachAllWord = new ArrayList<String>();
			for( String filepath : eachAllFile ){
				String file = readFiles(filepath);
				//List<String> filewords = ParticiplerByJeseg.getSpiltWords(file);
				List<String> filewords = ParticiplerByJeseg.getSpiltWords(file);
				for(String word : filewords){
					if(!eachAllWord.contains(word)){
						eachAllWord.add(word);
					}
				}
			}
		//}
		//System.out.println(eachAllWord);
		return eachAllWord;
	}
	
	public void FilterOffInvalid(List<String> eachAllWord){
		//jeseg已经过滤了停止词
		//半角
		eachAllWord.remove(".");
		eachAllWord.remove(",");
		eachAllWord.remove(" ");
		eachAllWord.remove("?");
		eachAllWord.remove("/");
		eachAllWord.remove("@");
		eachAllWord.remove("!");
		eachAllWord.remove("(");
		eachAllWord.remove(")");
		eachAllWord.remove("-");
		eachAllWord.remove("=");
		eachAllWord.remove("+");
		eachAllWord.remove("#");
		eachAllWord.remove("%");
		eachAllWord.remove("*");
		eachAllWord.remove("~");
		eachAllWord.remove("`");
		eachAllWord.remove("[");
		eachAllWord.remove("]");
		eachAllWord.remove("{");
		eachAllWord.remove("}");
		eachAllWord.remove(";");
		eachAllWord.remove(":");
		eachAllWord.remove("\"");
		eachAllWord.remove("'");
		eachAllWord.remove("<");
		eachAllWord.remove(">");
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
	
	public static void main(String[] args) throws Exception {
/*		ReduceFeature test = new ReduceFeature();
		String[] dirs= {
				"/home/ub/Corpus/Sample/C000007",
				"/home/ub/Corpus/Sample/C000008",
				"/home/ub/Corpus/Sample/C000010",
				"/home/ub/Corpus/Sample/C000013",
				"/home/ub/Corpus/Sample/C000014",
				"/home/ub/Corpus/Sample/C000016",
				"/home/ub/Corpus/Sample/C000020",
				"/home/ub/Corpus/Sample/C000022",
				"/home/ub/Corpus/Sample/C000023",
				"/home/ub/Corpus/Sample/C000024"};
		test.addDir(dirs);
		test.geteachAllFile();
		test.getEachAllWord();*/
		
		String[] dirs= {
				"/home/ub/Corpus/Sample/C000007",
				"/home/ub/Corpus/Sample/C000008",
				"/home/ub/Corpus/Sample/C000010",
				"/home/ub/Corpus/Sample/C000013",
				"/home/ub/Corpus/Sample/C000014",
				"/home/ub/Corpus/Sample/C000016",
				"/home/ub/Corpus/Sample/C000020",
				"/home/ub/Corpus/Sample/C000022",
				"/home/ub/Corpus/Sample/C000023",
				"/home/ub/Corpus/Sample/C000024"};
		
		ReduceFeatureByCHI test = new ReduceFeatureByCHI();
		test.addDir(dirs);
		test.getAllFile();
		test.proccessClasses();
		
		
		
	}
	



}
