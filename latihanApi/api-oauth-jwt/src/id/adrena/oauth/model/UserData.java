package id.adrena.oauth.model;

public class UserData {
	private int userId;
	private String usertype;
	private String email;
	private String password;
	
  public UserData(int userId, String usertype, String email, String password) {
	super();
	this.userId = userId;
	this.usertype = usertype;
	this.email = email;
	this.password = password;
}

	public UserData() {
		super();
		
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUsertype() {
		return usertype;
	}
	
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	};
	
		

}
