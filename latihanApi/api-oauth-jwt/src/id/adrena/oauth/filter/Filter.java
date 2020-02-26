package id.adrena.oauth.filter;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import id.adrena.oauth.model.SessionResult;


@Provider
public class Filter implements ContainerRequestFilter{
	
	private static final String UNSECURED_URL = "session";
	private static final String UNSECURED_URL2 = ".well-know";
	private static final String UNSECURED_URL3 = "refresh";
	

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		SessionResult result = null;
		
		if(requestContext.getUriInfo().getPath().contains(UNSECURED_URL)
			|| requestContext.getUriInfo().getPath().contains(UNSECURED_URL2)
			|| requestContext.getUriInfo().getPath().contains(UNSECURED_URL3)){
			return;
		}
		
		//ngecek ototitensi headaer
		List<String> authHeader = requestContext.getHeaders().get("Authorization");
		
		if(authHeader != null && authHeader.size() > 0) {
			String authorization = authHeader.get(0);
			StringTokenizer tokenizer = new StringTokenizer(authorization," ");
			
			try {
				String authType = tokenizer.nextToken();
				String token = tokenizer.nextToken();
				JWTClaimsSet set = Varifair.getJWTClaims(token);
				
				if(authType.equals("Bearer") && set != null) {
					return; //varifikasi berhasil
				}
			}catch (NoSuchElementException e) {
				result = new SessionResult()
						.withHttpStatus(Response.Status.BAD_REQUEST)
						.withResultSuccess(0)
						.withErrorMessage("invaid Authorization Header");
			} catch (ParseException | BadJOSEException | JOSEException e) {
				e.printStackTrace();
				
				
				if(e.getMessage().contains("Expired JWT")) {
					result = new SessionResult()
							.withHttpStatus(Response.Status.UNAUTHORIZED)
							.withResultSuccess(0)
							.withErrorMessage("Expired Token");
				}else {
					result = new SessionResult()
							.withHttpStatus(Response.Status.BAD_REQUEST)
							.withResultSuccess(0)
							.withErrorMessage("Invalid Token");
				}
				requestContext.abortWith(
						Response
						.status(result
						.getHttpStatus())
						.entity(result)
						.build());
			}		
	}		
  }
}
