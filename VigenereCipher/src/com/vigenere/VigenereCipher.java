package com.vigenere;
class Vigenere{
    private String key;
    private String plaintext;
    public Vigenere(){key = ""; plaintext="";}
    public void  constructKey(String plaintext, String key){
        this.plaintext = plaintext.toLowerCase();
        key = key.toLowerCase();
        StringBuilder str = new StringBuilder();
        while (str.length()<=plaintext.length()){
            str.append(key);
        }
        this.key = str.substring(0, plaintext.length());
    }
    public String getKey(){ return  this.key; }
    public String getPlaintext(){ return this.plaintext; }
    public String encrypt(){
        StringBuilder enc = new StringBuilder();
        for(int i=0;i<plaintext.length();i++){
            int k = key.charAt(i);
            int e = plaintext.charAt(i);
            int x = ((e+k)%97)%26;
            enc.append((char)(x+97));
        }
        return enc.toString().toUpperCase();
    }
    public void displayKeyAscii(){
        display(this.key);
    }
    public void displayPlaintxtAscii(){
        display(this.plaintext);
    }
    private void display(String str){
        for(int x: str.toCharArray()) System.out.print(x+" ");
        System.out.println();
    }
    public String decrypt(String enc){
        StringBuilder dec = new StringBuilder();
        enc = enc.toLowerCase();
        for(int i=0;i<enc.length();i++){
            int k = key.charAt(i);
            int e = enc.charAt(i);
            int x = (e-k+26)%26+97;
            dec.append((char)(x));
        }
        return dec.toString();
    }
}
public class VigenereCipher {
    public static void main(String[] args){
        Vigenere vigenere = new Vigenere();
        vigenere.constructKey("hellomynameisanurag","whatsup");
        System.out.println("Key : "+ vigenere.getKey());
        System.out.println("Plaintext : "+ vigenere.getPlaintext());
        String enc = vigenere.encrypt();
        System.out.println("Cipher text : "+enc);
        System.out.println("Decrypted text : "+vigenere.decrypt(enc));
    }
}
