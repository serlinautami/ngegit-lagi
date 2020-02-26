package id.adrena.oauth.model;

import javax.json.bind.annotation.JsonbTransient;
import javax.ws.rs.core.Response;

public class SessionResult {
	@JsonbTransient
	private int httpStatus;
	
	
	private int resultsuccess;
	private String errorMessage;
	
	
	private Token token; // dari file token di panggil
	


	public SessionResult(int httpStatus, int resultsuccess, String errorMessage, Token token) {
		super();
		this.httpStatus = httpStatus;
		this.resultsuccess = resultsuccess;
		this.errorMessage = errorMessage;
		this.token = token;
	}
	public SessionResult() {
		super();
		
	}
	
	public int getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	public int getResultsuccess() {
		return resultsuccess;
	}
	public void setResultsuccess(int resultsuccess) {
		this.resultsuccess = resultsuccess;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public SessionResult withHttpStatus(Response.Status status) {
		this.httpStatus = status.getStatusCode();
		return this;
	}
	public SessionResult withResultSuccess(int resultsuccess) {
		this.resultsuccess = resultsuccess;
		return this;
	}
	public SessionResult withErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
	public SessionResult withToken(Token token) {
		this.token= token;
		return this;
	}
	
}
