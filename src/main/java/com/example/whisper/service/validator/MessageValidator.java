package com.example.whisper.service.validator;

import com.example.whisper.entity.File;
import com.example.whisper.entity.Message;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageValidator {

    public static boolean isNotValid(List<Message> messages) {
        if (isEmpty(messages)) {
            return true;
        }
        return areFieldsCorrect(messages);
    }

    private static boolean areFieldsCorrect(List<Message> messages) {
        Message controlMessage = messages.get(0);
        for (Message message : messages) {

            System.out.println(isEmpty(message.getSender()));
            System.out.println(isEmpty(message.getReceiver()));
            System.out.println(message.getType() == null);
            System.out.println(!message.getType().equals(controlMessage.getType()));
            System.out.println(!message.getSender().equals(controlMessage.getSender()));
            System.out.println(controlMessage.getChat() == null ? message.getChat() != null : !controlMessage.getChat().equals(message.getChat()));
            System.out.println(!((controlMessage.getAttachments() != null) == (message.getAttachments() != null)));

            if (isEmpty(message.getSender()) ||
                    isEmpty(message.getReceiver()) ||
                    message.getType() == null ||
                    !message.getType().equals(controlMessage.getType()) ||
                    (!(Message.MessageType.WHO.equals(message.getType())) && isEmpty(message.getData())) && isEmpty(message.getFiles()) ||
                    !message.getSender().equals(controlMessage.getSender()) ||
                    controlMessage.getChat() == null ? message.getChat() != null : !controlMessage.getChat().equals(message.getChat()) ||
                    !((controlMessage.getAttachments() != null) == (message.getAttachments() != null))
            ) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmpty(UUID uuid) {
        return uuid == null;
    }

    public static boolean isEmpty(List<Message> messages) {
        return messages == null || messages.isEmpty();
    }

    private static boolean isEmpty(Set<File> files) {
        return files.size() == 0;
    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
}
