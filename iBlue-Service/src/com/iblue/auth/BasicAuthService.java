package com.iblue.auth;

import com.iblue.auth.AuthMsgInterface;

public class BasicAuthService implements AuthServiceInterface {

	@Override
	public boolean isValidMsg(AuthMsgInterface msg) {
		// TODO implement
		return true;
	}

	@Override
	public String generateToken(String user) {
		// TODO implement
		return "";
	}

}
