package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.MessageService;
import com.iwebpp.crypto.TweetNaclFast;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final MessageService messageService;

    @GetMapping()
    public List<Message> getChats(@RequestParam ("receiver") UUID receiver) {
        return messageService.findChats(receiver);
    }

    @GetMapping("{id}/participants")
    public List<Customer> getParticipants(@PathVariable("id") UUID chatId) {
        List<Message> messages = messageRepository.findAllByChatAndType(chatId, Message.MessageType.hello);
        List<UUID> participants = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
        if(participants.isEmpty()) {
            return new ArrayList<>();
        } else {
            return customerRepository.findAllById(participants);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> leaveChat(@PathVariable("id") UUID chatId, Message message) {
        UUID sender = message.getSender();
        String nonce = message.getNonce();
        String data = message.getData();
        Optional<Customer> customerOpt = customerRepository.findById(sender);
        if(customerOpt.isEmpty()) {
            messageRepository.deleteMyChatMessages(sender, sender, chatId);
            return ResponseEntity.ok().build();
        }
        Customer customer = customerOpt.get();
        byte[] publicKey = Base64.getDecoder().decode(customer.getPk());

        TweetNaclFast.Box.KeyPair kp = TweetNaclFast.Box.keyPair_fromSecretKey(publicKey);
        TweetNaclFast.Box box = new TweetNaclFast.Box(publicKey, publicKey);

        byte[] decrypted = box.open(Base64.getDecoder().decode(data), Base64.getDecoder().decode(nonce));
        UUID result = UUID.fromString(new String(decrypted));

        return ResponseEntity.badRequest().build();
    }
}
