package com.sdes;

import java.util.Arrays;

public class SDES {
    private char[] key10bit, p10, p8;
    private byte k1, k2; // 8bit each
    public SDES(String  key10bit) throws Exception {
        isValidFormat(key10bit, 10, 2);
        this.key10bit  = key10bit.toCharArray();
    }
    private void isValidFormat(String key, int len, int base ) throws Exception{
        if(key.length()!=len)
            throw new NumberFormatException("Key length is not valid !");
        try{
            Integer.parseInt(key, base);
        }catch (Exception e){
            throw new NumberFormatException(String.format("Given key is not in base %d !", base));
        }
    }
    private void arrayLeftShiftbyOne(char[] chars, int k){
        char c = chars[0];
        int i = 0;
        for( ;i<chars.length-1;i++){
            chars[i] = chars[i+1];
        }
        chars[i] = c;
    }
    private void doPermutation(char[] key, char[]  Key10bit, char[] p, int size){
        for(int i=0;i<size;i++){
            key[i] = Key10bit[p[i]-48];
        }
    }
    private int getIntVal(char[] key, int base){
        return Integer.parseInt(String.valueOf(key), base);
    }
    public void setPermutations(String p10, String p8){
            this.p10 = p10.toCharArray();
            this.p8 = p8.toCharArray();
    }
    private char[] generateSingleKey(char[] leftHalf, char[]  rightHalf, int shift){
        char[] key_p8 = new char[8];
        // left shift by amount k (i.e shift)
        while (shift-->0) {
            arrayLeftShiftbyOne(leftHalf, shift);
            arrayLeftShiftbyOne(rightHalf, shift);
        }
        //System.out.println(Arrays.toString(leftHalf));
        //System.out.println(Arrays.toString(rightHalf));
        // combine both of them
        char[] combinedHalves = new char[10];
        for(int i=0;i<5;i++){
            combinedHalves[i] = leftHalf[i];
            combinedHalves[i+5] = rightHalf[i];
        }
        // System.out.println(Arrays.toString(combinedHalves));
        // do p8
        doPermutation(key_p8, combinedHalves, p8,  8);
        return key_p8;
    }
    public void generateKeys(){
        char[] key_p10 = new char[10];
        char[][] key_p8 = new char[2][8];
        // do p10
        doPermutation(key_p10, this.key10bit, p10, 10);
        // divide into two halves
        char[] leftHalf = Arrays.copyOfRange(key_p10, 0, 5);
        char[] rightHalf = Arrays.copyOfRange(key_p10, 5, 10);
        int rounds = 2;
        int shift = 1;
        int i = 0;
        while (rounds-->0){
            key_p8[i] = generateSingleKey(leftHalf, rightHalf, shift);
            System.out.println(Arrays.toString(key_p8[i]));
            shift++;
            i++;
        }
        k1 = (byte)getIntVal(key_p8[0],2);
        k2 = (byte)getIntVal(key_p8[1],2);
        System.out.println(Byte.toUnsignedInt(k1)+" "+Byte.toUnsignedInt(k2));
    }
    public static void main(String[] args) throws Exception {
        SDES sdes = new SDES("1010000010");
        sdes.setPermutations("2416390875","52637498");
        sdes.generateKeys();
    }
}
