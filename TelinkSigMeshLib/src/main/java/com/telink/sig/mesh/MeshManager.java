package com.telink.sig.mesh;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventBus;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.lib.MeshLib;
import com.telink.sig.mesh.util.TelinkLog;

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
        TelinkLog.w("mesh lib version:" + Integer.toHexString(libVersion));
        meshLib.masterClockInit();
        meshLib.meshInitAll();
        meshLib.resetVendorId(VENDOR_ID);
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
