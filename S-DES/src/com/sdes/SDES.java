package com.sdes;

import java.util.Arrays;

public class SDES {
    private char[] key10bit, p10, p8, p4, ip, ipInv, ep;
    private char[][] keys; // 8bit each
    private final String[] sbox_vals = {"00","01","10","11"}; // for 2-bit substitution
    int rounds;
    // s-boxes
    private static final int[][] s0 = { {1,0,3,2},
                                        {3,2,1,0},
                                        {0,2,1,3},
                                        {3,1,3,2} };
    private static final int[][] s1 = { {0,1,2,3},
                                        {2,0,1,3},
                                        {3,0,1,0},
                                        {2,1,0,3} };
    public SDES(String  key10bit) throws Exception {
        isValidFormat(key10bit, 10, 2);
        this.key10bit  = key10bit.toCharArray();
        this.rounds = 2; // default 2
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
    private void doPermutation(char[] key, char[]  KeyNbit, char[] p, int size){
        for(int i=0;i<size;i++){
            key[i] = KeyNbit[p[i]-48];
        }
    }
    public void setPermutations(String p10, String p8, String p4, String ip, String ipInv, String ep){
            this.p10 = p10.toCharArray();
            this.p8 = p8.toCharArray();
            this.p4 = p4.toCharArray();
            this.ip = ip.toCharArray();
            this.ipInv = ipInv.toCharArray();
            this.ep = ep.toCharArray();
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
    private int getIntVal(char[] key, int base){
        return Integer.parseInt(String.valueOf(key), base);
    }
    private String computeFrom_S_box(int[][] sbox, char[] half){
        int row = Integer.parseInt(half[0]+""+half[3],2);
        int col = Integer.parseInt(half[1]+""+half[2],2);
        return sbox_vals[sbox[row][col]];
    }
    private char[] XOR(char[] bits1, char[] bits2, int size){
        char[] res = new char[size];
        for(int i=0;i<size;i++){
            char c1 =  bits1[i];
            char c2 = bits2[i];
            if(c1==c2)
                res[i] = '0';
            else
                res[i] = '1';
        }
        return res;
    }
    private char[] convertTo8bit(String ascii){
        char[] ascii_bits = new char[8];
        // check for length 8
        int i = 0;
        if(ascii.length()!=8){
            int n = ascii.length();
            for(i=0;i<8-n;i++){
                ascii_bits[i] ='0';
            }
        }
        int k = 0;
        for(;i<8;i++)
            ascii_bits[i] = ascii.charAt(k++);
        return ascii_bits;
    }
    public void setDefaultRounds(int rounds){
        if(rounds < 2){
            System.out.println("Rounds must be greater than equal to 2 !");
            System.out.println("Default Rounds "+ this.rounds);
            return;
        }
        this.rounds = rounds;
    }
    public void generateKeys(){
        char[] key_p10 = new char[10];
        char[][] key_p8 = new char[rounds][8];
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
            //  System.out.println(Arrays.toString(key_p8[i]));
            shift++;
            i++;
        }
        this.keys = key_p8;
        // System.out.println(Byte.toUnsignedInt(k1)+" "+Byte.toUnsignedInt(k2));
    }
    private char[] encryptionOneRound(char[] ascii_bits, char[] key){
        // 8bit plaintext key
        // System.out.println(Arrays.toString(ascii_bits));
        // divide into two halves
        char[] lefthalf = Arrays.copyOfRange(ascii_bits, 0, 4);
        char[] righthalf = Arrays.copyOfRange(ascii_bits, 4, 8);
        // do Expand permutation on righthalf
        char[] righthalf_ep = new char[8];
        doPermutation(righthalf_ep, righthalf, ep, 8);
        // System.out.println(Arrays.toString(righthalf_ep));
        // do XOR ith key1
        char[] xor_res_bits = XOR(key,  righthalf_ep, 8);
        // System.out.println(Arrays.toString(xor_res_bits));
        // divide into two halves
        char[] l = Arrays.copyOfRange(xor_res_bits, 0,4);
        char[] r = Arrays.copyOfRange(xor_res_bits, 4, 8);
        // apply s-boxes
        // s0 on left half and s1 on right half
        char[] combinedHalves = (computeFrom_S_box(s0, l) + computeFrom_S_box(s1, r)).toCharArray();
        // System.out.println(Arrays.toString(combinedHalves));
        // do permutation p4 on combined halves
        char[] out_p4_chars = new char[4];
        doPermutation(out_p4_chars, combinedHalves,  p4, 4);
        // System.out.println(Arrays.toString(out_p4_chars));
        // XOR with left half of IP and output of p4
        char[] res_xor_bits_2 = XOR(lefthalf, out_p4_chars,4);
        // final combine
        char[]  combine_final = new char[8];
        // System.out.println("XOR "+Arrays.toString(res_xor_bits_2));
        // System.out.println("Right half "+Arrays.toString(righthalf));
        for(int i=0;i<4;i++){
            combine_final[i+4] = righthalf[i];
            combine_final[i] = res_xor_bits_2[i];
        }
        return combine_final;
    }
    private int encryptDecryptASCII(int ascii, boolean keyReverse){
        String ascii_bits_str = Integer.toBinaryString(ascii);
        char[] ascii_bits = convertTo8bit(ascii_bits_str);
        char[] ip_out = new char[8];
        // if key reverse is true then this function will pass key in backward direction i.e will do decryption
        // else encryption
        int keyStart = keyReverse ? rounds-1:0;
        // do ip
        doPermutation(ip_out, ascii_bits, ip, 8);
        for(int i=0;i<rounds;i++){
            ip_out = encryptionOneRound(ip_out, keys[Math.abs(keyStart-i)]);
            // System.out.println(String.format("Round %d %s",i+1,Arrays.toString(ip_out)));
            if(i!=rounds-1){
                // switch left and right halves
                for(int j=0;j<4;j++){
                    char temp = ip_out[j];
                    ip_out[j] = ip_out[j+4];
                    ip_out[j+4] =  temp;
                }
            }
        }
        // do inverse permutation
        char[] encryptedBits = new char[8];
        doPermutation(encryptedBits, ip_out, ipInv, 8);
        // System.out.println("Encrypted Bits "+Arrays.toString(encryptedBits));
        return Integer.parseInt(String.valueOf(encryptedBits),2);
    }
    public String encryptText(String plaintext){
        StringBuilder cipherText = new StringBuilder();
        for(char x: plaintext.toCharArray()){
            cipherText.append((char)encryptDecryptASCII((int)x, false));
        }
        return cipherText.toString();
    }
    public String decryptText(String cipherText){
        StringBuilder plainText = new StringBuilder();
        for(char x: cipherText.toCharArray()){
            plainText.append((char)encryptDecryptASCII((int)x, true));
        }
        return plainText.toString();
    }
    public static void main(String[] args) throws Exception {
        SDES sdes = new SDES("1010000010");
        sdes.setPermutations("2416390875","52637498","1320","15203746","30246175","30121230");
        sdes.setDefaultRounds(2);
        sdes.generateKeys();
        // 0-255 Extended ascii range
        String cipherText = sdes.encryptText("Hello my name is Anurag!");
        System.out.println("Cipher Text : ");
        System.out.println(cipherText);
        System.out.println("Plain Text : " + sdes.decryptText(cipherText));
    }
}
