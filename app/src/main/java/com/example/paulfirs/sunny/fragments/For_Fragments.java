package com.example.paulfirs.sunny.fragments;

public class For_Fragments {

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x ", b));
        return sb.toString();
    }

    public static byte CRC8(byte[] word) {
        byte crc = 0;
        byte flag = 0;
        byte data = 0;

        for(int i = 0; i < (word.length - 1); i++ ) {
            data = word[i];
            for (int j = 0; j < 8; j++) {
                flag = (byte) ((crc & 0xFF) ^ (data & 0xFF));
                flag = (byte) ((flag & 0xFF) & 0x01);
                crc = (byte) ((crc & 0xFF) >>> 1); // если убрать 0xFF все ломается
                data = (byte) ((data & 0xFF) >>> 1);
                if (flag != 0)
                    crc = (byte)((crc & 0xFF) ^ 0x8C);
            }

            //Log.d(TAG, "CRC8/Maxim: " + crc);
        }
        return crc;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = 0;
        try {
            aLen = a.length;
        }catch (NullPointerException ignored){}
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        if(aLen != 0)
            System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static void reconnect() {

    }
}
