package com.xor;
class XOR{
    private char key;
    public XOR(){
        key = '\0';
    }
    public XOR(char key){
        this.key = key;
    }
    public String encryptDecrypt(String plaintext){
        StringBuilder enc = new StringBuilder();
        for(char x: plaintext.toCharArray()){
            enc.append((char)(x^key));
        }
        return enc.toString();
    }
}
public class XORCipher {
    public static void main(String[] args){
        String plaintext = "HELLO THERE";
        char key = 'X';
        XOR xor = new XOR(key);
        System.out.println("Plaintext : "+plaintext);
        String enc = xor.encryptDecrypt(plaintext);
        System.out.println("CipherText : "+enc);
        String dt = xor.encryptDecrypt(enc);
        System.out.println("DecryptedText : "+dt);
    }
}
