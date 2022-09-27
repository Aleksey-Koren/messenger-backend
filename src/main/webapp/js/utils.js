function uint8ToString(buffer) {
    return new TextDecoder().decode(buffer)
}

function stringToUint8(buffer) {
    return new TextEncoder().encode(buffer);
}

/**
 * @param message string
 * @param publicKeyToEncrypt Uint8Array
 * @param privateKeyToSign Uint8Array
 * @param nonce? Uint8Array|undefined
 * @return {{data: string, nonce: string}}
 */
function encrypt(message, publicKeyToEncrypt, privateKeyToSign, nonce) {
    var data = stringToUint8(message);
    if (!nonce) {
        nonce = new Uint8Array(24);
        self.crypto.getRandomValues(nonce)
    }
    return {
        data: Base64.fromByteArray(nacl.box(data, nonce, publicKeyToEncrypt, privateKeyToSign)),
        nonce: Base64.fromByteArray(nonce)
    };
}

/**
 * @param data string
 * @param secretKeyToDecrypt Uint8Array
 * @param publicKeyToVerify Uint8Array
 * @param nonce string
 * @return Uint8Array
 */
function decrypt(data, secretKeyToDecrypt, publicKeyToVerify, nonce) {
    return nacl.box.open(Base64.toByteArray(data), Base64.toByteArray(nonce), publicKeyToVerify, secretKeyToDecrypt);
}
