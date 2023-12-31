package org.immo.servicepublicapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MutationAPI extends AbstractRequestAPI {
    final String URL_API = "https://apidf-preprod.cerema.fr/dvf_opendata/mutations/";

    public MutationAPI (int idMutation) throws IOException, URISyntaxException {
        URL = new URI((URL_API+idMutation)).toURL();
        System.out.println(URL);
        this.conn = this.getRequestResult(this.URL);
    }
}

