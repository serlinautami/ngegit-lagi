package id.adrena.api.oauth.endpoint;

import java.io.IOException;
import java.text.ParseException;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import id.adrena.api.ouath.utils.JWKUtils;
import id.adrena.oauth.model.OauthRequest;
import id.adrena.oauth.model.SessionResult;
import id.adrena.oauth.model.Token;
import id.adrena.oauth.service.SessionService;

@Path("refresh")
public class OauthEndpoint {
	
	@EJB
	private SessionService service;
	
	private JWTClaimsSet verifyRefreshToken(String refreshToken) throws IOException, ParseException, BadJOSEException, JOSEException{
	   ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
	   
	    JWEAlgorithm expectedJWEAlg = JWEAlgorithm.RSA_OAEP_256;// algoritma yang di pakai refresh token kita
		EncryptionMethod expectedJWEnc = EncryptionMethod.A256GCM;// metode engkipsi yang di pakai refresh token kita
		
		// jwk set yang dipakai untuk enkrip refresh token kita
		JWKSource<SecurityContext> jweKeySource = 
				new ImmutableJWKSet<>(JWKUtils.getEncyptSet());
		
		JWEKeySelector<SecurityContext> jweKeySelector = 
				new JWEDecryptionKeySelector<>(expectedJWEAlg, expectedJWEnc, jweKeySource);
		
		jwtProcessor.setJWEKeySelector(jweKeySelector);
		
		// algoritma yang dipakai untuk signature access token kita
		JWSAlgorithm expectedJWSAlg =JWSAlgorithm.RS256;
		
		//jwk set yang di pakai untuk signature access token kita
		JWKSource<SecurityContext> jwsKeySource =
				new ImmutableJWKSet<>(JWKUtils.getSigningSet());
		
		JWSKeySelector<SecurityContext> keySelector =
				new JWSVerificationKeySelector<>(expectedJWSAlg, jwsKeySource);
		
		jwtProcessor.setJWSKeySelector(keySelector);
		
		DefaultJWTClaimsVerifier<SecurityContext> claimsVerifier =
				new DefaultJWTClaimsVerifier<SecurityContext>();
		
		
		jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);
		
		return jwtProcessor.process(refreshToken, null);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postRefreshToken(OauthRequest request) {
		SessionResult result = null;
		
		try {
			JWTClaimsSet RefreshClaims = verifyRefreshToken(request.getRefreshToken());
			
			if (RefreshClaims.getStringClaim("userType").equals("common")) {
				Token token = new Token();
				token.setAccess(
						service.generateAccessToken(
								"bebas",
								service.getUserDataFromDB(),
								3600).serialize());
				token.setExpiresIn(3600);
				token.setType("Bearer");
				
				result = new SessionResult()
						.withHttpStatus(Response.Status.OK)
						.withResultSuccess(1)
						.withErrorMessage("")
						.withToken(token);			
			} else {
				result = new SessionResult()
						.withHttpStatus(Response.Status.BAD_REQUEST)
						.withResultSuccess(0)
						.withErrorMessage("Invalid Refresh Token");	
			}	
		}catch (Exception e) {
			e.printStackTrace();
			result = new SessionResult()
					.withHttpStatus(Response.Status.INTERNAL_SERVER_ERROR)
					.withResultSuccess(0)
					.withErrorMessage("Internal Service Error");		
		}
		return Response
				.status(result.getHttpStatus())
				.entity(result)
				.build();
	}
}
