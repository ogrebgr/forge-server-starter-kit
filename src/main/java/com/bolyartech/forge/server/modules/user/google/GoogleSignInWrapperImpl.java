package com.bolyartech.forge.server.modules.user.google;

import com.bolyartech.forge.server.modules.user.facebook.ExternalUser;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class GoogleSignInWrapperImpl implements GoogleSignInWrapper {
    private final NetHttpTransport transport = new NetHttpTransport();
    private final JsonFactory mJsonFactory = new GsonFactory();
    private final GoogleIdTokenVerifier mVerifier = new GoogleIdTokenVerifier(transport, mJsonFactory);

    @Override
    public ExternalUser checkToken(String token) {
        try {
            GoogleIdToken idToken = mVerifier.verify(token);
            GoogleIdToken.Payload payload = idToken.getPayload();

            return new ExternalUser(payload.getSubject(), payload.getEmail());
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }
}
