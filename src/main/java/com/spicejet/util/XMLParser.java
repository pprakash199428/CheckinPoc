package com.spicejet.util;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * Facilitates xml parsing.
 * @param <T> - Class for which Marshalling and Un Marshalling need to be done.
 */
public class XMLParser<T> {

	static Logger logger = Logger.getRootLogger();

    /**
     * Method will Un-Marshall the parsed stream against passed class.
     *
     * @param stream - Input Stream , which will contain the content that need to be un-marshall.
     * @param dtoClass - Class for which Marshalling and Un Marshalling need to be done.
     * @param <T> - Class for which Marshalling and Un Marshalling need to be done.
     * @return - Generated Object after Un-Marshalling.
     */
	public static <T> T unMarshall(InputStream stream, Class<T> dtoClass) {

		logger.info("Unmarshalling for " + dtoClass.getName());

		JAXBContext jaxbContext;
		T unmarshalledObj = null;
		try {
			jaxbContext = JAXBContext.newInstance(dtoClass);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			unmarshalledObj = (T) jaxbUnmarshaller.unmarshal(stream);
			logger.debug("Unmarshalling for " + dtoClass.getName() + "successful");
		} catch (JAXBException e) {
			logger.error("Unmarshalling for " + dtoClass.getName() + "not done.", e);
		}
		return unmarshalledObj;
	}
}
