package com.telink.sig.mesh.demo.model;

/**
 * Created by kee on 2019/2/27.
 */

public enum PrivateDevice2 {



    LIGHT("00010204000901", "downLight",
            new byte[]{(byte)0x11,(byte)0x02,(byte)0x05,(byte)0x00,(byte)0x32,(byte)0x38,(byte)0x69,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x16,(byte)0x01,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x02,(byte)0xfe,(byte)0x00,(byte)0xff,(byte)0x01,(byte)0xff
                    ,(byte)0x00,(byte)0x10,(byte)0x02,(byte)0x10,(byte)0x04,(byte)0x10,(byte)0x06,(byte)0x10,(byte)0x07,(byte)0x10,(byte)0x03,(byte)0x12,(byte)0x04,(byte)0x12,(byte)0x00,(byte)0x13
                    ,(byte)0x01,(byte)0x13,(byte)0x03,(byte)0x13,(byte)0x04,(byte)0x13,(byte)0x07,(byte)0x13,(byte)0x08,(byte)0x13,(byte)0x11,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x06,(byte)0x13,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0a,(byte)0x13,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0b,(byte)0x13});




    PrivateDevice2(String pid, String name, byte[] cpsData) {
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

    public static PrivateDevice2 filter(byte[] serviceData) {
        if (serviceData.length < 3) return null;
        String pid = parsePid(serviceData);
        PrivateDevice2[] values = PrivateDevice2.values();
        for (PrivateDevice2 value : values) {
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
        pid = CheckUtil.checkStringDigitHead(Util.algorismToHEXString(brand),4) +
                CheckUtil.checkStringDigitHead(Util.algorismToHEXString(wireless),2) +
                CheckUtil.checkStringDigitHead(Util.algorismToHEXString(type),2) +
                CheckUtil.checkStringDigitHead(Util.algorismToHEXString(typeSub),4) +
                CheckUtil.checkStringDigitHead(Util.algorismToHEXString(version),2) ;
        return pid;
    }
}
