#ifndef __MIBLE_SECURE_AUTH_H__
#define __MIBLE_SECURE_AUTH_H__
#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

#define MESH_REG_TYPE                       0x40UL
#define MESH_REG_START                      (MESH_REG_TYPE)
#define MESH_REG_SUCCESS                    (MESH_REG_TYPE+1)
#define MESH_REG_FAILED                     (MESH_REG_TYPE+2)
#define MESH_REG_VERIFY_SUCC                (MESH_REG_TYPE+3)
#define MESH_REG_S_CERT_INVAILD             (MESH_REG_TYPE+4)
#define MESH_REG_S_PUBKEY_INVAILD           (MESH_REG_TYPE+5)
#define MESH_REG_S_SIGN_INVAILD             (MESH_REG_TYPE+6)
#define MESH_REG_D_CERT_INVAILD             (MESH_REG_TYPE+7)
#define MESH_REG_D_PUBKEY_INVAILD           (MESH_REG_TYPE+8)
#define MESH_REG_D_SIGN_INVAILD             (MESH_REG_TYPE+9)

#define MESH_LOGIN_TYPE                     0x50UL
#define MESH_ADMIN_LOGIN_START              (MESH_LOGIN_TYPE)
#define MESH_ADMIN_LOGIN_SUCCESS            (MESH_LOGIN_TYPE+1)
#define MESH_ADMIN_LOGIN_FAILED             (MESH_LOGIN_TYPE+2)
#define MESH_ADMIN_LOGIN_VERIFY_FAILED      (MESH_LOGIN_TYPE+3)

#define MESH_SHARE_TYPE                     0x60UL
#define MESH_SHARE_LOGIN_START              (MESH_SHARE_TYPE)
#define MESH_SHARE_LOGIN_START_W_CERT       (MESH_SHARE_TYPE+4)
#define MESH_SHARE_LOGIN_SUCCESS            (MESH_SHARE_TYPE+1)
#define MESH_SHARE_LOGIN_FAILED             (MESH_SHARE_TYPE+2)
#define MESH_SHARE_LOGIN_EXPIRED            (MESH_SHARE_TYPE+3)


#define SYS_TYPE                       0xA0UL
#define SYS_KEY_RESTORE                (SYS_TYPE)
#define SYS_KEY_DELETE                 (SYS_TYPE+1)

#define ERR_TYPE                       0xE0UL
#define ERR_NOT_REGISTERED             (ERR_TYPE)
#define ERR_REGISTERED                 (ERR_TYPE+1)
#define ERR_REPEAT_LOGIN               (ERR_TYPE+2)

typedef enum {
	UNAUTHORIZATION = 0,
	ADMIN_AUTHORIZATION,
	SHARE_AUTHORIZATION
} mi_author_stat_t;

typedef enum {
	SCHD_EVT_REG_SUCCESS                    = 0x01,
	SCHD_EVT_REG_FAILED                     = 0x02,
	SCHD_EVT_ADMIN_LOGIN_SUCCESS            = 0x03,
	SCHD_EVT_ADMIN_LOGIN_FAILED             = 0x04,
	SCHD_EVT_SHARE_LOGIN_SUCCESS            = 0x05,
	SCHD_EVT_SHARE_LOGIN_FAILED             = 0x06,
	SCHD_EVT_TIMEOUT                        = 0x07,
	SCHD_EVT_KEY_NOT_FOUND                  = 0x08,
	SCHD_EVT_KEY_FOUND                      = 0x09,
	SCHD_EVT_KEY_DEL_FAIL                   = 0x0A,
	SCHD_EVT_KEY_DEL_SUCC                   = 0x0B,
	SCHD_EVT_MESH_REG_SUCCESS               = 0x0C,
	SCHD_EVT_MESH_REG_FAILED                = 0x0D
} schd_evt_id_t;

struct __PACKED prov_data_struct {
    uint8_t  netkey[16];
    uint16_t net_idx;
    uint8_t  flags;
    uint32_t iv;
    uint16_t address;
};
typedef struct prov_data_struct prov_data_t;

typedef struct {
    uint16_t net_idx;
    uint16_t app_idx;
    uint8_t appkey[16];
} appkey_item_t;

typedef struct {
    appkey_item_t *head;
    uint8_t size;
} appkey_list_t;

typedef struct {
    uint16_t elem_idx;
    uint16_t vendor;
    uint16_t model;
    uint16_t appkey_idx;
} bind_item_t;

typedef struct {
    bind_item_t *head;
    uint8_t size;
} model_bind_list_t;

typedef struct {
    uint8_t          (*p_devkey)[16];
    prov_data_t       *p_prov_data;
    appkey_list_t     *p_appkey_list;
    model_bind_list_t *p_bind_list;
} mesh_config_t;

typedef struct {
    schd_evt_id_t id;
    union {
        mesh_config_t mesh_config;
    } data;
} schd_evt_t;


typedef void (*mi_schd_event_handler_t)(schd_evt_t *p_event);
typedef int (*mi_kbd_input_get_t)(uint8_t *pdata, uint8_t len);
typedef int (*mi_msc_power_manage_t)(bool power_stat);
extern uint32_t schd_ticks;

void set_mi_authorization(mi_author_stat_t status);
int get_mi_mesh_static_oob(uint8_t *p_out, uint8_t len);
uint32_t get_mi_reg_stat(void);
uint32_t get_mi_authorization(void);
uint32_t get_mi_key_id(void);
uint32_t mi_scheduler_init(uint32_t interval, mi_schd_event_handler_t handler,
    mi_kbd_input_get_t recorder, mi_msc_power_manage_t manager, const void * p_iic_config);
uint32_t mi_scheduler_start(uint32_t status);
void mi_schd_process(void);

#ifdef __cplusplus
}
#endif

/** @} */

#endif  /* __MIBLE_SECURE_AUTH_H__ */ 
