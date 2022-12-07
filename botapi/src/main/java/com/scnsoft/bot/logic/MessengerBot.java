package com.scnsoft.bot.logic;

import com.scnsoft.bot.model.Message;

/**
 * Basic interface for creating custom bots.
 */
public interface MessengerBot {

    /**
     * Processes incoming messsage and returns custom bot response to this message.  
     * User can add different logic to process messages.
     * 
     * @param message  the incoming message for the bot
     * @return  the message, which bot send after proccessing message
     */
    Message handleIncommingMessage(Message message);

    /**
     * Gets bot ID string, which is correct UUID
     * @return  the bot ID of the UUID type
     */
    String getBotId();

    /**
     * Gets bot public key string in PKCS#8 format.
     * PKCS#8 format is a format where the public key header starts with BEGIN PRIVATE KEY.
     * @return the bot public key string in PKCS#8 format
     */
    String getPublicKey();

    /**
     * Get bot private key string in PKCS#1 format.
     * PKCS#1 format is a format where the private key header starts with BEGIN RSA PRIVATE KEY
     * @return the bot private key string in PKCS#1 format
     */
    String getPrivateKey();
}
