package com.telink.sig.mesh.demo.model;

/**
 * Created by kee on 2019/2/27.
 */

public enum PrivateDevice {

    PANEL(0x0211, 0x07, "panel",
            new byte[]{(byte) 0x11, (byte) 0x02, (byte) 0x07, (byte) 0x00, (byte) 0x32, (byte) 0x37, (byte) 0x69, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00
                    , (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0xfe, (byte) 0x01, (byte) 0xfe, (byte) 0x02, (byte) 0xfe, (byte) 0x00, (byte) 0xff
                    , (byte) 0x01, (byte) 0xff, (byte) 0x00, (byte) 0x12, (byte) 0x01, (byte) 0x12, (byte) 0x00, (byte) 0x10, (byte) 0x03, (byte) 0x12, (byte) 0x04, (byte) 0x12, (byte) 0x06, (byte) 0x12, (byte) 0x07, (byte) 0x12
                    , (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x10, (byte) 0x03, (byte) 0x12
                    , (byte) 0x04, (byte) 0x12, (byte) 0x06, (byte) 0x12, (byte) 0x07, (byte) 0x12, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x10
                    , (byte) 0x03, (byte) 0x12, (byte) 0x04, (byte) 0x12, (byte) 0x06, (byte) 0x12, (byte) 0x07, (byte) 0x12, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00}),

    LIGHT(0x3832, 0x05, "light",
            new byte[]{(byte)0x11,(byte)0x02,(byte)0x05,(byte)0x00,(byte)0x32,(byte)0x38,(byte)0x69,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x16,(byte)0x01,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x02,(byte)0xfe,(byte)0x00,(byte)0xff,(byte)0x01,(byte)0xff
                    ,(byte)0x00,(byte)0x10,(byte)0x02,(byte)0x10,(byte)0x04,(byte)0x10,(byte)0x06,(byte)0x10,(byte)0x07,(byte)0x10,(byte)0x03,(byte)0x12,(byte)0x04,(byte)0x12,(byte)0x00,(byte)0x13
                    ,(byte)0x01,(byte)0x13,(byte)0x03,(byte)0x13,(byte)0x04,(byte)0x13,(byte)0x07,(byte)0x13,(byte)0x08,(byte)0x13,(byte)0x11,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x06,(byte)0x13,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0a,(byte)0x13,(byte)0x00,(byte)0x00
                    ,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x0b,(byte)0x13}),

    CT(0x0211, 0x01, "ct",
            new byte[]{(byte) 0x11, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x32, (byte) 0x37, (byte) 0x69, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x19, (byte) 0x01, (byte) 0x00, (byte) 0x00
                    , (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0xfe, (byte) 0x01, (byte) 0xfe, (byte) 0x02, (byte) 0xfe, (byte) 0x00, (byte) 0xff
                    , (byte) 0x01, (byte) 0xff, (byte) 0x00, (byte) 0x12, (byte) 0x01, (byte) 0x12, (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x10, (byte) 0x04, (byte) 0x10, (byte) 0x06, (byte) 0x10, (byte) 0x07, (byte) 0x10
                    , (byte) 0x03, (byte) 0x12, (byte) 0x04, (byte) 0x12, (byte) 0x06, (byte) 0x12, (byte) 0x07, (byte) 0x12, (byte) 0x00, (byte) 0x13, (byte) 0x01, (byte) 0x13, (byte) 0x03, (byte) 0x13, (byte) 0x04, (byte) 0x13
                    , (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x10, (byte) 0x06, (byte) 0x13});


    PrivateDevice(int vid, int pid, String name, byte[] cpsData) {
        this.vid = vid;
        this.pid = pid;
        this.name = name;
        this.cpsData = cpsData;
    }

    private final int vid;
    private final int pid;
    private final String name;
    private final byte[] cpsData;

    public int getVid() {
        return vid;
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public byte[] getCpsData() {
        return cpsData;
    }

    public static PrivateDevice filter(byte[] serviceData) {
        if (serviceData.length < 3) return null;
        System.out.println(parsePid(serviceData));
        int vid = (serviceData[0] & 0xFF) + (((serviceData[1] & 0xFF) << 8));
        int pid = serviceData[2] & 0xFF;
        PrivateDevice[] values = PrivateDevice.values();
        for (PrivateDevice device :
                values) {
            if (device.vid == vid && device.pid == pid) {
                return device;
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
