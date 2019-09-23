/********************************************************************************************************
 * @file     lighting_model.h 
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
#include "../../proj_lib/sig_mesh/app_mesh.h"

typedef struct{
	u8 mac[6];
	u16 ele_addr;
}mac_addr_set_t;


void mesh_fast_prov_start();
void mesh_fast_prov_rsp_handle(mesh_rc_rsp_t *rsp);
int mesh_fast_prov_sts_set(u8 sts_set);
void mesh_fast_prov_val_init();
int mesh_reset_network(u8 provision_enable);
void mesh_fast_prov_proc();
void mesh_fast_prov_reliable_timeout_handle();
void mesh_fast_prov_start();
int is_fast_prov_mode();
int mesh_fast_prov_sts_get();

int cb_vd_mesh_reset_network(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_get_addr(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_get_addr(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_set_addr(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_set_provision_data(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_provision_confirm(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_provision_complete(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);

int cb_vd_mesh_addr_sts(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_primary_addr_sts(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_provison_data_sts(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
int cb_vd_mesh_provision_sts(u8 *par, int par_len, mesh_cb_fun_par_t *cb_par);
u16 get_win32_prov_unicast_adr();
int set_win32_prov_unicast_adr(u16 adr);

