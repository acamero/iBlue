package com.iblue.auth;

import com.iblue.auth.AuthMsgInterface;

public interface AuthServiceInterface {

	public boolean isValidMsg(AuthMsgInterface msg);
	
	public String generateToken(String user) ;	
	
}
