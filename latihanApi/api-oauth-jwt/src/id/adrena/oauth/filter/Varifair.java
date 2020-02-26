package id.adrena.oauth.filter;

import java.io.IOException;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;                       
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import id.adrena.api.ouath.utils.JWKUtils;

public class Varifair {

	// mengambil jwk yang sama di satu server
	private static JWKSource<SecurityContext> getJWKS() throws IOException, ParseException {
		JWKSource<SecurityContext> key = new ImmutableJWKSet<SecurityContext>(JWKUtils.getSigningSet());
		return key;
	}
	private static JWSKeySelector<SecurityContext> getKey(JWSAlgorithm algorithm, JWKSource<SecurityContext> jwkSource){
		return new JWSVerificationKeySelector<>(algorithm, jwkSource);
	}
	// untuk memvarifay dan clain kedalam jwt
	public static JWTClaimsSet getJWTClaims(String token) throws IOException, ParseException, BadJOSEException, JOSEException
	{
		ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<SecurityContext>();
		jwtProcessor.setJWSKeySelector(getKey(JWSAlgorithm.RS256, getJWKS()));
		
		//ambil calims dari jwt
		JWTClaimsSet set = jwtProcessor.process(token, null);
		
		return set;
	}
}
