#ifndef MIJIA_BLE_LIBS_CRYPTOGRAPHY_MI_MESH_OTP_H_
#define MIJIA_BLE_LIBS_CRYPTOGRAPHY_MI_MESH_OTP_H_
#include <stdbool.h>
#include <stdint.h>
#include "mi_mesh_otp_config.h"
typedef enum {
    MARK_DIRTY          = 0x00,
    OTP_DEV_CERT        = 0x01,
    OTP_MANU_CERT       = 0x02,
    OTP_ROOT_CERT       = 0x03,
    OTP_DEV_CERT_PRI    = 0x04,
    OTP_BLE_MAC         = 0x10,
    OTP_META_DATA       = 0xF1,
    OTP_FREE_ITEM       = 0xFFFF,
} mi_otp_item_type_t;	

typedef int (*uart_sync_tx_t)(uint8_t const * const out, uint16_t olen);
typedef int (*uart_sync_rx_t)(uint8_t * const in, uint16_t max_ilen);
typedef void (*dbg_flash_lock_func_t)(void);
int cpy_root_cert(uint8_t *p_root_cert,uint16_t root_cert_size);

int mi_mesh_otp_seal_tag(uint32_t * base);
bool mi_mesh_otp_is_existed(void);
int mi_mesh_otp_manufacture_init(uart_sync_tx_t tx, uart_sync_rx_t rx,
        dbg_flash_lock_func_t flash, dbg_flash_lock_func_t debug);
int mi_mesh_otp_write(mi_otp_item_type_t item, const uint8_t *p_in, uint16_t in_len);
int mi_mesh_otp_read(mi_otp_item_type_t item, uint8_t *p_out, uint16_t len);
int mi_mesh_otp_program(void);
int mi_mesh_otp_program_simulation(void);
int mi_mesh_otp_verify(void);

#endif /* MIJIA_BLE_LIBS_CRYPTOGRAPHY_MI_MESH_OTP_H_ */
