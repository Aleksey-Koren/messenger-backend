package com.example.whisper.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.whisper.entity.Bot;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.service.BotService;
import com.example.whisper.service.ChatService;
import com.example.whisper.service.CustomerService;
import com.example.whisper.service.MessageService;
import com.example.whisper.service.impl.ChatServiceImpl;
import com.example.whisper.service.util.DecoderUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bots")
@EnableBinding(Processor.class)
@Slf4j
public record BotController(
    CustomerService customerServie, 
    BotService botService, 
    ChatServiceImpl chatService,
    DecoderUtil decoderUtil,
    MessageService messageService) {

    private static final String BOT_APPLICATION_URL = "http://localhost:8081/";

    @PostMapping
    public Message getMessage(Message message) {
        log.info("Yuhuuuu, I've received message: " + message);
        return message;
    }

    @StreamListener(target = Processor.INPUT)
	public void respond(Message message) {
        HttpEntity<Message> request = new HttpEntity<>(message);
        RestTemplate restTemplate = new RestTemplate();
        String botUrl = BOT_APPLICATION_URL + "bot/messages";
        restTemplate.postForEntity(botUrl, request, Message.class, "");
	}
    // @GetMapping 
    // ResponseEntity<String> getMessage() {
    //     String message = "Some message";
    //     return ResponseEntity.ok(message);
    // }

    // @PostMapping
    // public ResponseEntity<String> addMessageToQueue(@Payload String message) {
    //     message = "Some new message";
    //     this.gateway.broadcastMessage(message);
    //     return ResponseEntity.ok(message);
    // }

    // @GetMapping("/all")
    // public List<Customer> findAll() {
    //     List<Customer> allCustomers = Collections.emptyList();
    //     try {
    //         Customer customer = new Customer();
    //         customer.setId(UUID.randomUUID());
    //         customer.setPk("sdf342");
    //         customerServie.register(customer);
    
    //         Chat chat = new Chat(UUID.randomUUID(), customer.getId(), null);
    //         chatService.create(chat);
    
            // Bot bot = new Bot();
            // bot.setId(UUID.randomUUID());
            // bot.setPk("sdfsfsdfd");
            // botService.register(bot);
            // chatService.addCustomerToChat(bot.getId(), chat.getId());
    
    
    //         allCustomers = chatService.findChatBots(chat.getId());
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //     }

    //     return allCustomers;
    // }

    @GetMapping
    public String decryptThisFuckingMessage() {
        UUID id = UUID.fromString("44dc2c94-754d-4f31-b93c-fc5f570be2f5");
        Message message = messageService.findById(id);
        String messageText = message.getData();
        String nonce = message.getNonce();
        String publicKeyPem = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg7jPIliJov3ky5yB5iH3
            vXQwzzQy7eF6NsYSzi3aWVkzUo5622O2MOpBE/nHks/kin7gqRJCoxDXTCpi9nie
            630p+MxnHp0JIqIkeTUjsjFVgODdUwu8GZvkDEgjKUWWDKTQUTH5qxZwLBYICjd4
            5WUnPBfv1L3gk1FbnilTRHMwSCOl/1do8FdgFIMm7TiLPrjZpbuP9quh6f+8GrzR
            dPW3ql1RTvOM296X18KdC37d713UB6adNZESaPJk2UFtx0hm5hThQBhdU8R5roZU
            8aXTXqQcmrSVWCGUfQz7eY3nvfskUa2AtcJcWQx8fPD+J7CfbVr0uM20Dd/5k5/O
            /wIDAQAB
            -----END PUBLIC KEY-----
                """;
        
        String privateKeyPem = """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEpAIBAAKCAQEAg7jPIliJov3ky5yB5iH3vXQwzzQy7eF6NsYSzi3aWVkzUo56
            22O2MOpBE/nHks/kin7gqRJCoxDXTCpi9nie630p+MxnHp0JIqIkeTUjsjFVgODd
            Uwu8GZvkDEgjKUWWDKTQUTH5qxZwLBYICjd45WUnPBfv1L3gk1FbnilTRHMwSCOl
            /1do8FdgFIMm7TiLPrjZpbuP9quh6f+8GrzRdPW3ql1RTvOM296X18KdC37d713U
            B6adNZESaPJk2UFtx0hm5hThQBhdU8R5roZU8aXTXqQcmrSVWCGUfQz7eY3nvfsk
            Ua2AtcJcWQx8fPD+J7CfbVr0uM20Dd/5k5/O/wIDAQABAoIBAA/T7jffUbKfNZXt
            2YIQxNckFhVf3VrOREQ+qtXRlrIz0MHz0vl30tWv4GAJHJ0clAa8VjyasB0uEeDa
            Gwgo2FfNs3RtA1sfXZCm0SErbH2mBfM1kgK/nzuois+a/3afIPel+k43ZhvT12jE
            wPSRU9qkvAExkyUKJ+kr1ol7x8CqtnT70rwtYboAlDxaJsgkLSIRl73F3nvmhxyL
            3uN2KbGMJfKCK1TuIUFr6mVs9E1pD019M4461mSNV0WZK/GHuDyg7RBLTwJBCfdH
            AOM7u7qRSCNi7es2iVDIzCj3ivxKUxz3zACCi4wR6WRiTobCN4KiokWY44Ipp4Vs
            JPPbLwECgYEAxxL41rKGOyV0NzpWnBcZ46xL0BxnpPuC9B8n+9TxNJykLgb16pcJ
            SCldAUxPBH8yWMp/bpl5mWALNklLgtYhPEm9YRrpkS32Fj3HTSCTxTn2Fn+Fh+ce
            ztpe9mGo5VKHZ6BlBfgjMZ9gunSeWUY6eL/ZwHWwQIF1E0lXcpy4XH8CgYEAqWNi
            aFCd4m04179G/9r/TtmP7KhSDny8MxKOTS8Jw0de6jKx8neAe0KbuvmcC2tS90tG
            178sPz70bNGFWX0S8AplB+ZEygW23UMkuxgWB16U9fdeQQV9gdtIKXnmtHi1L1s5
            cHzC1qAilaCQPcVExGDYbzKZNhVvZNIRRqGZTYECgYAY5JtvJWW5kaZlnYrk+9KF
            8K6OOG8RZ03pvteeGvOKc6foBYDMs9Q6TMnPdr5OvqUKC1BCATj+X88nG6UwmBwR
            0Nq4gMgCGnAyOclpMJCp0eSezZVh+BmXHiFPx8h53IcIAzt+specBeTvi9OdMDXx
            kXPmKBFBD68XGU2xDD3VewKBgQCTlEZp0kssurJ6rmlig2BLR5xwuVn8y0qBSNp4
            ftIHtIgKji6hrOJhAfCCn/UHsfMwCALp9+LKG8SDhqyjAijcwIGztzkZto7TCivX
            Yi3XL92iy2nwRqNJ305o7I9Hj590aguKaPisXAqlyKXDW7sFScdoYnJAP7603Z01
            p5fUgQKBgQC6tm5gb8/I/C+raK1VQQAT5MgYMi9eWwo7eZ4f0y5GEbjtOX5fYGnc
            p8xP0CiQ2n0qf06NFbQ4hlNL2Oqh27FkogjJ37vbqB++3XgjiYeEz/dPK8YrSuGb
            M4UpebhpMl2YH9SGsRTFZolQpR/rlg7dQxo1htLzfjdHG/0/cKCT8g==
            -----END RSA PRIVATE KEY-----            
                """;
        String decryptedText = decoderUtil.decrypt(messageText, publicKeyPem, privateKeyPem, nonce);
        log.info(decryptedText);
        return null;
    }
}
