package com.maxcdn;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class MaxCDNApi extends DefaultApi10a {
	
	  private static final String AUTHORIZE_URL = "https://rws.maxcdn.com/oauth/authorize?oauth_token=%s";
	  private static final String REQUEST_TOKEN_RESOURCE = "rws.maxcdn.com/oauth/request_token";
	  private static final String ACCESS_TOKEN_RESOURCE = "rws.maxcdn.com/oauth/access_token";
	/*
	 * 
	 * (non-Javadoc)
	 * @see org.scribe.builder.api.DefaultApi10a#getRequestTokenEndpoint()
	 * Overriding point for 3-Legged Authentication
	 */
	  @Override
	  public String getAccessTokenEndpoint()
	  {
	    return "https://" + ACCESS_TOKEN_RESOURCE;
	  }

	  @Override
	  public String getRequestTokenEndpoint()
	  {
	    return "https://" + REQUEST_TOKEN_RESOURCE;
	  }


	@Override
	public String getAuthorizationUrl(Token requestToken) {
		// TODO Auto-generated method stub
		 return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

}
