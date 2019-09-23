/* Copyright (c) 2010-2017 Xiaomi. All Rights Reserved.
 *
 * The information contained herein is property of Xiaomi.
 * Terms and conditions of usage are described in detail in 
 * STANDARD SOFTWARE LICENSE AGREEMENT.
 *
 * Licensees are granted free, non-transferable use of the information. NO
 * WARRANTY of ANY KIND is provided. This heading must NOT be removed from
 * the file.
 *
 */

/**@file
 *
 * @defgroup ble_mi Xiaomi Service
 * @{
 * @ingroup  ble_sdk_srv
 * @brief    Xiaomi Service implementation.
 *
 * @details The Xiaomi Service is a simple GATT-based service with many characteristics.
 *          Data received from the peer is passed to the application, and the data received
 *          from the application of this service is sent to the peer as Handle Value
 *          Notifications. This module demonstrates how to implement a custom GATT-based
 *          service and characteristics using the BLE Stack. The service
 *          is used by the application to send and receive pub_key and MSC Cert to and from the
 *          peer.
 *
 * @note The application must propagate BLE Stack events to the Xiaomi Service module
 *       by calling the ble_mi_on_ble_evt() function from the ble_stack_handler callback.
 */

#ifndef __MI_SERVICE_SECURE_H__
#define __MI_SERVICE_SECURE_H__
#include <stdint.h>

#define BLE_MI_MAX_DATA_LEN (GATT_MTU_SIZE_DEFAULT - 3) /**< Maximum length of data (in bytes) that can be transmitted to the peer by the Xiaomi  service module. */


/**@brief Xiaomi Service event handler type. */
typedef void (*ble_mi_data_handler_t) (uint8_t * p_data, uint16_t length);

/**@brief Xiaomi Service initialization structure.
 *
 * @details This structure contains the initialization information for the service. The application
 * must fill this structure and pass it to the service using the @ref ble_mi_init
 *          function.
 */
typedef struct
{
    ble_mi_data_handler_t data_handler; /**< Event handler to be called for handling received data. */
} ble_mi_init_t;

/**@brief Xiaomi Service structure.
 *
 * @details This structure contains status information related to the service.
 */
typedef struct {
	uint8_t                  uuid_type;               /**< UUID type for Xiaomi Service Base UUID. */
	uint16_t                 conn_handle;             /**< Handle of the current connection (as provided by the BLE Stack). BLE_CONN_HANDLE_INVALID if not in a connection. */
	uint16_t                 service_handle;          /**< Handle of Xiaomi Service (as provided by the BLE Stack). */

	uint16_t                 version_handle;          /**< Handle related to the characteristic value (as provided by the BLE Stack). */
	uint16_t                 ctrl_point_handle;       /**< Handle related to the characteristic value (as provided by the BLE Stack). */
	uint16_t                 secure_auth_handle;      /**< Handle related to the characteristic value (as provided by the BLE Stack). */
    
	uint16_t                 ota_ctrl_point;
	uint16_t                 ota_data;
//	bool                     is_notification_enabled; /**< Variable to indicate if the peer has enabled notification of the RX characteristic.*/
//	ble_mi_data_handler_t    data_handler;            /**< Event handler to be called for handling received data. */
} ble_mi_t;

/**@brief Function for initializing the Xiaomi Service.
 *
 * @param[in] p_mi_s_init  Information needed to initialize the service.
 *
 * @retval NRF_SUCCESS If the service was successfully initialized. Otherwise, an error code is returned.
 * @retval NRF_ERROR_NULL If either of the pointers p_mi_s or p_mi_s_init is NULL.
 */
uint32_t mi_sevice_init(void);

/**@brief Function for sending Auth status to the peer.
 *
 * @details This function sends the input status as an AUTH characteristic notification to the
 *          peer.
 *
 * @param[in] status    Status to be sent.
 *
 * @retval NRF_SUCCESS If the status was sent successfully. Otherwise, an error code is returned.
 */
uint32_t opcode_send(uint32_t status);
uint32_t opcode_recv(void);

#ifdef __cplusplus
}
#endif

#endif // __MI_SERVICE_SECURE_H__

/** @} */
