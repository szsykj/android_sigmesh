package com.telink.sig.mesh.demo.ui;



/**
 * Created by kee on 2019/2/27.
 */

public enum PrivateNvcDevice {



    LIGHT("00010204000901", "downLight",
            new byte[]{(byte)0x11,(byte)0x02,(byte)0x05,(byte)0x00,(byte)0x32,(byte)0x38,(byte)0x69,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x16,(byte)0x01,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x02,(byte)0xfe,(byte)0x00,(byte)0xff,(byte)0x01,(byte)0xff
                    ,(byte)0x00,(byte)0x10,(byte)0x02,(byte)0x10,(byte)0x04,(byte)0x10,(byte)0x06,(byte)0x10,(byte)0x07,(byte)0x10,(byte)0x03,(byte)0x12,(byte)0x04,(byte)0x12,(byte)0x00,(byte)0x13
                    ,(byte)0x01,(byte)0x13,(byte)0x03,(byte)0x13,(byte)0x04,(byte)0x13,(byte)0x07,(byte)0x13,(byte)0x08,(byte)0x13,(byte)0x11,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x06,(byte)0x13,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0a,(byte)0x13,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0b,(byte)0x13}),

    LIGHT_TEST("00010204000901", "downLight",
                  new byte[]{(byte)0x11,(byte)0x02,(byte)0x05,(byte)0x00,(byte)0x32,(byte)0x39,(byte)0x69,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x19,(byte)0x01,(byte)0x00,(byte)0x00
                          ,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x00,(byte)0xff,(byte)0x01,(byte)0xff,(byte)0x00,(byte)0x12
                          ,(byte)0x01,(byte)0x12,(byte)0x00,(byte)0x10,(byte)0x02,(byte)0x10,(byte)0x04,(byte)0x10,(byte)0x06,(byte)0x10,(byte)0x07,(byte)0x10,(byte)0x03,(byte)0x12,(byte)0x04,(byte)0x12
                          ,(byte)0x06,(byte)0x12,(byte)0x07,(byte)0x12,(byte)0x00,(byte)0x13,(byte)0x01,(byte)0x13,(byte)0x03,(byte)0x13,(byte)0x04,(byte)0x13,(byte)0x07,(byte)0x13,(byte)0x08,(byte)0x13
                          ,(byte)0x11,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x06,(byte)0x13,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00
                          ,(byte)0x02,(byte)0x10,(byte)0x0a,(byte)0x13,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0b,(byte)0x13});


    PrivateNvcDevice(String pid, String name, byte[] cpsData) {
        this.pid = pid;
        this.name = name;
        this.cpsData = cpsData;
    }

    private final String pid;
    private final String name;
    private final byte[] cpsData;

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public byte[] getCpsData() {
        return cpsData;
    }

    public static PrivateNvcDevice filter(byte[] serviceData) {
        if (serviceData.length < 3) return null;
        String pid = parsePid(serviceData);
        PrivateNvcDevice[] values = PrivateNvcDevice.values();
        for (PrivateNvcDevice value : values) {
            if (value.pid.equals(pid)) {
                return value;
            }
        }
        return null;

    }

    public static String parsePid(byte[] record){
        String pid = "";
        int brand = record[1]&0xff | ((record[2]<< 8)&0xff00);
        int wireless = 2;
        int type = record[3]&0xff;
        int typeSub = record[4] | ((record[5] << 8)&0xff00);
        int version = record[6] & 0xff;
        pid = checkStringDigitHead(algorismToHEXString(brand),4) +
                checkStringDigitHead(algorismToHEXString(wireless),2) +
                checkStringDigitHead(algorismToHEXString(type),2) +
                checkStringDigitHead(algorismToHEXString(typeSub),4) +
                checkStringDigitHead(algorismToHEXString(version),2) ;
        return pid;
    }

    public static String checkStringDigitHead(String str,int digit){
        if (str != null) {
            if (str.length() < digit) {
                StringBuilder builder = new StringBuilder(str);
                for (int i = 0; i < digit-str.length(); i++) {
                    builder.insert(0,0);
                }
                return builder.toString();
            }else if (str.length() > digit){
                return str.substring(0,digit);
            }
        }
        return str;
    }

    public static String algorismToHEXString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;

        }
        result = result.toUpperCase();

        return result;
    }
}
