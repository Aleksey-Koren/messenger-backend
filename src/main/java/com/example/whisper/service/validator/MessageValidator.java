package com.example.whisper.service.validator;

import com.example.whisper.entity.Message;

import java.util.List;

public class MessageValidator {

    public static boolean isValid(List<Message> messages) {
        if (isEmpty(messages)) {
            return false;
        }
        return areFieldsCorrect(messages);
    }

    private static boolean areFieldsCorrect(List<Message> messages) {
        Message controlMessage = messages.get(0);
        for (Message message : messages) {
            if (
                    !message.getType().equals(controlMessage.getType()) ||
                            (isEmpty(message.getData())) && isEmpty(message.getAttachments()) ||
                            !message.getSender().equals(controlMessage.getSender()) ||
                            controlMessage.getChat() == null ? message.getChat() != null : !controlMessage.getChat().equals(message.getChat()) ||
                            !((controlMessage.getAttachments() != null) == (message.getAttachments() != null))
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(List<Message> messages) {
        return messages == null || messages.isEmpty();
    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
}
