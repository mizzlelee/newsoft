package com.example.partb.util;

import java.util.HashMap;
import java.util.Map;

/**
 * http post request object
 */
public class HttpPostObject {
	private String request_code;
	private String request_url;
	private Map<String, String> request_params;
	private HttpResponseCallback resposeCallback;

	public HttpPostObject(String url, HttpResponseCallback resposeCallback, String request_code) {
		this.request_url = url;
		this.resposeCallback = resposeCallback;
		this.request_code = request_code;
	}

	/**
	 * get associated callback handler
	 *
	 * @return
	 */
	public HttpResponseCallback getResposeCallback () {
		return resposeCallback;
	}

	/**
	 * get target url
	 *
	 * @return
	 */
	public String getURL () {
		return request_url;
	}

	/**
	 * get all params
	 *
	 * @return
	 */
	public Map<String, String> getParams () {
		return request_params;
	}

	/**
	 * put a post param
	 *
	 * @param key
	 * @param value
	 */
	public void putParam (String key, String value) {
		if (request_params == null)
			request_params = new HashMap<String, String>();
		request_params.put(key, value);
	}

	/**
	 * get param by key
	 *
	 * @param key
	 * @return
	 */
	public String getParam (String key) {
		String result = null;
		if (request_params != null)
			result = request_params.get(key);
		return result;
	}

	/**
	 * get request hash code, to identify unique request when request was completed
	 *
	 * @return
	 */
	public String getRequestCode () {
		return request_code;
	}

	/**
	 * callback interface
	 */
	public interface HttpResponseCallback {
		public abstract void onHttpResponse (HttpPostObject postObject, String result, int status);
	}
}
