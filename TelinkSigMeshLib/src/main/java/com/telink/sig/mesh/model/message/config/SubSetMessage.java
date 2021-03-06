/********************************************************************************************************
 * @file Group.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2010
 *
 * @par Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 *           All rights reserved.
 *
 *			 The information contained herein is confidential and proprietary property of Telink
 * 		     Semiconductor (Shanghai) Co., Ltd. and is available under the terms
 *			 of Commercial License Agreement between Telink Semiconductor (Shanghai)
 *			 Co., Ltd. and the licensee in separate contract or the terms described here-in.
 *           This heading MUST NOT be removed from this file.
 *
 * 			 Licensees are granted free, non-transferable use of the information in this
 *			 file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided.
 *
 *******************************************************************************************************/
package com.telink.sig.mesh.model.message.config;

import com.telink.sig.mesh.model.message.ModelMessage;

/**
 * include add & delete
 * Created by kee on 2019/3/4.
 */
public class SubSetMessage implements ModelMessage {

    /**
     * element address
     * 2 bytes
     */
    public int elementAddress;

    /**
     * sub address
     */
    public int address;

    /**
     * sig model id
     * 2 bytes
     */
    public int modelId;

    public boolean sig = true;

    public static SubSetMessage createInstance(int elementAddress, int address, int modelId, boolean sig) {
        SubSetMessage instance = new SubSetMessage();
        instance.elementAddress = elementAddress;
        instance.address = address;
        instance.modelId = modelId;
        instance.sig = sig;
        return instance;
    }

    @Override
    public byte[] toBytes() {
        return sig ?
                new byte[]{
                        (byte) (this.elementAddress), (byte) ((this.elementAddress >> 8)),
                        (byte) (this.address ), (byte) ((this.address >> 8) ),
                        (byte) (this.modelId ), (byte) ((this.modelId >> 8) ),
                }
                :
                new byte[]{
                        (byte) (this.elementAddress ), (byte) ((this.elementAddress >> 8) ),
                        (byte) (this.address ), (byte) ((this.address >> 8) ),
                        (byte) (this.modelId ), (byte) ((this.modelId >> 8) ),
                        (byte) ((this.modelId >> 16) ),
                        (byte) ((this.modelId >> 24) )
                };
    }
}
