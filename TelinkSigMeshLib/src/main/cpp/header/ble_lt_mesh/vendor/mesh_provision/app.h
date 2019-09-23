/********************************************************************************************************
 * @file     app.h 
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

#pragma once

#include "../../proj/tl_common.h"
u8 gateway_cmd_from_host_ctl(u8 *p, u16 len );
u8 gateway_upload_mac_address(u8 *p_mac,u8 *p_adv);
void set_gateway_provision_sts(unsigned char en);
unsigned char get_gateway_provisison_sts();
u8 gateway_upload_provision_suc_event(u8 evt);
void set_gateway_provision_para_init();
u8 gateway_upload_keybind_event(u8 evt);
u8 gateway_upload_provision_slef_sts(u8 sts);
u8 gateway_upload_node_ele_cnt(u8 ele_cnt);
u8 gateway_upload_node_info(u16 unicast);
u8 gateway_upload_mesh_ota_sts(u8 *p_dat,int len);
u8 gateway_upload_mesh_sno_val();

u8 gateway_cmd_from_host_ota(u8 *p, u16 len );
u8 gateway_cmd_from_host_mesh_ota(u8 *p, u16 len );


