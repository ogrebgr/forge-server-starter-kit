package com.bolyartech.scram_sasl.server;


/**
 * Provides server side processing of the SCRAM-SHA1 SASL authentication
 */
@SuppressWarnings("unused")
public class ScramSha1SaslServerProcessor extends AbstractScramSaslServerProcessor {
    /**
     * Creates new ScramSha1SaslServerProcessor
     * @param connectionId ID of the client connection
     * @param listener Listener
     * @param userDataLoader loader for user data
     * @param sender Sender used to send messages to the clients
     */
    public ScramSha1SaslServerProcessor(long connectionId,
                                        Listener listener,
                                        UserDataLoader userDataLoader,
                                        Sender sender) {

        super(connectionId, listener, userDataLoader, sender, "SHA-1", "HmacSHA1");
    }
}
