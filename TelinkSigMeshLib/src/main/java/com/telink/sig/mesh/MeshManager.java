package com.telink.sig.mesh;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventBus;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.lib.MeshLib;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.util.AuthValueManager;
import com.telink.sig.mesh.util.TelinkLog;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @program: TelinkSigMeshRelease
 * @description:
 * @author: jarLiao
 * @create: 2019-09-18
 **/
public class MeshManager {
    public static Application mApplication;
    private static final short VENDOR_ID = 0x0211;
    private Handler mOfflineCheckHandler;

    private static volatile MeshManager instance = null;
    private MeshLib meshLib;
    private EventBus<String> mEventBus;

    private MeshManager(){}

    public static MeshManager getInstance() {
        if (instance == null){
            synchronized (MeshManager.class) {
                if (instance == null) {
                    instance = new MeshManager();
                }
            }
        }
        return instance;
     }


    public void init(Context context){
        mApplication = (Application) context.getApplicationContext();
        mEventBus = new EventBus<>();

        HandlerThread offlineCheckThread = new HandlerThread("offline check thread");
        offlineCheckThread.start();
        mOfflineCheckHandler = new Handler(offlineCheckThread.getLooper());

        meshLib = new MeshLib(mApplication);
        // getLibVersion 必须调用
        int libVersion = meshLib.getLibVersion();
        meshLib.masterClockInit();
        meshLib.meshInitAll();
        meshLib.resetVendorId(VENDOR_ID);
        meshLib.setGattProCloudEn((byte) 1);
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


    public Handler getOfflineCheckHandler() {
        return mOfflineCheckHandler;
    }

    public MeshLib getMeshLib() {
        return meshLib;
    }

    public void saveLog(String log){

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
