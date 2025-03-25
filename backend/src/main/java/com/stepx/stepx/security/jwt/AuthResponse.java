package com.stepx.stepx.security.jwt;

public class AuthResponse {

	private Status status;
	private String message;
	private String error;
	private String accessToken;  // Agregamos el token a la respuesta

	public enum Status {
		SUCCESS, FAILURE
	}

	public AuthResponse() {
	}

	public AuthResponse(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public AuthResponse(Status status, String message, String accessToken) {
		this.status = status;
		this.message = message;
		this.accessToken = accessToken;
	}

	public AuthResponse(Status status, String message, String error, String accessToken) {
		this.status = status;
		this.message = message;
		this.error = error;
		this.accessToken = accessToken;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String toString() {
		return "AuthResponse [status=" + status + ", message=" + message + ", error=" + error + ", accessToken=" + accessToken + "]";
	}

}
