package id.adrena.api.oauth.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nimbusds.jose.jwk.JWKSet;

import id.adrena.api.ouath.utils.JWKUtils;

@Path(".well-know") // well- know konfigurasi secara umum agar orang lain tau secara umum
public class JWKEndpoint {
	

	@GET
	@Path("jwks.json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKeys() {
		JWKSet keys = null;
		
		try {
			keys = JWKUtils.getSigningSet();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Response
				.status(Response.Status.OK)
				.entity(keys.toJSONObject(true))
				.build();
	}

}
