package com.maxcdn;

import org.json.JSONException;

public class MaxCDNRequest extends MaxCDNObject {

	public MaxCDNRequest() throws JSONException {
		super();
		// TODO Auto-generated constructor stub
	}
	public MaxCDNRequest(String json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}
	public MaxCDNRequest(String key,Object data) throws JSONException{
		super();
		this.put(key, data);
		//code = this.getInt("code");
	}
	public MaxCDNRequest append(String key, Object value){
		try {
			this.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

}
