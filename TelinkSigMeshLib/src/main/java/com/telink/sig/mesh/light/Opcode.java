/********************************************************************************************************
 * @file Opcode.java
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
package com.telink.sig.mesh.light;

import android.support.annotation.Nullable;

import com.telink.sig.mesh.model.OpcodeType;

public enum Opcode {

    APPKEY_ADD(0x00, "Add Application key"),
    APPKEY_UPDATE(0x01),
    COMPOSITION_DATA_STATUS(0x02),
    CFG_MODEL_PUB_SET(0x03),
    HEALTH_CURRENT_STATUS(0x04),
    HEALTH_FAULT_STATUS(0x05),
    HEARTBEAT_PUB_STATUS(0x06),


    // config
    APPKEY_DEL(0x0080),
    APPKEY_GET(0x0180),
    APPKEY_LIST(0x0280),
    APPKEY_STATUS(0x0380),

    // attenttion timer
    HEALTH_ATTENTION_GET(0x0480),
    HEALTH_ATTENTION_SET(0x0580),
    HEALTH_ATTENTION_SET_NOACK(0x0680),
    HEALTH_ATTENTION_STATUS(0x0780),

    COMPOSITION_DATA_GET(0x0880),
    CFG_BEACON_GET(0x0980),
    CFG_BEACON_SET(0x0A80),
    CFG_BEACON_STATUS(0x0B80),
    CFG_DEFAULT_TTL_GET(0x0C80),
    CFG_DEFAULT_TTL_SET(0x0D80),
    CFG_DEFAULT_TTL_STATUS(0x0E80),
    CFG_FRIEND_GET(0x0F80),
    CFG_FRIEND_SET(0x1080),
    CFG_FRIEND_STATUS(0x1180),
    CFG_GATT_PROXY_GET(0x1280),
    CFG_GATT_PROXY_SET(0x1380),
    CFG_GATT_PROXY_STATUS(0x1480),
    CFG_KEY_REFRESH_PHASE_GET(0x1580),
    CFG_KEY_REFRESH_PHASE_SET(0x1680),
    CFG_KEY_REFRESH_PHASE_STATUS(0x1780),
    CFG_MODEL_PUB_GET(0x1880),
    CFG_MODEL_PUB_STATUS(0x1980),
    CFG_MODEL_PUB_VIRTUAL_ADR_SET(0x1A80),
    CFG_MODEL_SUB_ADD(0x1B80),
    CFG_MODEL_SUB_DEL(0x1C80),
    CFG_MODEL_SUB_DEL_ALL(0x1D80),
    CFG_MODEL_SUB_OVER_WRITE(0x1E80),
    CFG_MODEL_SUB_STATUS(0x1F80),
    CFG_MODEL_SUB_VIRTUAL_ADR_ADD(0x2080),
    CFG_MODEL_SUB_VIRTUAL_ADR_DEL(0x2180),
    CFG_MODEL_SUB_VIRTUAL_ADR_OVER_WRITE(0x2280),
    CFG_NW_TRANSMIT_GET(0x2380),
    CFG_NW_TRANSMIT_SET(0x2480),
    CFG_NW_TRANSMIT_STATUS(0x2580),
    CFG_RELAY_GET(0x2680),
    CFG_RELAY_SET(0x2780),
    CFG_RELAY_STATUS(0x2880),
    CFG_SIG_MODEL_SUB_GET(0x2980),
    CFG_SIG_MODEL_SUB_LIST(0x2A80),
    CFG_VENDOR_MODEL_SUB_GET(0x2B80),
    CFG_VENDOR_MODEL_SUB_LIST(0x2C80),
    CFG_LPN_POLL_TIMEOUT_GET(0x2D80),
    CFG_LPN_POLL_TIMEOUT_STATUS(0x2E80),

    HEALTH_FAULT_CLEAR(0x2F80),
    HEALTH_FAULT_CLEAR_NOACK(0x3080),
    HEALTH_FAULT_GET(0x3180),
    HEALTH_FAULT_TEST(0x3280),
    HEALTH_FAULT_TEST_NOACK(0x3380),

    HEALTH_PERIOD_GET(0x3480),
    HEALTH_PERIOD_SET(0x3580),
    HEALTH_PERIOD_SET_NOACK(0x3680),
    HEALTH_PERIOD_STATUS(0x3780),

    HEARTBEAT_PUB_GET(0x3880),
    HEARTBEAT_PUB_SET(0x3980),
    HEARTBEAT_SUB_GET(0x3A80),
    HEARTBEAT_SUB_SET(0x3B80),
    HEARTBEAT_SUB_STATUS(0x3C80),

    MODE_APP_BIND(0x3D80),
    MODE_APP_STATUS(0x3E80),
    MODE_APP_UNBIND(0x3F80),
    NETKEY_ADD(0x4080),
    NETKEY_DEL(0x4180),
    NETKEY_GET(0x4280),
    NETKEY_LIST(0x4380),
    NETKEY_STATUS(0x4480),
    NETKEY_UPDATE(0x4580),
    NODE_ID_GET(0x4680),
    NODE_ID_SET(0x4780),
    NODE_ID_STATUS(0x4880),
    NODE_RESET(0x4980),
    NODE_RESET_STATUS(0x4A80),
    SIG_MODEL_APP_GET(0x4B80),
    SIG_MODEL_APP_LIST(0x4C80),
    VENDOR_MODEL_APP_GET(0x4D80),
    VENDOR_MODEL_APP_LIST(0x4E80),

    // generic
    G_ONOFF_GET(0x0182),
    G_ONOFF_SET(0x0282),
    G_ONOFF_SET_NOACK(0x0382),
    G_ONOFF_STATUS(0x0482),

    G_LEVEL_GET(0x0582),
    G_LEVEL_SET(0x0682),
    G_LEVEL_SET_NOACK(0x0782),
    G_LEVEL_STATUS(0x0882),
    G_DELTA_SET(0x0982),
    G_DELTA_SET_NOACK(0x0A82),
    G_MOVE_SET(0x0B82),
    G_MOVE_SET_NOACK(0x0C82),

    G_DEF_TRANS_TIME_GET(0x0D82),
    G_DEF_TRANS_TIME_SET(0x0E82),
    G_DEF_TRANS_TIME_SET_NOACK(0x0F82),
    G_DEF_TRANS_TIME_STATUS(0x1082),

    G_ON_POWER_UP_GET(0x1182),
    G_ON_POWER_UP_STATUS(0x1282),
    G_ON_POWER_UP_SET(0x1382),
    G_ON_POWER_UP_SET_NOACK(0x1482),

    G_POWER_LEVEL_GET(0x1582),
    G_POWER_LEVEL_SET(0x1682),
    G_POWER_LEVEL_SET_NOACK(0x1782),
    G_POWER_LEVEL_STATUS(0x1882),
    G_POWER_LEVEL_LAST_GET(0x1982),
    G_POWER_LEVEL_LAST_STATUS(0x1A82),
    G_POWER_DEF_GET(0x1B82),
    G_POWER_DEF_STATUS(0x1C82),
    G_POWER_LEVEL_RANGE_GET(0x1D82),
    G_POWER_LEVEL_RANGE_STATUS(0x1E82),
    G_POWER_DEF_SET(0x1F82),
    G_POWER_DEF_SET_NOACK(0x2082),
    G_POWER_LEVEL_RANGE_SET(0x2182),
    G_POWER_LEVEL_RANGE_SET_NOACK(0x2282),

    G_BATTERY_GET(0x2382),
    G_BATTERY_STATUS(0x2482),

    G_LOCATION_GLOBAL_GET(0x2582),
    G_LOCATION_GLOBAL_STATUS(0x40),
    G_LOCATION_LOCAL_GET(0x2682),
    G_LOCATION_LOCAL_STATUS(0x2782),
    G_LOCATION_GLOBAL_SET(0x41),
    G_LOCATION_GLOBAL_SET_NOACK(0x42),
    G_LOCATION_LOCAL_SET(0x2882),
    G_LOCATION_LOCAL_SET_NOACK(0x2982),

    // lighting
    LIGHTNESS_GET(0x4B82),
    LIGHTNESS_SET(0x4C82),
    LIGHTNESS_SET_NOACK(0x4D82),
    LIGHTNESS_STATUS(0x4E82),
    LIGHTNESS_LINEAR_GET(0x4F82),
    LIGHTNESS_LINEAR_SET(0x5082),
    LIGHTNESS_LINEAR_SET_NOACK(0x5182),
    LIGHTNESS_LINEAR_STATUS(0x5282),
    LIGHTNESS_LAST_GET(0x5382),
    LIGHTNESS_LAST_STATUS(0x5482),
    LIGHTNESS_DEFULT_GET(0x5582),
    LIGHTNESS_DEFULT_STATUS(0x5682),
    LIGHTNESS_RANGE_GET(0x5782),
    LIGHTNESS_RANGE_STATUS(0x5882),
    LIGHTNESS_DEFULT_SET(0x5982),
    LIGHTNESS_DEFULT_SET_NOACK(0x5A82),
    LIGHTNESS_RANGE_SET(0x5B82),
    LIGHTNESS_RANGE_SET_NOACK(0x5C82),
    LIGHT_CTL_GET(0x5D82),
    LIGHT_CTL_SET(0x5E82),
    LIGHT_CTL_SET_NOACK(0x5F82),
    LIGHT_CTL_STATUS(0x6082),
    LIGHT_CTL_TEMP_GET(0x6182),
    LIGHT_CTL_TEMP_RANGE_GET(0x6282),
    LIGHT_CTL_TEMP_RANGE_STATUS(0x6382),
    LIGHT_CTL_TEMP_SET(0x6482),
    LIGHT_CTL_TEMP_SET_NOACK(0x6582),
    LIGHT_CTL_TEMP_STATUS(0x6682),
    LIGHT_CTL_DEFULT_GET(0x6782),
    LIGHT_CTL_DEFULT_STATUS(0x6882),
    LIGHT_CTL_DEFULT_SET(0x6982),
    LIGHT_CTL_DEFULT_SET_NOACK(0x6A82),
    LIGHT_CTL_TEMP_RANGE_SET(0x6B82),
    LIGHT_CTL_TEMP_RANGE_SET_NOACK(0x6C82),

    // HSL
    LIGHT_HSL_GET(0x6D82),
    LIGHT_HSL_HUE_GET(0x6E82),
    LIGHT_HSL_HUE_SET(0x6F82),
    LIGHT_HSL_HUE_SET_NOACK(0x7082),
    LIGHT_HSL_HUE_STATUS(0x7182),
    LIGHT_HSL_SAT_GET(0x7282),
    LIGHT_HSL_SAT_SET(0x7382),
    LIGHT_HSL_SAT_SET_NOACK(0x7482),
    LIGHT_HSL_SAT_STATUS(0x7582),
    LIGHT_HSL_SET(0x7682),
    LIGHT_HSL_SET_NOACK(0x7782),
    LIGHT_HSL_STATUS(0x7882),
    LIGHT_HSL_TARGET_GET(0x7982),
    LIGHT_HSL_TARGET_STATUS(0x7A82),
    LIGHT_HSL_DEF_GET(0x7B82),
    LIGHT_HSL_DEF_STATUS(0x7C82),
    LIGHT_HSL_RANGE_GET(0x7D82),
    LIGHT_HSL_RANGE_STATUS(0x7E82),
    LIGHT_HSL_DEF_SET(0x7F82),
    LIGHT_HSL_DEF_SET_NOACK(0x8082),
    LIGHT_HSL_RANGE_SET(0x8182),
    LIGHT_HSL_RANGE_SET_NOACK(0x8282),

    // time
    TIME_GET(0x3782),
    TIME_SET(0x5C),
    TIME_STATUS(0x5D),
    TIME_ROLE_GET(0x3882),
    TIME_ROLE_SET(0x3982),
    TIME_ROLE_STATUS(0x3A82),
    TIME_ZONE_GET(0x3B82),
    TIME_ZONE_SET(0x3C82),
    TIME_ZONE_STATUS(0x3D82),
    TAI_UTC_DELTA_GET(0x3E82),
    TAI_UTC_DELTA_SET(0x3F82),
    TAI_UTC_DELTA_STATUS(0x4082),

    // scheduler
    SCHD_ACTION_GET(0x4882),
    SCHD_ACTION_STATUS(0x5F),
    SCHD_GET(0x4982),
    SCHD_STATUS(0x4A82),
    SCHD_ACTION_SET(0x60),
    SCHD_ACTION_SET_NOACK(0x61),

    // scene
    SCENE_GET(0x4182),
    SCENE_RECALL(0x4282),
    SCENE_RECALL_NOACK(0x4382),
    SCENE_STATUS(0x5E),
    SCENE_REG_GET(0x4482),
    SCENE_REG_STATUS(0x4582),
    SCENE_STORE(0x4682),
    SCENE_STORE_NOACK(0x4782),
    SCENE_DEL(0x9E82),
    SCENE_DEL_NOACK(0x9F82),

    // remote provision
    REMOTE_PROV_SCAN_CAPA_GET(0x4F80),
    REMOTE_PROV_SCAN_CAPA_STS(0x5080),
    REMOTE_PROV_SCAN_GET(0x5180),
    REMOTE_PROV_SCAN_START(0x5280),
    REMOTE_PROV_SCAN_STOP(0x5380),
    REMOTE_PROV_SCAN_STS(0x5480),
    REMOTE_PROV_SCAN_REPORT(0x5580),
    REMOTE_PROV_EXTEND_SCAN_START(0x5680),
    REMOTE_PROV_EXTEND_SCAN_REPORT(0x5780),
    REMOTE_PROV_LINK_GET(0x5880),
    REMOTE_PROV_LINK_OPEN(0x5980),
    REMOTE_PROV_LINK_CLOSE(0x5A80),
    REMOTE_PROV_LINK_STS(0x5B80),
    REMOTE_PROV_LINK_REPORT(0x5C80),
    REMOTE_PROV_PDU_SEND(0x5D80),
    REMOTE_PROV_PDU_OUTBOUND_REPORT(0x5E80),
    REMOTE_PROV_PDU_REPORT(0x5F80),

    G_INFO_GET(0x01B6),
    S_INFO_GET(0x02B6);

    private final int value;
    private final String info;

    /**
     * if command is reliable
     * -1 unknown default
     * 0 unreliable
     * 1 reliable
     */
    private final int reliable;

    Opcode(int value) {
        this.value = value;
        this.info = "";
        this.reliable = -1;
    }

    Opcode(int value, @Nullable String info) {
        this.value = value;
        this.info = info;
        this.reliable = -1;
    }

    Opcode(int value, String info, int reliable) {
        this.value = value;
        this.info = info;
        this.reliable = reliable;
    }

    public static Opcode valueOf(int value) {
        for (Opcode op :
                values()) {
            if (op.getValue() == value) {
                return op;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public int getReliable() {
        return reliable;
    }

    public String getInfo() {
        return info;
    }




}
