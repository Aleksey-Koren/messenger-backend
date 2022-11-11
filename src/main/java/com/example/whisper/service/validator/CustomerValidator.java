package com.example.whisper.service.validator;

import com.example.whisper.entity.Message;

import java.util.List;
import java.util.UUID;

public class CustomerValidator {

    public static boolean isValidUpdateTitle(List<Message> messages) {
        if (isEmpty(messages)) {
            return false;
        }
        return areFieldsCorrectUpdateTitle(messages);
    }

    private static boolean isEmpty(List<Message> messages) {
        return messages == null || messages.isEmpty();
    }

    private static boolean areFieldsCorrectUpdateTitle(List<Message> messages) {
        Message oneFromAll = messages.get(0);
        for (Message message : messages) {
            if (isEmpty(message.getSender()) ||
                    isEmpty(message.getReceiver()) ||
                    message.getType() == null ||
                    isEmpty(message.getData()) ||
                    !message.getSender().equals(oneFromAll.getSender())
            ) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(UUID uuid) {
        return uuid == null;
    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
}
