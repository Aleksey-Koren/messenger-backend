package com.example.whisper.service;

public interface CryptService {

    String decrypt(byte[] input, byte[] publicKeyToVerify, byte[] nonce, byte[] secretKeyToDecrypt);
}
