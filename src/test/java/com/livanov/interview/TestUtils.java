package com.livanov.interview;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * Common util that can be used in testing
 * 
 * @author hrist
 *
 */
public class TestUtils {

	/**
	 * Reads file resource to bytes
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static byte[] getFileContent(String fileName) throws IOException, URISyntaxException {

		// improved this method call a bit
    	return Files.readAllBytes(Paths.get(TestUtils.class.getClassLoader().getResource(fileName).toURI()));
    }
}
