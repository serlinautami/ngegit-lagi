package id.adrena.oauth.service;

import java.sql.Date;
import java.time.Instant;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import id.adrena.api.ouath.utils.JWKUtils;
import id.adrena.oauth.model.SessionRequest;
import id.adrena.oauth.model.SessionResult;
import id.adrena.oauth.model.Token;
import id.adrena.oauth.model.UserData;

@Stateless
public class SessionService {
	public UserData getUserDataFromDB() {
	return new UserData(1, "common", "user@oauth.com", "HelloWorld@123");
	}
	
	public SignedJWT generateAccessToken(String keyId, UserData user, int expireTime) {
		JWSHeader header = new JWSHeader //Membikin header
				.Builder(JWSAlgorithm.RS256)
				.keyID(keyId)
				.type(JOSEObjectType.JWT)// opsional
				.build();
		
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder() //membikin body/payload
		.subject(String.valueOf(user.getUserId()))
		.claim("userType", user.getUsertype())
		.issuer("localhost.8080")
		.issueTime(Date.from(Instant.now().plusSeconds(expireTime)))
		.build();
		
		SignedJWT signedJWT = new SignedJWT(header, claimsSet);
		
		RSASSASigner signer = null;
		try {
			//signecture jwk
			JWK jwk = JWKUtils.getSigningSet().getKeyByKeyId(keyId);	
					signer = new RSASSASigner(RSAKey.parse(jwk.toJSONObject()));
					signedJWT.sign(signer);	//membikin signature
		}catch (Exception e) {
			e.printStackTrace();		
	}
		return signedJWT;
}
	private JWEObject generateRefreshToken(String keyId,SignedJWT signedJWT) {

		JWEHeader jweheader = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
				.contentType("JWT")
				.build();//header jwe (refresh token)
		
		Payload payload = new Payload(signedJWT);// payload/body jwe (refresh token)
		
		JWEObject jweObject = new JWEObject(jweheader, payload);
		
		try {
			//encypt jwe
			JWK jwke = JWKUtils.getEncyptSet().getKeyByKeyId(keyId);
			RSAEncrypter encrypter = new RSAEncrypter(RSAKey.parse(jwke.toJSONObject()));
			jweObject.encrypt(encrypter);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jweObject;
	}
	
	public SessionResult verifyUser(SessionRequest request) {
	 		UserData user = getUserDataFromDB();
	 		
		if(!request.getUsername().equals(user.getEmail())// tanda ! pengecekan false
			|| !request.getPassword().equals(user.getPassword())){
			return new SessionResult()
					.withHttpStatus(Response.Status.UNAUTHORIZED)
					.withErrorMessage("Username atau password salah.")
					.withResultSuccess(0);
		}
		//Membuat Refactoring 
		Token token = new Token();
		token.setAccess(generateAccessToken("bebas", user, 3600)
				.serialize());//untuk mengkomper objeck jwt ke string
		token.setRefresh(generateRefreshToken("bebas-enc", generateAccessToken("bebas", user, 3600*24*365))
				.serialize());
		token.setType("Bearer");
		token.setExpiresIn(3600);
		
		return new SessionResult()
				.withHttpStatus(Response.Status.OK)
				.withErrorMessage("")
				.withResultSuccess(1)
				.withToken(token);
	}
}
