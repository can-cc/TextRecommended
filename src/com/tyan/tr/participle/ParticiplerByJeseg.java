package com.tyan.tr.participle;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.lionsoul.jcseg.ASegment;
import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;


public class ParticiplerByJeseg {
	public static JcsegTaskConfig config = new JcsegTaskConfig(
			"bin/jcseg.properties");
	public static ADictionary dic = DictionaryFactory
			.createDefaultDictionary(config);
	public static ASegment seg;
	
	static {
		try {
			 seg = (ASegment) SegmentFactory.createJcseg(
					JcsegTaskConfig.COMPLEX_MODE, new Object[] { config, dic });
		} catch (JcsegException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> getSpiltWords(String sentence) throws IOException{
		seg.reset(new StringReader(sentence));
		List<String> words = new ArrayList<String>();
		IWord word = null;
		while ( (word = seg.next()) != null ) {
			words.add(word.getValue());
		}
		return words;
	}
	

	
	public static void main(String[] args) throws IOException {
		String sentence = "文本分类语料库设计为基于搜狐分类目录手工编辑的网页分类结果组织成的网页、分类结果及基准分类算法在内的综合数据集合";
		System.out.println(getSpiltWords(sentence).toArray());
		
	}

}
