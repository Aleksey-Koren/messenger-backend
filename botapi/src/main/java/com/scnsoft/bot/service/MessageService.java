package com.scnsoft.bot.service;

import java.util.List;

import com.scnsoft.bot.dto.MessageDto;

/**
 * Handler for messages coming from the messenger application
 */
public interface MessageService {
    /**
     * Accepts a message from the messenger application sends the bot's response to users
     * 
     * @param message the message coming from the messenger
     * @return messages sent back to users
     */
    List<MessageDto> respondOnBotMessage(MessageDto message);
}
