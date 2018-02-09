package com.maxcdn;

import java.security.SignatureException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class MaxCDN {
	
	public String alias;
	public String key;
	public String secret;
	private Token token_stored;
	public String MaxCDNrws_url = "https://rws.maxcdn.com/";
	
	public MaxCDN(String alias, String consumer_key, String consumer_secret){
		this.alias = alias;
		this.key = consumer_key;
		this.secret = consumer_secret;
		token_stored = null;
		
	}
	
	
	public MaxCDN(String alias, String consumer_key, String consumer_secret,Token token){
		this.alias = alias;
		this.key = consumer_key;
		this.secret = consumer_secret;
		this.token_stored = token;
		
	}
	
	/*
	 * API Methods
	 */
	public MaxCDNObject get(String endpoint){
		return this.request(endpoint, Verb.GET, null);
	}
	public MaxCDNObject delete(String endpoint){
		return this.request(endpoint, Verb.DELETE, null);
	}
	public MaxCDNObject put(String endpoint, MaxCDNRequest request){
		return this.request(endpoint, Verb.PUT, request);
	}
	public MaxCDNObject post(String endpoint, MaxCDNRequest request){
		return this.request(endpoint, Verb.POST, request);
	}
	
	public Token getRequestToken(){
		OAuthService service = new ServiceBuilder()
		   .provider(MaxCDNApi.class)
		   .apiKey(key)
		   .apiSecret(secret)
		   .build();
        return service.getRequestToken();
	}
	
	public String getAuthUrl(Token requestToken){
	    // Obtain the Request Token
		OAuthService service = new ServiceBuilder()
		   .provider(MaxCDNApi.class)
		   .apiKey(key)
		   .apiSecret(secret)
		   .build();
		
	
	    return service.getAuthorizationUrl(requestToken);
	}
	
	public Token getAccessToken(Token requestToken, String verify){
		OAuthService service = new ServiceBuilder()
		   .provider(MaxCDNApi.class)
		   .apiKey(key)
		   .apiSecret(secret)
		   .build();
		return service.getAccessToken(requestToken, new Verifier(verify) );
	}
	
	public synchronized MaxCDNObject request(String end, Verb verb, MaxCDNRequest body){
		try {
			if(token_stored == null)
			return new MaxCDNObject(this._request(end, verb, body));
			else return new MaxCDNObject(this._request(end, verb, body,token_stored));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public synchronized MaxCDNObject request(String end, Verb verb, MaxCDNRequest body,Token token){
		try {
			return new MaxCDNObject(this._request(end, verb, body,token));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Manually setting the access token
	 */
	public boolean setToken(Token token){
		token_stored = token;
		return true;
	}
	
	public synchronized String _request(String end, Verb verb, MaxCDNRequest body) throws SignatureException, Exception{	
		return _request(end, verb,body,null);
	}
	public synchronized String _request(String end, Verb verb, MaxCDNRequest body,Token token) throws SignatureException, Exception{	
		OAuthService service = new ServiceBuilder()
		   .provider(MaxCDNApi.class)
		   .apiKey(key)
		   .apiSecret(secret)
		   .build();
		 
		OAuthRequest request = new OAuthRequest(verb, this.MaxCDNrws_url + alias + end);
		request.addHeader("User-Agent", "Java MaxCDN API Client");
		if(verb == Verb.PUT || verb == Verb.POST){
			for(int i = 0;i < body.names().length(); i++){
				String key = (String) body.names().get(i);
				request.addBodyParameter(key, body.getString(key));
			}
		}
		service.signRequest((token == null) ? new Token("","") : token, request);
		 
		Response response = request.send();
		//Console.log(response.getBody());

	    return response.getBody();
	}
}
