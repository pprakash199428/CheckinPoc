package com.spicejet.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Will handle operation needed for all the resource files.
 */
@Component
public class ResouceHandler {

	Logger logger = Logger.getLogger(ResouceHandler.class);

	@Autowired
	ResourceLoader resourceLoader;

    /**
     * Method will load file mentioned in passed parameter and
     * returns object of File @File.
     *
     * @param fileName - File need to be loaded.
     * @return - File object that has been loaded.
     *
     * @throws IOException - In case of any error occured while laoding file.
     */
	public File getFile(String fileName) throws IOException {
		logger.debug("Loading : " + fileName);
		Resource resource = resourceLoader.getResource("classpath:" + fileName);
		File loadedResource = null;
		try {
			loadedResource = resource.getFile();
			logger.debug("Loaded : " + fileName);
		} catch (IOException e) {
			logger.error("Failed to load file. ", e);
			throw new IOException("Unable to Load File.");
		}
		return loadedResource;
	}

    /**
     * Method will load file mentioned in passed parameter and
     * returns InputStream of File @File.
     *
     * @param fileName - File need to be loaded.
     * @return - InputStream object of File that has been loaded.
     *
     * @throws IOException - In case of any error occured while laoding file.
     */

	public InputStream getFileStream(String fileName) throws IOException {
		logger.debug("Loading : " + fileName);
		Resource resource = resourceLoader.getResource("classpath:" + fileName);
		InputStream stream = null;
		try {
			logger.debug("Trying to generate Stream.");
			stream = resource.getInputStream();
			logger.debug("Generated Stream  successfully.");

		} catch (IOException e) {
			logger.error("Failed to load file. ", e);
			throw new IOException("Unable to Load File.");
		}
		return stream;
	}

}
