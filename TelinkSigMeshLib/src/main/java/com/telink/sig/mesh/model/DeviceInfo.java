/********************************************************************************************************
 * @file DeviceInfo.java
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
package com.telink.sig.mesh.model;

import android.os.Handler;
import android.util.SparseBooleanArray;

import com.telink.sig.mesh.MeshManager;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.message.config.PubSetMessage;
import com.telink.sig.mesh.util.TelinkLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceInfo implements Serializable {
    //nvc增加代码--设备名称
    public String deviceName;
    //nvc增加代码--设备pid
    public String devicePid;
    //nvc增加代码--设备did
    public String deviceDid;
    /**
     * primary element unicast address
     */
    public int meshAddress;

    /**
     * mac address
     */
    public String macAddress;

    /**
     * element count
     */
    public int elementCnt = 0;

    /**
     * device subscription/group info
     */
    public List<Integer> subList = new ArrayList<>();

    /**
     * publish info
     * <i>for future use</i>
     */
//    public List<Integer> pubList = new ArrayList<>();

    // device lightness
    public int lum = 0;

    // device temperature
    public int temp = 0;

    /**
     * device on off state
     * 0:off 1:on -1:offline
     */
    private int onOff = -1;

    /**
     * device application key bind state
     * default unbind
     */
    public DeviceBindState bindState = DeviceBindState.UNBIND;

    // 设备绑定的model列表
    /**
     * update V2.6 增加多element支持后， bindState model 不再由用户选择， 而采用默认配置。
     * 此字段暂无意义
     */
    public short[] boundModels;

    // 本地保存的闹钟列表
    public List<Scheduler> schedulers = new ArrayList<>();


    public NodeInfo nodeInfo = null;

    public boolean selected = false;

    /**
     * 是否添加了状态订阅， demo中默认使用周期性上报
     */
//    private boolean pubSet;
    private PublishModel publishModel;

    // provision完成后保存设备的device key信息， 防止 bind 失败后同步json数据时， 因找不到device key无法重新bind的情况出现
    public byte[] deviceKey;

    // 是否打开relay功能 ： for test
    private boolean relayEnable = true;


    /**
     * 5s interval, 3 cnt
     */
//    private static final int OFFLINE_CHECK_PERIOD = MeshLib.Constant.PUB_INTERVAL * 3 + 2;
//    Handler handler = MeshManager.getInstance().getOfflineCheckHandler();
//    private OfflineCheckTask offlineCheckTask = new OfflineCheckTask() {
//        @Override
//        public void run() {
//            heartbeatCount++;
//            handler.postDelayed(offlineCheckTask, heartTimeOut);
//            TelinkLog.d("offline check task timeout count "+ heartbeatCount + " adr -- " + meshAddress);
//            //修改去除telinkApplication
//            if (heartbeatCount == 2){
////                MeshService.getInstance().getOnOff(meshAddress,0,null);
//                setPublication(meshAddress);
//            }else if (heartbeatCount >= 3 && onOff != -1) {
//                onOff = -1;
//                checkOfflineRun = false;
//                handler.removeCallbacks(offlineCheckTask);
//                TelinkLog.d("device offline : adr -- " + meshAddress + " mac -- " + macAddress);
//                MeshManager.getInstance().dispatchEvent(new MeshEvent(MeshManager.getInstance(), MeshEvent.EVENT_TYPE_DEVICE_OFFLINE, DeviceInfo.this));
//            }
//        }
//    };

    public int getOnOff() {
        return onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
//        heartbeatCount = 0;
        //修改去除telinkApplication
        TelinkLog.d("device on off status change : " + onOff + " adr -- " + meshAddress + " mac -- " + macAddress);
//        if (publishModel != null) {
            //修改去除telinkApplication
//            if (this.onOff != -1 && !checkOfflineRun) {
//                checkOfflineRun = true;
//                handler.postDelayed(offlineCheckTask, heartTimeOut);
//            }

//        }
    }


    public void setPublication(int address,int heartInterval){
        final int appKeyIndex = MeshService.getInstance().appKeyIndex;
        int modelId = SigMeshModel.SIG_MD_G_ONOFF_S.modelId;
        int pubEleAdr = getTargetEleAdr(modelId);
        publishModel = new PublishModel(pubEleAdr, modelId, 0xffff, heartInterval);
        PubSetMessage pubSetMessage = PubSetMessage.createDefault(address,
                publishModel.address, appKeyIndex, publishModel.period, publishModel.modelId, true);
        boolean result = MeshService.getInstance().setPublication(address, pubSetMessage, null);
        TelinkLog.d("setPublication : adr -- " + address + " result -- " + result);
    }


//    int heartInterval = 13 * 1000;
//    int heartTimeOut = 16 * 1000;
//    int heartbeatCount = 0;
    boolean checkOfflineRun = false;//检测设备离线线程是否运行
//    public boolean isHeratbeatOpen = false;//设备定时上报是否打开
    public boolean isPubSet() {
        return publishModel != null;
    }

    public PublishModel getPublishModel() {
        return publishModel;
    }

    public void setPublishModel(PublishModel model) {
        this.publishModel = model;
//        //修改去除telinkApplication
//        handler.removeCallbacks(offlineCheckTask);
//        if (this.onOff != -1) {
//            int timeout = pubTimeOut;
//            if (timeout > 0) {
//                handler.postDelayed(offlineCheckTask, timeout);
//            }
//        }
//        if (this.publishModel != null && this.onOff != -1) {
//            int timeout = 16 * 1000 * 3 + 2;
//            if (timeout > 0) {
//                handler.postDelayed(offlineCheckTask, timeout);
//            }
//        }
    }

    public boolean isRelayEnable() {
        return relayEnable;
    }

    public void setRelayEnable(boolean relayEnable) {
        this.relayEnable = relayEnable;
    }

    public Scheduler getSchedulerByIndex(byte index) {
        if (schedulers == null || schedulers.size() == 0) {
            return null;
        }
        for (Scheduler scheduler : schedulers) {
            if (scheduler.getIndex() == index) {
                return scheduler;
            }
        }
        return null;
    }

    public void saveScheduler(Scheduler scheduler) {
        if (schedulers == null) {
            schedulers = new ArrayList<>();
            schedulers.add(scheduler);
        } else {
            for (int i = 0; i < schedulers.size(); i++) {
                if (schedulers.get(i).getIndex() == scheduler.getIndex()) {
                    schedulers.set(i, scheduler);
                    return;
                }
            }
            schedulers.add(scheduler);
        }

    }

    // 0 - 15/0x0f
    public byte allocSchedulerIndex() {
        if (schedulers == null || schedulers.size() == 0) {
            return 0;
        }

        outer:
        for (byte i = 0; i <= 0x0f; i++) {
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getIndex() == i) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    public String getOnOffDesc() {
        if (this.onOff == 1) {
            return "ON";
        } else if (this.onOff == 0) {
            return "OFF";
        } else if (this.onOff == -1) {
            return "OFFLINE";
        }
        return "UNKNOWN";
    }

    /**
     * 获取设备支持on off model 的元素列表
     * 面板模式下， 可能支持多个on off model
     *
     * @return adr
     */
    public List<Integer> getOnOffEleAdrList() {
        if (nodeInfo == null) return null;
        List<Integer> addressList = new ArrayList<>();

        // element 地址由primary地址递增计算得出
        int eleAdr = this.meshAddress;
        outer:
        for (NodeInfo.Element element : nodeInfo.cpsData.elements) {
            if (element.sigModels != null) {
                for (int modelId : element.sigModels) {
                    if (modelId == SigMeshModel.SIG_MD_G_ONOFF_S.modelId) {
                        addressList.add(eleAdr++);
                        continue outer;
                    }
                }
            }
            eleAdr++;
        }

        return addressList;
    }

    /**
     * @param tarModelId target model id
     * @return element address: -1 err
     */
    public int getTargetEleAdr(int tarModelId) {
        if (nodeInfo == null) return -1;
        int eleAdr = this.meshAddress;
        for (NodeInfo.Element element : nodeInfo.cpsData.elements) {
            if (element.sigModels != null) {
                for (int modelId : element.sigModels) {
                    if (modelId == tarModelId) {
                        return eleAdr;
                    }
                }
            }

            if (element.vendorModels != null) {
                for (int modelId : element.vendorModels) {
                    if (modelId == tarModelId) {
                        return eleAdr;
                    }
                }
            }

            eleAdr++;
        }
        return -1;
    }

    /**
     * 获取设备亮度支持
     *
     * @return lum lightness union info
     */
    public SparseBooleanArray getLumEleInfo() {
        if (nodeInfo == null) return null;
        int eleAdr = this.meshAddress;

        SparseBooleanArray result = new SparseBooleanArray();

        for (NodeInfo.Element element : nodeInfo.cpsData.elements) {
            if (element.sigModels != null) {
                boolean levelSupport = false;
                boolean lumSupport = false;
                // 是否包含亮度信息
                for (int modelId : element.sigModels) {
                    if (modelId == SigMeshModel.SIG_MD_LIGHTNESS_S.modelId) {
                        lumSupport = true;
                    }
                    if (modelId == SigMeshModel.SIG_MD_G_LEVEL_S.modelId) {
                        levelSupport = true;
                    }
                }

                if (lumSupport) {
                    result.append(eleAdr, levelSupport);
                    return result;
                }
            }
            eleAdr++;
        }
        return null;
    }

    /**
     * 获取设备色温支持
     *
     * @return temp lightness
     */
    public SparseBooleanArray getTempEleInfo() {
        if (nodeInfo == null) return null;
        int eleAdr = this.meshAddress;

        SparseBooleanArray result = new SparseBooleanArray();

        for (NodeInfo.Element element : nodeInfo.cpsData.elements) {
            if (element.sigModels != null) {
                boolean levelSupport = false;
                boolean tempSupport = false;
                // 是否包含亮度信息
                for (int modelId : element.sigModels) {
                    if (modelId == SigMeshModel.SIG_MD_LIGHT_CTL_TEMP_S.modelId) {
                        tempSupport = true;
                    }
                    if (modelId == SigMeshModel.SIG_MD_G_LEVEL_S.modelId) {
                        levelSupport = true;
                    }
                }

                if (tempSupport) {
                    result.append(eleAdr, levelSupport);
                    return result;
                }
            }
            eleAdr++;
        }
        return null;
    }

}
