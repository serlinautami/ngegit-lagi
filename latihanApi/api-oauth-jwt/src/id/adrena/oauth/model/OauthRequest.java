package id.adrena.oauth.model;

public class OauthRequest {
	private String refreshToken;

	public OauthRequest(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public OauthRequest() {
		super();
		
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	

}
