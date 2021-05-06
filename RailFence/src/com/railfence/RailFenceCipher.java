package com.railfence;
class RailFence{
    private int key;
    public RailFence(int key) {
        this.key = key;

    }
    public String encrypt(String plaintext){
        if(this.key==1)
            return plaintext;
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
    public String decrypt(String cipherText){
        StringBuilder plaintext = new StringBuilder();
        int j = 0;
        int row = this.key;
        int col = cipherText.length();
        boolean down = false;
        char[][]  mat = new char[row][col];
        for(int i=0;i<col;i++){
            if(j==0 || j==this.key-1){
                down = !down;
            }
            mat[j][i] = '*';
            if(down)
                j++;
            else
                j--;
        }
        int index = 0;
        // replace '*' with ciphertext alphabets row wise
        for(int i=0;i<row;i++){
            for(int k=0;k<col;k++){
                if(mat[i][k]=='*' && index<col)
                    mat[i][k] = cipherText.charAt(index++);
            }
        }
        // printing key matrix
//        for(int i=0;i<row;i++){
//            for(int k=0;k<col;k++){
//                System.out.print(mat[i][k]+" ");
//            }
//            System.out.println();
//        }
        // build the final string
        j=0;
        down = false;
        for(int i=0;i<col;i++){
            if(j==0 || j==this.key-1){
                down = !down;
            }
            plaintext.append(mat[j][i]);
            if(down)
                j++;
            else
                j--;
        }
        return plaintext.toString();
    }
}
public class RailFenceCipher {
    public static void main(String[] args) {
        RailFence rf = new RailFence(4);
        String enc = rf.encrypt("hello my name is Anurag!! :)");
        System.out.println("Encrypted Text: "+enc);
        System.out.println("Decrypyed Text: "+rf.decrypt(enc));
    }
}
