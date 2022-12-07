package com.scnsoft.bot.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.scnsoft.bot.dto.ChatDto;
import com.scnsoft.bot.dto.MessageDto;
import com.scnsoft.bot.dto.MessageDto.MessageType;
import com.scnsoft.bot.logic.MessengerBot;
import com.scnsoft.bot.logic.crypt.AesAlgorithm;
import com.scnsoft.bot.model.Message;
import com.scnsoft.bot.service.MessageService;

/**
 * Handler for messages coming from the messenger application
 */
@Service
public record MessageServiceImpl(
    AesAlgorithm aesAlgorithm,
    RestTemplate restTemplate,
    MessengerBot messengerBot
) implements MessageService {

    private static final int KEY_SIZE = 16;

    // TODO: move this url to application property as source application url
    private static final String MESSENGER_URL = "http://localhost:8080";

    
    
    /**
     * Receives a message from the messenger application, transmits it to the bot, 
     * receives a response from the user bot and sends the bot's response to all 
     * chat users except the bot itself
     * 
     * @param messageDto the message coming from the messenger
     * @return messages sent back to users
     */
    @Override
    public List<MessageDto> respondOnBotMessage(MessageDto messageDto) {
        String decryptedMessageData = new String(aesAlgorithm.decrypt(messageDto), StandardCharsets.UTF_8);
        Message decryptedIncommingMessage = new Message(decryptedMessageData, null);
        Message botAnswer = messengerBot.handleIncommingMessage(decryptedIncommingMessage);

        UUID botId = messageDto.receiver();
        UUID chatId = messageDto.chat();
        String chatUrl = MESSENGER_URL + "/chats/" + chatId;
        ResponseEntity<ChatDto> chatResponse = restTemplate.getForEntity(chatUrl, ChatDto.class);
        ChatDto chat = chatResponse.getBody();
        
        return createEcryptedMessageForEachChatMemberFromBot(botAnswer.data(), chat, botId);
    }
    
    private List<MessageDto> createEcryptedMessageForEachChatMemberFromBot(String decryptedMessageData, ChatDto chat, UUID botId) {
        return chat.members()
            .stream()
            .filter(member -> !Objects.equals(member.id(), botId))
            .map(member -> {
                String generatedNonce = aesAlgorithm.generateNonce(KEY_SIZE);
                
                MessageDto decryptedMessage = MessageDto.builder()
                    .sender(botId)
                    .receiver(member.id())
                    .chat(chat.id())
                    .type(MessageType.whisper)
                    .data(decryptedMessageData)
                    .nonce(generatedNonce)
                    .build();

                String encryptedMessageData = new String(aesAlgorithm.encrypt(decryptedMessage), StandardCharsets.UTF_8);
                return MessageDto.builder()
                    .sender(decryptedMessage.sender())
                    .receiver(decryptedMessage.receiver())
                    .chat(decryptedMessage.chat())
                    .type(decryptedMessage.type())
                    .data(encryptedMessageData)
                    .nonce(decryptedMessage.nonce())
                    .build();
            })
            .toList();
    }
}
