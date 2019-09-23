/********************************************************************************************************
 * @file TelinkMeshApplication.java
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
package com.telink.sig.mesh.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import com.telink.sig.mesh.MeshManager;
import com.telink.sig.mesh.TelinkApplication;
import com.telink.sig.mesh.demo.model.AddressRange;
import com.telink.sig.mesh.demo.model.LogInfo;
import com.telink.sig.mesh.demo.model.Mesh;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.SettingEvent;
import com.telink.sig.mesh.light.CtlStatusNotificationParser;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.ProvisionDataGenerator;
import com.telink.sig.mesh.light.parser.OnlineStatusInfoParser;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.model.NodeInfo;
import com.telink.sig.mesh.model.NotificationInfo;
import com.telink.sig.mesh.model.SigMeshModel;
import com.telink.sig.mesh.util.MeshUtils;
import com.telink.sig.mesh.util.TelinkLog;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Telinkg Mesh App
 * Created by kee on 2017/8/17.
 */
public class TelinkMeshApplication extends TelinkApplication {

    private static final String TAG = "TelinkMeshApplication";
    private static TelinkMeshApplication meshApplication;

    private Mesh mMesh;

    private List<LogInfo> logs;

    private boolean logEnable = false;

    private String localUUID;

    @Override
    public void onCreate() {
        super.onCreate();
        meshApplication = this;
        MeshManager.getInstance().init(this);
        initMesh();
//        TelinkLog.d("Mesh network key:" + Arrays.bytesToHexString(mMesh.networkKey, ":"));
//        TelinkLog.d("Mesh app key:" + Arrays.bytesToHexString(mMesh.appKey, ":"));
//        TelinkLog.d("Mesh key:" + Arrays.bytesToHexString(getMeshLib().getMeshKey(), ":"));
        logs = new ArrayList<>();
        this.setLogEnable(SharedPreferenceHelper.isLogEnable(this));
        closePErrorDialog();
    }

    private void closePErrorDialog(){
        if (Build.VERSION.SDK_INT <= 27){
            return;
        }
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initMesh() {
        Object object = FileSystem.readAsObject(this, Mesh.STORAGE_NAME);
        if (object == null) {
            mMesh = new Mesh();

            mMesh.networkKey = MeshUtils.generateRandom(16);
            mMesh.appKey = MeshUtils.generateRandom(16);
            List<Group> groups = new ArrayList<>();
            String[] groupNames = getResources().getStringArray(R.array.group_name);
            for (int i = 0; i < 8; i++) {
                Group group = new Group();
                group.address = i | 0xC000;
                group.name = groupNames[i];
                groups.add(group);
            }
            mMesh.groups = groups;

            mMesh.devices = new ArrayList<>();

            // init local address an provision index
            mMesh.localAddress = 1;
            mMesh.pvIndex = 1;
//            mMesh.selectedModels = new ArrayList<>();
            mMesh.selectedModels.addAll(java.util.Arrays.asList(SigMeshModel.getDefaultSelectList()));
            mMesh.provisionerUUID = getLocalUUID();
            mMesh.unicastRange = new AddressRange(0x01, 0xFF);
            mMesh.saveOrUpdate(this);
        } else {
            mMesh = (Mesh) object;
        }
    }


    public String getLocalUUID() {
        if (localUUID == null) {
            localUUID = SharedPreferenceHelper.getLocalUUID(this);
        }
        return localUUID;
    }

    public static TelinkMeshApplication getInstance() {
        return meshApplication;
    }

    public Mesh getMesh() {
        return mMesh;
    }

    public void setupMesh(Mesh mesh) {
//        if (!mesh.equals(mMesh)) {
        this.mMesh = mesh;
        dispatchEvent(new MeshEvent(this, MeshEvent.EVENT_TYPE_MESH_RESET, null));
//        }
    }

    @Override
    protected void onServiceCreated() {
        try{
            byte[] pvData = ProvisionDataGenerator.getProvisionData(mMesh.networkKey, mMesh.netKeyIndex, mMesh.ivUpdateFlag, mMesh.ivIndex, mMesh.localAddress);
            MeshService.getInstance().meshProvisionParSetDir(pvData, pvData.length);
//        mMesh.saveOrUpdate(getApplicationContext());
            MeshService.getInstance().setLocalAddress(mMesh.localAddress);

            List<byte[]> nodeList = new ArrayList<>();
            for (DeviceInfo device : mMesh.devices) {
                nodeList.add(device.nodeInfo.toVCNodeInfo());
            }
            byte[][] nodeData = nodeList.toArray(new byte[][]{});
            MeshService.getInstance().reattachNodes(nodeData);
            MeshService.getInstance().resetAppKey(mMesh.appKeyIndex, mMesh.netKeyIndex, mMesh.appKey);
        }catch (Exception e){
            e.printStackTrace();
        }

//        if (nodeData != null) {
//            for (int i = 0; i < nodeData.length; i++) {
//                TelinkLog.d("node data: " + i + " -- " + Arrays.bytesToHexString(nodeData[i], ":"));
//            }
//        }
//            TelinkMeshApplication.getInstance().getMesh().localAddress = addr;
//            SharedPreferenceHelper.setFirst(this, false);
//        }

        super.onServiceCreated();
    }

    @Override
    protected void onMeshEvent(Intent intent) {
        switch (intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE)) {
            case MeshEvent.EVENT_TYPE_DISCONNECTED:
                for (DeviceInfo tmpDev : mMesh.devices) {
                    tmpDev.setOnOff(-1);
                    tmpDev.lum = 0;
                    tmpDev.temp = 0;
                }
                break;
        }
        super.onMeshEvent(intent);
    }

    @Override
    protected void onSettingEvent(Intent intent) {
//        super.onSettingEvent(intent);
        final String type = intent.getStringExtra(MeshController.EXTRA_EVENT_TYPE);
        final int iv = intent.getIntExtra(SettingEvent.EVENT_TYPE_IV_UPDATE, -1);
        if (SettingEvent.EVENT_TYPE_IV_UPDATE.equals(type) && iv != -1) {
            if (mMesh != null) {
                mMesh.ivIndex = iv;
                mMesh.saveOrUpdate(this);
            }
        }
    }

    @Override
    protected void onOnlineStatusNotify(Intent intent) {
        byte[] data = intent.getByteArrayExtra(MeshController.EXTRA_NOTIFY_RAW);
        List<OnlineStatusInfoParser.OnlineStatusInfo> infoList = OnlineStatusInfoParser.create().parseInfo(data);
        if (infoList != null && mMesh != null) {
            for (OnlineStatusInfoParser.OnlineStatusInfo onlineStatusInfo : infoList) {
                if (onlineStatusInfo.status == null || onlineStatusInfo.status.length < 3) break;
                DeviceInfo deviceInfo = mMesh.getDeviceByMeshAddress(onlineStatusInfo.address);
                if (deviceInfo == null) continue;
                int onOff;
                if (onlineStatusInfo.sn == 0) {
                    onOff = -1;
                } else {
                    if (onlineStatusInfo.status[0] == 0) {
                        onOff = 0;
                    } else {
                        onOff = 1;
                    }


                }
                /*if (deviceInfo.getOnOff() != onOff){

                }*/
                deviceInfo.setOnOff(onOff);
                deviceInfo.lum = onlineStatusInfo.status[0];
                deviceInfo.temp = onlineStatusInfo.status[1];
            }
        }
        super.onOnlineStatusNotify(intent);
    }

    @Override
    protected void onNotificationRsp(Intent intent) {

        byte[] data = intent.getByteArrayExtra(MeshController.EXTRA_NOTIFY_RAW);
        NotificationInfo notificationInfo = (NotificationInfo) intent.getSerializableExtra(MeshController.EXTRA_NOTIFY_INFO);
        NotificationEvent event = new NotificationEvent(this, data, notificationInfo);
        boolean statusChanged = false;
        if (mMesh != null && mMesh.devices != null) {
//            NotificationInfo info = event.getNotificationInfo();
            if (notificationInfo == null || notificationInfo.params == null) return;
            byte[] params = notificationInfo.params;
            switch (event.getType()) {
                case NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS:
                    int onOff;
                    if (params.length == 1) {
                        onOff = params[0];
                    } else {
                        onOff = params[1];
                    }

                    for (DeviceInfo onlineDevice : mMesh.devices) {
                        if (onlineDevice.meshAddress == event.getNotificationInfo().srcAdr) {
                            statusChanged = true;
                            if (onlineDevice.getOnOff() != onOff) {
                                onlineDevice.setOnOff(onOff);
                            }
                            break;
                        }
                    }

                    break;


                case NotificationEvent.EVENT_TYPE_DEVICE_LEVEL_STATUS:
                    // 查找 该 element 下 的 亮度/色温 model

                    int srcAdr = event.getNotificationInfo().srcAdr;
                    short status;

                    // 是否带有目标值
                    if (params.length >= 4) {
                        status = (short) ((params[2] & 0xFF) | ((params[3] & 0xFF) << 8));
                    } else {
                        status = (short) ((params[0] & 0xFF) | ((params[1] & 0xFF) << 8));
                    }

                    byte tarVal = getMeshLib().level2Lum(status);
                    TelinkLog.d("lightness status val: " + tarVal + " -- " + status);
                    for (DeviceInfo onlineDevice : mMesh.devices) {
                        int deviceAdr = onlineDevice.meshAddress;
                        int updateType = -1;
                        if (onlineDevice.nodeInfo == null) {
                            continue;
                        }
                        for (NodeInfo.Element element : onlineDevice.nodeInfo.cpsData.elements) {
                            if (deviceAdr == srcAdr) {
                                if (element.containModel(SigMeshModel.SIG_MD_LIGHTNESS_S.modelId)) {
                                    updateType = 0;
                                    break;
                                } else if (element.containModel(SigMeshModel.SIG_MD_LIGHT_CTL_TEMP_S.modelId)) {
                                    updateType = 1;
                                    break;
                                }
                            }
                            deviceAdr++;
                        }

                        if (updateType == 0) {
                            // update lum
                            statusChanged = true;
                            TelinkLog.d("update type lum");
                            if (tarVal <= 0) {
                                onlineDevice.setOnOff(0);
                            } else {
                                onlineDevice.setOnOff(1);
                                onlineDevice.lum = tarVal;
                            }
                        } else if (updateType == 1) {
                            statusChanged = true;
                            // update temp
                            TelinkLog.d("update type temp");
                            onlineDevice.temp = tarVal;
                        }
                        /*if (onlineDevice.meshAddress == event.getNotificationInfo().srcAdr) {
                            if (lum <= 0) {
                                onlineDevice.onOff = 0;
                            } else {
                                onlineDevice.onOff = 1;
                                onlineDevice.lum = lum;
                            }

                            break;
                        }*/
                    }
                    break;

                case NotificationEvent.EVENT_TYPE_LIGHTNESS_STATUS_NOTIFY:
                    int liStatus;
                    if (params.length >= 4) {
                        liStatus = (short) ((params[2] & 0xFF) | ((params[3] & 0xFF) << 8));
                    } else {
                        liStatus = (short) ((params[0] & 0xFF) | ((params[1] & 0xFF) << 8));
                    }

                    int lightness = TelinkApplication.getInstance().getMeshLib().lightness2Lum(liStatus);
                    for (DeviceInfo onlineDevice : mMesh.devices) {
                        if (onlineDevice.meshAddress == event.getNotificationInfo().srcAdr) {
                            if (onlineDevice.getOnOff() == -1 || onlineDevice.lum != lightness) {
                                if (lightness <= 0) {
                                    onlineDevice.setOnOff(0);
                                } else {
                                    onlineDevice.setOnOff(1);
                                    onlineDevice.lum = lightness;
                                }
                                statusChanged = true;
                            }

                            break;
                        }
                    }
                    break;
                case NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY:
                    CtlStatusNotificationParser ctlParser = CtlStatusNotificationParser.create();
                    CtlStatusNotificationParser.CtlInfo ctlInfo = ctlParser.parse(params);
                    if (ctlInfo == null) return;
                    for (DeviceInfo onlineDevice : mMesh.devices) {
                        if (onlineDevice.meshAddress == event.getNotificationInfo().srcAdr) {
                            if (onlineDevice.getOnOff() == -1 || ctlInfo.lum != onlineDevice.lum || ctlInfo.temp != onlineDevice.temp) {
                                statusChanged = true;
                            }
                            if (ctlInfo.lum > 0) {
                                onlineDevice.setOnOff(1);
                                onlineDevice.lum = ctlInfo.lum;
                            } else {
                                onlineDevice.setOnOff(0);
                            }
                            onlineDevice.temp = ctlInfo.temp;
                            break;
                        }
                    }

                    break;

                case NotificationEvent.EVENT_TYPE_DEVICE_VERSION:

                    break;
            }

        }
        event.setStatusChanged(statusChanged);
        dispatchEvent(event);

    }


    public void setLogEnable(boolean enable) {
        if (this.logEnable != enable) {
            this.logEnable = enable;
        }
    }


    public void saveLog(String action) {
        if (logEnable) {
            logs.add(new LogInfo(action));
        }
    }

    public void saveLogInFile(String fileName, String logInfo) {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + File.separator + "TelinkSigMeshSetting");
        if (FileSystem.writeString(dir, fileName + ".txt", logInfo) != null) {
            TelinkLog.d("log saved in: " + fileName);
        }
    }

    public List<LogInfo> getLogInfo() {
        return logs;
    }

    public void clearLogInfo() {
        logs.clear();
    }

}
