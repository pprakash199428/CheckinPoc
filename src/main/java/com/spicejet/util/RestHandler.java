package com.spicejet.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class RestHandler {

	public enum RequestType {
		GET, POST
	}

	Map<String, String> headerContentMap;

	String bodyContentMap;

	RequestType requestType;

	String url;

	HttpGet httpGet;

	HttpPost httpPost;

	public Map<String, String> getHeaderContentMap() {
		return headerContentMap;
	}

	public void setHeaderContentMap(Map<String, String> headerContentMap) {
		this.headerContentMap = headerContentMap;
	}

	public String getBodyContentMap() {
		return bodyContentMap;
	}

	public void setBodyContentMap(String bodyContentMap) {
		this.bodyContentMap = bodyContentMap;
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
			headerContentMap = new HashMap<>();
		}
		headerContentMap.put("Content-Type", "application/json");

		/* building request on the basis of requestType */
		if ((requestType) != null && (url != null)) {
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
				httpPost.setEntity(new ByteArrayEntity(bodyContentMap.getBytes("UTF8")));
			}
		}
	}

	public HttpResponse sendRequest() throws ClientProtocolException, IOException {
		RequestConfig config = RequestConfig.custom().setSocketTimeout(150000).setConnectTimeout(150000).build();
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(config);
        HttpClient httpClient = httpClientBuilder.build();		
        buildRequest();
		if ((requestType != null) && (url != null)) {
			if (requestType.equals(RequestType.GET)) {
				return httpClient.execute(httpGet);
			} else if (requestType.equals(RequestType.POST)) {
				return httpClient.execute(httpPost);
			}
		}
		return null;
	}

}
