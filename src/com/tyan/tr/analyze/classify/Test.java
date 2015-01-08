package com.tyan.tr.analyze.classify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		// Pattern pattern = Pattern.compile("^[1-9]d*.*");
		  Pattern pattern = Pattern.compile("[(^[0-9]*.)(^[0-9]*)]*");
		  Matcher matcher = pattern.matcher("5465464");
		  boolean b= matcher.matches();
		  //当条件满足时，将返回true，否则返回false
		  System.out.println(b);
	}
}
