package com.tyan.tr.tool;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class FilterOffUtf8Mb4 {
	public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {  
        byte[] bytes = text.getBytes("UTF-8");  
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);  
        int i = 0;  
        while (i < bytes.length) {  
            short b = bytes[i];  
            if (b > 0) {  
                buffer.put(bytes[i++]);  
                continue;  
            }  
            b += 256;  
            if ((b ^ 0xC0) >> 4 == 0) {  
                buffer.put(bytes, i, 2);  
                i += 2;  
            }  
            else if ((b ^ 0xE0) >> 4 == 0) {  
                buffer.put(bytes, i, 3);  
                i += 3;  
            }  
            else if ((b ^ 0xF0) >> 4 == 0) {  
                i += 4;  
            }  
        }  
        buffer.flip();  
        return new String(buffer.array(), "utf-8");  
    }  
	

}
