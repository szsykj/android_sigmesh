/********************************************************************************************************
 * @file     OtaParameters.java 
 *
 * @brief    for TLSR chips
 *
 * @author	 telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
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
package com.telink.sig.mesh.light.parameter;

/**
 *
 * Created by kee on 2017/11/23.
 */

public class OtaParameters extends Parameters {

    public static OtaParameters getDefault(String[] targets, byte[] firmware) {
        OtaParameters parameters =  new OtaParameters();
        parameters.setTargets(targets);
        parameters.setFirmware(firmware);
        return parameters;
    }

    public void setTargets(String[] mac) {
        this.set(ACTION_OTA_LIST, mac);
    }

    public void setFirmware(byte[] firmware) {
        this.set(ACTION_OTA_FIRMWARE, firmware);
    }

}
