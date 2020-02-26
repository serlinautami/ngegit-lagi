package id.adrena.oauth.model;

public class SessionRequest {
	private String username;
	private String password;
	
	public SessionRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SessionRequest() {
		super();
		
	}
	
	

}
