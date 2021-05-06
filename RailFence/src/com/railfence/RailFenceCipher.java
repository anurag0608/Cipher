package com.railfence;
class RailFence{
    private int key;
    public RailFence(int key) {
        this.key = key;

    }
    public String encrypt(String plaintext){
        // allocating memory
        StringBuilder[] railFence = new StringBuilder[this.key];
        for (int i = 0; i < this.key; i++)
            railFence[i] = new StringBuilder();
        int j = 0; // pointing to plain text
        int i = 0; // pointing to rail fence matrix
        boolean down = false;
        int start = -1;
        while (j<plaintext.length()){
            if(i==0){
                down = true;
                start = j;
            }
            if(down){
                railFence[i].append(plaintext.charAt(j));
                i++;
                if(j-start==this.key-1){
                    down = false;
                    i = railFence.length-2;
                }
            }else {
                railFence[i].append(plaintext.charAt(j));
                i--;
            }
            j++;
        }
        StringBuilder enc = new StringBuilder();
        for (i=0;i<railFence.length;i++){
            enc.append(railFence[i].toString());
        }
        return enc.toString();
    }
}
public class RailFenceCipher {
    public static void main(String[] args) {
        RailFence rf = new RailFence(2);
        String enc = rf.encrypt("my name is Anurag!");
        System.out.println(enc);
    }
}
