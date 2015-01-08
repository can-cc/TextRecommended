package com.tyan.tr.participle;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class ParticiplerByIK {

	public static List<String> getSpiltWords(String sentence) throws IOException {
		List<String> words = new ArrayList<String>();
		Reader text = new StringReader(sentence);
		IKSegmenter iks = new IKSegmenter(text,true);
		Lexeme token;
		while ((token = iks.next()) != null)
        {
			words.add(token.getLexemeText());
        }
		return words;
	}

}
