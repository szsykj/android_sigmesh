#ifndef __MI_CONFIG_H__
#define __MI_CONFIG_H__
#include <stdint.h>
#define MI_IOT_TELINK_MODE 		1
#if MI_IOT_TELINK_MODE
//#define PRODUCT_ID			   0x03b7//telink lab pid
//#define PRODUCT_ID			   0x0379//generic pid
#define PRODUCT_ID			   0x03b4//yeelight pid
#else
#define PRODUCT_ID             0x0379//silicon lab pid
#endif
#define DEMO_CER_MODE		0
#define FLASH_CER_MODE		1 
#if MI_IOT_TELINK_MODE
#define MI_CER_MODE 	FLASH_CER_MODE
#endif
#define EVT_MAX_SIZE           16
#define EVT_QUEUE_SIZE         8

#define HAVE_MSC               0
#define MI_SCHD_PROCESS_IN_MAIN_LOOP 1

/* DEBUG */
#define PRINT_MSC_INFO         0
#define PRINT_MAC              0
#define PRINT_DEV_PUBKEY       0
#define PRINT_ECDHE            0
#define PRINT_SHA256           0
#define PRINT_SIGN             0
#define PRINT_LTMK             0

#define DEBUG_MIBLE            0

#define DFU_NVM_START          0x4A000UL      /**< DFU Start Address */
#define DFU_NVM_END            0x7E800UL      /**< DFU End Address */

#define DEV_SK_FLASH_ADR 	   0x7f000
#endif  /* __MI_CONFIG_H__ */ 


