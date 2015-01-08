package com.tyan.tr.participle;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class ParticiplerByStanford {
	 private static final String basedir = System.getProperty("SegDemo", "/javalib/stanford-segmenter-2014-08-27/data");
	 private static CRFClassifier<CoreLabel> segmenter;
	 static{
		 try {
			System.setOut(new PrintStream(System.out, true, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		    Properties props = new Properties();
		    props.setProperty("sighanCorporaDict", basedir);
		    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
		    // props.setProperty("normTableEncoding", "UTF-8");
		    // below is needed because CTBSegDocumentIteratorFactory accesses it
		    props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");

		    props.setProperty("inputEncoding", "UTF-8");
		    props.setProperty("sighanPostProcessing", "true");

		    segmenter = new CRFClassifier<CoreLabel>(props);
		    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
	 }
	  public static List<String> getSpiltWords(String sentence) throws Exception {
//	    System.setOut(new PrintStream(System.out, true, "utf-8"));
//
//	    Properties props = new Properties();
//	    props.setProperty("sighanCorporaDict", basedir);
//	    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
//	    // props.setProperty("normTableEncoding", "UTF-8");
//	    // below is needed because CTBSegDocumentIteratorFactory accesses it
//	    props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
//
//	    props.setProperty("inputEncoding", "UTF-8");
//	    props.setProperty("sighanPostProcessing", "true");
//
//	    CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
//	    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);

	    String sample = "我住在美国。";
	    List<String> segmented = segmenter.segmentString(sentence);
	    System.out.println(segmented);
		return segmented;
	  }
	  
	  public static void main(String[] args) throws Exception {
		  ParticiplerByStanford.getSpiltWords("我住在美国。");
	}
}
