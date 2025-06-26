package com.dollysolutions.s600;

import java.text.SimpleDateFormat;
import java.util.Date;

public class util {

    public static String hex2Str(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            sb.append(HEX_DIGITS[(b >> 4) & 0x0F]); // convert the upper 4 bits to hexadecimal
            sb.append(HEX_DIGITS[b & 0x0F]); // convert the lower 4 bits to hexadecimal
            sb.append(' '); // add a space separator

            if ((i + 1) % 1024 == 0) { // every 1024 bytes, add a newline
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String bcdToAsc(byte[] bytes, int len) {
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            int h = (bytes[i] >> 4) & 0x0F;
            int l = bytes[i] & 0x0F;
            sb.append((char) (h + (h < 10 ? '0' : 'A' - 10)));
            sb.append((char) (l + (l < 10 ? '0' : 'A' - 10)));
        }
        return sb.toString();
    }

    public static String getCurTime() {
        // 创建日期对象，并获取当前时间
        Date currentDate = new Date();

        // 创建 SimpleDateFormat 对象，定义时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化日期对象为字符串
        String formattedDate = sdf.format(currentDate);

        return formattedDate;
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();

        for (int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }

        return result;
    }

    public static String bcd2str(byte[] bcds) {
        if (bcds == null) {
            return "";
        } else {
            char[] ascii = "0123456789abcdef".toCharArray();
            byte[] temp = new byte[bcds.length * 2];

            for (int i = 0; i < bcds.length; ++i) {
                temp[i * 2] = (byte) (bcds[i] >> 4 & 15);
                temp[i * 2 + 1] = (byte) (bcds[i] & 15);
            }

            StringBuffer res = new StringBuffer();

            for (int i = 0; i < temp.length; ++i) {
                res.append(ascii[temp[i]]);
            }

            return res.toString().toUpperCase();
        }
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }

    public static byte[] short2bytes(short num) {
        byte[] targets = new byte[2];

        for (int i = 0; i < 2; ++i) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) (num >>> offset & 255);
        }

        return targets;
    }

    public static String getBinaryStrFromByteArr(byte[] bArr) {
        String result = "";
        byte[] var5 = bArr;
        int var4 = bArr.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            result = result + getBinaryStrFromByte(b);
        }

        return result;
    }

    public static String getBinaryStrFromByte(byte b) {
        String result = "";
        byte a = b;

        for (int i = 0; i < 8; ++i) {
            byte c = a;
            a = (byte) (a >> 1);
            a = (byte) (a << 1);
            if (a == c) {
                result = "0" + result;
            } else {
                result = "1" + result;
            }

            a = (byte) (a >> 1);
        }

        return result;
    }

    public static String getBinaryStrFromByte2(byte b) {
        String result = "";
        byte a = b;

        for (int i = 0; i < 8; ++i) {
            result = a % 2 + result;
            a = (byte) (a >> 1);
        }

        return result;
    }

    public static String getBinaryStrFromByte3(byte b) {
        String result = "";
        byte a = b;

        for (int i = 0; i < 8; ++i) {
            result = a % 2 + result;
            a = (byte) (a / 2);
        }

        return result;
    }

    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];

        for (int i = 0; i < 4 && i < iArrayLen; ++i) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 255);
        }

        return bLocalArr;
    }

    public static byte[] xor(byte[] op1, byte[] op2) {
        if (op1.length != op2.length) {
            throw new IllegalArgumentException("argument error, length error");
        } else {
            byte[] result = new byte[op1.length];

            for (int i = 0; i < op1.length; ++i) {
                result[i] = (byte) (op1[i] ^ op2[i]);
            }

            return result;
        }
    }

}
