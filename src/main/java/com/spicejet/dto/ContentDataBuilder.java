package com.spicejet.dto;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.spicejet.enums.Template;
import com.spicejet.util.ResouceHandler;
import com.spicejet.util.XMLParser;

/**
 * Builder to prepare Content Data.
 *
 */
@Component
public class ContentDataBuilder<T> {

	Logger logger = Logger.getLogger(ContentDataBuilder.class);

	@Autowired
	ResouceHandler resouceHandler;

	@Autowired
	Environment env;

    /**
     * Method to prepare Content Data to pass to client.
     *
     * @param contentClassList - List of Raw Content Object.
     * @return - Prepared Data for Content.
     */
	public Data buildContentResponse(List<T> contentClassList, com.spicejet.enums.Template template) {
		logger.debug("Going to make Content Data String.");
		List<String> responseList = new ArrayList<>();
		Content contentTemplate = loadDataTemplate(template);

		for (T content : contentClassList) {
			StringBuffer response = new StringBuffer();
			response.append(contentTemplate.getHeader());
			for (Field field : contentTemplate.getField()) {
				response.append(field.getPosition());
				if (field.isLabel())
					response.append(field.getContent());
				else {
					String methodName = field.getMethodName();
                    String className = content.getClass().getCanonicalName();
					try {
                        Class classB = Class.forName(className);
                        Method method = classB.getDeclaredMethod(methodName);
						response.append(method.invoke(content));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						logger.error("Unable to find field Method : " + methodName + " for "+className+" class.");
					} catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
				response.append(contentTemplate.getDelimiter());
			}
            response.append(contentTemplate.getEndChar());
			String responseString = response.toString();
			logger.debug("String Prepared :  " + responseString);
			responseList.add(responseString);
		}
		return prepareData(responseList, template);
	}

    /**
     * Method will unMarshall and load the Content Template File .
     *
     * @return - Loads Content Template.
     */
	public Content loadDataTemplate(com.spicejet.enums.Template templates) {
		Content content = new Content();
		try {
			InputStream stream = resouceHandler.getFileStream(env.getProperty(templates.getPropertyName()));
			content = XMLParser.<Content> unMarshall(stream, Content.class);
		} catch (IOException exception) {
			logger.error("Unable to load content template.", exception);
		}
		return content;
	}

    /**
     * Method will construct Data object from Data Strings.
     *
     * @param dataList - Data List for Content.
     * @return - Data : Constructed Data String List.
     */
	public Data prepareData(List<String> dataList, Template template){
		logger.debug("Transforming the data string to Data Holder XML.");
		Data data = new Data();
		data.setType(template.getContentType().getDisplayName());
		for(String dataString : dataList){
			data.getDatum().add(dataString);
		}
		logger.debug("Done.");
		return data;
	}

}
