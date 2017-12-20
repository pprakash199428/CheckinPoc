package com.spicejet.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

@Component
public class RestRequester {

	public enum RequestType {
		GET, POST
	}

	Map<String, String> headerContentMap;
	Map<String, String> bodyContentMap;
	RequestType requestType;
	String url;
	HttpGet httpGet;
	HttpPost httpPost;

	public Map<String, String> getHeaderContentMap() {
		if (headerContentMap == null) {
			headerContentMap = new HashMap<String, String>();
		}
		return headerContentMap;
	}

	public Map<String, String> getBodyContentMap() {
		if (bodyContentMap == null) {
			bodyContentMap = new HashMap<String, String>();
		}
		return bodyContentMap;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
		if (this.requestType.equals(RequestType.POST)) {
			this.httpPost = new HttpPost();
		} else if (this.requestType.equals(RequestType.GET)) {
			this.httpGet = new HttpGet();
		}
	}

	public RequestType getRequestType() {
		return requestType;
	}

	private void buildRequest() throws UnsupportedEncodingException {
		/* Setting default values in header */
		if (headerContentMap == null) {
			headerContentMap = new HashMap<String, String>();
		}
		headerContentMap.put("Content-Type", "application/x-www-form-urlencoded");
		headerContentMap.put("Accept", "application/json");
		/* building request on the basis of requestType */
		if (((requestType) != null) && (url != null)) {
			if (requestType.equals(RequestType.GET)) {
				httpGet.setURI(URI.create(url));
				for (Map.Entry<String, String> entry : headerContentMap.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			} else if (requestType.equals(RequestType.POST)) {
				httpPost.setURI(URI.create(url));
				for (Map.Entry<String, String> entry : headerContentMap.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : bodyContentMap.entrySet()) {
					urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			}
		}
	}

	public HttpResponse sendRequest() {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			buildRequest();
			if ((requestType != null) && (url != null)) {
				if (requestType.equals(RequestType.GET)) {
					return httpClient.execute(httpGet);
				} else if (requestType.equals(RequestType.POST)) {
					return httpClient.execute(httpPost);
				}
			}
		} catch (IOException ioException) {
		}
		return null;
	}
}
