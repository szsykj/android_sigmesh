/********************************************************************************************************
 * @file TelinkApplication.java
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
package com.telink.sig.mesh;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;

import com.telink.sig.mesh.ble.AdvertisingDevice;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventBus;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.OnlineStatusEvent;
import com.telink.sig.mesh.event.OtaEvent;
import com.telink.sig.mesh.event.ScanEvent;
import com.telink.sig.mesh.event.SettingEvent;
import com.telink.sig.mesh.lib.MeshLib;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.parser.OnlineStatusInfoParser;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.MeshCommand;
import com.telink.sig.mesh.model.NotificationInfo;
import com.telink.sig.mesh.model.OtaDeviceInfo;
import com.telink.sig.mesh.util.TelinkLog;
import com.telink.sig.mesh.util.AuthValueManager;

import java.security.Security;
import java.util.List;
import java.util.Locale;

/**
 * Telin application base
 * Created by kee on 2017/8/17.
 */

public abstract class TelinkApplication extends Application {

    private static final String TAG = "TelinkApplication";

    private static TelinkApplication mApp;

    private MeshLib meshLib;

    private EventBus<String> mEventBus;

    private BroadcastReceiver mLightReceiver;

    private Handler mOfflineCheckHandler;

    private static final short VENDOR_ID = 0x0211;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        HandlerThread offlineCheckThread = new HandlerThread("offline check thread");
        offlineCheckThread.start();
        mOfflineCheckHandler = new Handler(offlineCheckThread.getLooper());
        LocalBroadcastManager.getInstance(this).registerReceiver(makeLightReceiver(), makeLightFilter());
        mEventBus = new EventBus<>();

        meshLib = new MeshLib(this);

        // getLibVersion 必须调用
        int libVersion = meshLib.getLibVersion();

        TelinkLog.w("mesh lib version:" + Integer.toHexString(libVersion));
        meshLib.masterClockInit();
        meshLib.meshInitAll();
        meshLib.resetVendorId(VENDOR_ID);

        meshLib.setGattProCloudEn((byte) 0);
        meshLib.setVendorHelper(new MeshLib.VendorHelper() {
            @Override
            public int setProvCloudParams(byte[] pid, byte[] mac) {
                return 0;
            }

            @Override
            public int setProvCloudConfirm(byte[] confirm, byte[] confirmKey, byte[] proRandom) {
                String mac = MeshService.getInstance().getCurDeviceMac();
                byte[] authValue = AuthValueManager.getAuthData(mac);
                byte[] confirmData = new byte[32];
                System.arraycopy(proRandom,0,confirmData,0,16);
                System.arraycopy(authValue,0,confirmData,16,16);
                byte[] aes = AuthValueManager.aesCmac(confirmData,confirmKey);
                System.arraycopy(aes,0,confirm,0,16);

                TelinkLog.d("setProvCloudConfirm() called with: confirm = [" + AuthValueManager.getHexString(confirm) + "]");
                return 1;
            }

            @Override
            public int cloudDevConfirmCheck(byte[] confirmKey, byte[] devRandom, byte[] devConfirm) {
                String mac = MeshService.getInstance().getCurDeviceMac();
                byte[] authValue = AuthValueManager.getAuthData(mac);
//                byte[] authValue = AuthValueManager.getAuthValue("f8a7630c173a");
                byte[] confirmData = new byte[32];
                System.arraycopy(devRandom,0,confirmData,0,16);
                System.arraycopy(authValue,0,confirmData,16,16);
                byte[] confirm = AuthValueManager.aesCmac(confirmData,confirmKey);
                TelinkLog.d( "cloudDevConfirmCheck() called with: confirmKey = [" + AuthValueManager.getHexString(confirmKey) + "], devRandom = [" + AuthValueManager.getHexString(devRandom) + "], devConfirm = [" + AuthValueManager.getHexString(devConfirm) + "]");
                return AuthValueManager.compareConfirm(confirm,devConfirm)?1:0;
            }

            @Override
            public void gattProvisionNetInfoCallback() {

            }
        });
    }

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public Handler getOfflineCheckHandler() {
        return mOfflineCheckHandler;
    }

    public static TelinkApplication getInstance() {
        return mApp;
    }

    public MeshLib getMeshLib() {
        return meshLib;
    }

    public abstract void saveLog(String log);

    protected BroadcastReceiver makeLightReceiver() {
        if (this.mLightReceiver == null)
            this.mLightReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    if (intent.getAction() == null) return;
                    switch (intent.getAction()) {
                        case MeshController.ACTION_SCAN_EVENT:
                            onScanEvent(intent);
                            break;

                        case MeshController.ACTION_MESH_EVENT:
                            onMeshEvent(intent);
                            break;

                        case MeshController.ACTION_COMMAND_EVENT:
                            onCommandEvent(intent);
                            break;

                        case MeshController.ACTION_SETTING_EVENT:
                            onSettingEvent(intent);
                            break;

                        case MeshController.ACTION_STATUS_RESPONSE:
                            onNotificationRsp(intent);
                            break;

                        case MeshService.ACTION_SERVICE_CREATE:
                            onServiceCreated();
                            break;
                        case MeshService.ACTION_SERVICE_DESTROY:
                            onServiceDestroyed();
                            break;
                        case MeshController.ACTION_OTA:
                            dispatchEvent(new OtaEvent(this, intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE),
                                    (OtaDeviceInfo) intent.getSerializableExtra(MeshController.EXTRA_DEVICE_INFO)));
                            break;

                        case MeshController.ACTION_ONLINE_STATUS:
                            onOnlineStatusNotify(intent);
                            break;
                    }
                }
            };

        return this.mLightReceiver;
    }


    protected void onServiceCreated() {
        dispatchEvent(new Event<>(this, MeshController.EVENT_TYPE_SERVICE_CREATE));
    }

    protected void onServiceDestroyed() {
        dispatchEvent(new Event<>(this, MeshController.EVENT_TYPE_SERVICE_DESTROY));
    }

    protected void onScanEvent(Intent intent) {
        TelinkLog.d(TAG + "#ACTION_SCAN_EVENT");
        String eventType = intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE);
        if (eventType.equals(ScanEvent.DEVICE_FOUND)) {
            dispatchEvent(new ScanEvent(this, eventType, (AdvertisingDevice) intent.getParcelableExtra(MeshController.EXTRA_ADV_DEVICE)));
        } else {
            dispatchEvent(new ScanEvent(this, eventType));
        }

    }

    protected void onMeshEvent(Intent intent) {
        TelinkLog.d(TAG + "#onMeshEvent");
        dispatchEvent(new MeshEvent(this, intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE),
                (DeviceInfo) intent.getSerializableExtra(MeshController.EXTRA_DEVICE_INFO)));
    }

    protected void onCommandEvent(Intent intent) {
        TelinkLog.d(TAG + "#onCommandEvent");
        dispatchEvent(new CommandEvent(this,
                intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE),
                (MeshCommand) intent.getSerializableExtra(MeshController.EXTRA_MESH_COMMAND)));
    }

    protected void onSettingEvent(Intent intent) {
        TelinkLog.d(TAG + "#onMeshEvent");
        dispatchEvent(new SettingEvent(this, intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE),
                intent.getIntExtra(MeshController.EXTRA_SETTING_INFO, -1)));
    }

    protected void onNotificationRsp(Intent intent) {
        TelinkLog.d(TAG + "#onNotificationRsp");
        NotificationEvent event = new NotificationEvent(this, intent.getByteArrayExtra(MeshController.EXTRA_NOTIFY_RAW), (NotificationInfo) intent.getSerializableExtra(MeshController.EXTRA_NOTIFY_INFO));
        dispatchEvent(event);
    }

    protected void onOnlineStatusNotify(Intent intent) {
        TelinkLog.d(TAG + "#onOnlineStatusNotify");
        byte[] data = intent.getByteArrayExtra(MeshController.EXTRA_NOTIFY_RAW);
        TelinkLog.d(TAG + toHexString(data));

        List<OnlineStatusInfoParser.OnlineStatusInfo> onlineStatusInfos = OnlineStatusInfoParser.create().parseInfo(data);

        for (OnlineStatusInfoParser.OnlineStatusInfo onlineStatusInfo : onlineStatusInfos) {
            TelinkLog.d(TAG +" sn: "+onlineStatusInfo.sn+" address: "+onlineStatusInfo.address+" status data: " +parseByte2HexStr(onlineStatusInfo.status));

        }

        dispatchEvent(new OnlineStatusEvent(this, OnlineStatusEvent.ONLINE_STATUS_NOTIFY, data));
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase(Locale.US)+" ");
        }
        return sb.toString();
    }



    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String toHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0 || bytes.length % 2 != 0) {
            return null;
        }

        final int byteLength = bytes.length;
        final int charCount = 2 * byteLength;
        final char[] chars = new char[charCount];

        for (int i = 0; i < byteLength; i++) {
            final int byteHex = bytes[i] & 0xFF;
            chars[i * 2] = HEX_ARRAY[byteHex >>> 4];
            chars[i * 2 + 1] = HEX_ARRAY[byteHex & 0x0F];
        }
        return new String(chars);
    }

    protected IntentFilter makeLightFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(MeshController.ACTION_SCAN_EVENT);
        filter.addAction(MeshController.ACTION_MESH_EVENT);
        filter.addAction(MeshController.ACTION_COMMAND_EVENT);
        filter.addAction(MeshController.ACTION_SETTING_EVENT);
        filter.addAction(MeshController.ACTION_STATUS_RESPONSE);
        filter.addAction(MeshController.ACTION_OTA);
        filter.addAction(MeshController.ACTION_ONLINE_STATUS);
        filter.addAction(MeshService.ACTION_SERVICE_CREATE);
        filter.addAction(MeshService.ACTION_SERVICE_DESTROY);
        return filter;
    }

    /********************************************************************************
     * Event API
     *******************************************************************************/

    /**
     * 添加一个事件监听器
     *
     * @param eventType 事件类型
     * @param listener  事件监听器
     */
    public void addEventListener(String eventType, EventListener<String> listener) {
        this.mEventBus.addEventListener(eventType, listener);
    }

    /**
     * 移除事件监听器
     */
    public void removeEventListener(EventListener<String> listener) {
        this.mEventBus.removeEventListener(listener);
    }

    /**
     * 从事件监听器中移除指定的事件
     *
     * @param eventType type
     * @param listener  ls
     */
    public void removeEventListener(String eventType, EventListener<String> listener) {
        this.mEventBus.removeEventListener(eventType, listener);
    }

    /**
     * 移除所有的事件监听器
     */
    public void removeEventListeners() {
        this.mEventBus.removeEventListeners();
    }

    /**
     * 分发事件
     *
     * @param event ev
     */
    public void dispatchEvent(Event<String> event) {
        this.mEventBus.dispatchEvent(event);
    }
}
