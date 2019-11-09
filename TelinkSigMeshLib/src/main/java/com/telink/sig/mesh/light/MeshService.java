/********************************************************************************************************
 * @file MeshService.java
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
package com.telink.sig.mesh.light;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.telink.sig.mesh.R;
import com.telink.sig.mesh.light.parameter.AutoConnectParameters;
import com.telink.sig.mesh.light.parameter.KeyBindParameters;
import com.telink.sig.mesh.light.parameter.ProvisionParameters;
import com.telink.sig.mesh.light.parameter.RemoteProvisionParameters;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.MeshCommand;
import com.telink.sig.mesh.model.NodeInfo;
import com.telink.sig.mesh.model.Scheduler;
import com.telink.sig.mesh.model.message.DeltaMessage;
import com.telink.sig.mesh.model.message.HSLMessage;
import com.telink.sig.mesh.model.message.LevelMessage;
import com.telink.sig.mesh.model.message.LightnessMessage;
import com.telink.sig.mesh.model.message.ModelMessage;
import com.telink.sig.mesh.model.message.OnOffMessage;
import com.telink.sig.mesh.model.message.SceneRecallMessage;
import com.telink.sig.mesh.model.message.SceneStoreMessage;
import com.telink.sig.mesh.model.message.TemperatureMessage;
import com.telink.sig.mesh.model.message.TimeMessage;
import com.telink.sig.mesh.model.message.TransitionTime;
import com.telink.sig.mesh.model.message.config.PubGetMessage;
import com.telink.sig.mesh.model.message.config.PubSetMessage;
import com.telink.sig.mesh.model.message.config.RelaySetMessage;
import com.telink.sig.mesh.model.message.config.SubSigGetMessage;
import com.telink.sig.mesh.model.message.config.SubSetMessage;
import com.telink.sig.mesh.model.message.config.SubVendorGetMessage;
import com.telink.sig.mesh.util.TelinkLog;
import com.telink.sig.mesh.util.UnitConvert;

import static com.telink.sig.mesh.light.Opcode.G_INFO_GET;

/**
 * MeshService
 */
public class MeshService extends Service {

    private static final String TAG = "MeshService";

    protected MeshController mMeshController;

    private static MeshService mThis;

    public static final String ACTION_SERVICE_CREATE = "ACTION_SERVICE_CREATE";

    public static final String ACTION_SERVICE_DESTROY = "ACTION_SERVICE_DESTROY";

    public int netKeyIndex = 0;

    public int appKeyIndex = 0;

    //nvc增加代码--后台服务9.0处理
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            this.stopForeground(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //nvc增加代码--后台服务9.0处理
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知渠道的id
        String id = "my_channel_01";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = "Celightling";
//         用户可以看到的通知渠道的描述
        String description = "";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
        mChannel.setDescription(description);
//         设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(false);
        mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(false);
//        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);

        // 为该通知设置一个id
        int notifyID = 1;
        // 通知渠道的id
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Celightling Running...")
                .setContentText("Click to know more or stop the APP.")
                .setSmallIcon(R.mipmap.ic)
                .setChannelId(CHANNEL_ID)
                .build();
        startForeground(1, notification);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static MeshService getInstance() {
        return mThis;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TelinkLog.d(TAG + "onCreate");
        mThis = this;
        this.mMeshController = new MeshController();
        mMeshController.start(getApplicationContext());
        onServiceCreated();
    }

    private void onServiceCreated() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SERVICE_CREATE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TelinkLog.d(TAG + "-onDestroy");
        onServiceDestroyed();
        if (this.mMeshController != null) {
            this.mMeshController.stop();
        }
    }

    private void onServiceDestroyed() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SERVICE_DESTROY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void setLocalAddress(int address) {
        mMeshController.setLocalAddress(address);
    }

    public void setNetKeyIndex(int netKeyIndex) {
        this.netKeyIndex = netKeyIndex;
    }

    public void setAppKeyIndex(int appKeyIndex) {
        this.appKeyIndex = appKeyIndex;
    }

    /*******************************************************************************************
     * Actions
     *******************************************************************************************/

    /**
     * scan for devices
     * scan result will be post by event
     *
     * @param parameters scan params
     * @see com.telink.sig.mesh.event.ScanEvent#DEVICE_FOUND target device found
     * @see com.telink.sig.mesh.event.ScanEvent#SCAN_TIMEOUT scan timeout for COMMON_SCAN_TIMEOUT
     * @see com.telink.sig.mesh.event.ScanEvent#SCAN_FAIL scan fail
     */
    public void startScan(ScanParameters parameters) {
        mMeshController.startScan(parameters);
    }

    /**
     * start provision
     * provision result will post by event
     *
     * @param provisionParameters provision params
     * @see com.telink.sig.mesh.event.MeshEvent#EVENT_TYPE_PROVISION_SUCCESS
     * @see com.telink.sig.mesh.event.MeshEvent#EVENT_TYPE_PROVISION_FAIL
     */
    public void startProvision(ProvisionParameters provisionParameters) {
        mMeshController.startProvision(provisionParameters);
    }

    public void startRemoteProvision(RemoteProvisionParameters parameters) {
        mMeshController.startRemoteProvision(parameters);
    }

    public void startRemoteBind(KeyBindParameters parameters) {
        mMeshController.startRemoteBind(parameters);
    }

    /**
     * start bind app key
     *
     * @param bindParameters bind params
     * @see com.telink.sig.mesh.event.MeshEvent#EVENT_TYPE_KEY_BIND_SUCCESS
     * @see com.telink.sig.mesh.event.MeshEvent#EVENT_TYPE_KEY_BIND_FAIL
     */
    public void startKeyBind(KeyBindParameters bindParameters) {
        mMeshController.startKeyBind(bindParameters);
    }

    /**
     * connect mesh network
     *
     * @see com.telink.sig.mesh.event.MeshEvent#EVENT_TYPE_AUTO_CONNECT_LOGIN
     */
    public void autoConnect(AutoConnectParameters autoConnectParameters) {
        mMeshController.autoConnect(autoConnectParameters);
    }

    /**
     * update connect params when network info changed
     */
    public void updateAutoConnectParams(AutoConnectParameters autoConnectParameters) {
        mMeshController.updateAutoConnectParams(autoConnectParameters);
    }

    /**
     * set action idle
     *
     * @param disconnect if disconnect current device
     */
    public void idle(boolean disconnect) {
        mMeshController.idle(disconnect);
    }

    /**
     * ota for target device with mac
     *
     * @param mac      mac
     * @param firmware firmware info
     */
    public void startOta(String mac, byte[] firmware) {
        mMeshController.startOta(mac, firmware);
    }


    public String getCurDeviceMac() {
        return mMeshController.getCurDeviceMac();
    }

    /**
     * get mesh if is offline in auto connect mode
     *
     * @return offline
     */
    public boolean isOffline() {
        return mMeshController.getCurDeviceMac() == null;
    }

    //nvc增加代码--查询当前模式
    public int getActionMode(){return  mMeshController.getActionMode();}


    /*******************************************************************************************
     * mesh setting
     *******************************************************************************************/

    public void filterInit(int adr) {
        mMeshController.filterInit(adr);
    }

    /**
     * 设置本地的provision信息
     *
     * @param data data
     * @param len  len
     */
    public void meshProvisionParSetDir(byte[] data, int len) {
        mMeshController.meshProvisionParSetDir(data, len);
    }

    /**
     * reset nodes info
     *
     * @param nodesData nodes info, if null all nodes info will be removed
     */
    public void reattachNodes(byte[][] nodesData) {
        mMeshController.reattachNodes(nodesData);
    }

    /**
     * remove single node info from lib
     *
     * @param address node address
     * @see com.telink.sig.mesh.model.DeviceInfo#nodeInfo
     * @see NodeInfo#toVCNodeInfo()
     */
    public void removeNodeInfo(int address) {
        mMeshController.removeNodeInfo(address);
    }

    /**
     * add single node to lib
     *
     * @param nodeInfo
     * @see com.telink.sig.mesh.model.DeviceInfo#nodeInfo
     * @see NodeInfo#toVCNodeInfo()
     */
    public void addNodeInfo(int address, byte[] nodeInfo) {
        mMeshController.addNodeInfo(address, nodeInfo);
    }

    /**
     * reset application key
     */
    public void resetAppKey(int appKeyIndex, int netKeyIndex, byte[] appKey) {
        mMeshController.resetAppKey(appKeyIndex, netKeyIndex, appKey);
    }

    /*******************************************************************************************
     * tests
     *******************************************************************************************/

    public void ivTest() {
        mMeshController.ivTest();
    }

    public void snoTest() {
        mMeshController.snoTest();
    }

    public void sendMeshTestCmd(int addr, int mode) {
        mMeshController.sendMeshTestCmd(addr, mode);
    }

    /*******************************************************************************************
     * mesh cmd
     *******************************************************************************************/

    /**
     * send mesh command by model message, include config model
     *
     * @param meshCommand command info
     *                    for reliable command, tag is recommended for differing
     * @return if command will be sent
     */
    public boolean sendMeshCommand(MeshCommand meshCommand) {
        TelinkLog.d("Send Mesh Command : " + meshCommand.toString());
        return mMeshController.sendMeshCommand(meshCommand);
    }


    /**
     * common command
     *
     * @param params not suggested to call direct
     * @see #sendMeshCommand(MeshCommand)
     */
    public void sendOpByINI(byte[] params) {
        mMeshController.sendOpByINI(params);
    }

    /********************* generic *********************/

    /**
     * get on off state
     *
     * @param adr dest address: unicast address , group address, 0xFFFF
     */
    public boolean getOnOff(int adr, int rspMax, Object tag) {
        /*MeshCommand command = new MeshCommand();
        command.netKeyIndex = this.netKeyIndex;
        command.applicationKeyIndex = this.appKeyIndex;
        command.retryCnt = CMD_RETRY_CNT;
        command.rspMax = (byte) rspMax;
        command.destAddress = adr;
        command.opcode = Opcode.G_ONOFF_GET.getValue();*/
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.G_ONOFF_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * @param adr                  dest address
     * @param onOff                0 off, 1 on
     * @param rspMax               rsp cnt max
     * @param ack                  true:  ack, false: no ack
     * @param transTimeMillisecond transition time, unit millisecond
     * @param delay                delay time, unit 5 millisecond
     * @see TransitionTime#getValue()
     */
    public boolean setOnOff(int adr, byte onOff, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {

        MeshCommand command = MeshCommand.newInstance(
                this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.G_ONOFF_SET.getValue() : Opcode.G_ONOFF_SET_NOACK.getValue());

        OnOffMessage message = OnOffMessage.createInstance(onOff);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * get lightness
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getLevel(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.G_LEVEL_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * @param adr                  dest address
     * @param level                lightness
     * @param ack                  true:  ack, false: no ack
     * @param rspMax               rsp cnt max
     * @param transTimeMillisecond transition time, unit millisecond
     * @param delay                delay time, unit 5 millisecond
     * @see TransitionTime#getValue()
     */
    public boolean setLevel(int adr, int level, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.G_LEVEL_SET.getValue() : Opcode.G_LEVEL_SET_NOACK.getValue());

        LevelMessage message = LevelMessage.createInstance(level);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * @param adr                  dest address
     * @param delta                delta value
     * @param ack                  if ack
     * @param rspMax               response max
     * @param transTimeMillisecond transition time , unit millisecond
     * @param delay                delay
     */
    public boolean setDelta(int adr, int delta, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.G_DELTA_SET.getValue() : Opcode.G_DELTA_SET_NOACK.getValue());

        DeltaMessage message = DeltaMessage.createInstance(delta);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /********************* lighting *********************/

    /**
     * get lightness
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getLightness(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.LIGHTNESS_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * set lightness
     *
     * @param adr                  dest adr
     * @param lightness            lightness 0-65535
     * @param rspMax               rsp max
     * @param ack                  ack
     * @param transTimeMillisecond trans time
     * @param delay                delay
     */
    public boolean setLightness(int adr, int lightness, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.LIGHTNESS_SET.getValue() : Opcode.LIGHTNESS_SET_NOACK.getValue());

        LightnessMessage message = LightnessMessage.createInstance(lightness);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    public boolean setLum(int adr, int lum, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        int lightness = UnitConvert.lum2lightness(lum);
        return this.setLightness(adr, lightness, ack, rspMax, transTimeMillisecond, delay, tag);
    }


    /**
     * get temperature
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getTemperature(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.LIGHT_CTL_TEMP_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * set temperature
     *
     * @param adr                  dest adr
     * @param temperature          temperature 800-20000
     * @param rspMax               rsp max
     * @param ack                  ack
     * @param transTimeMillisecond trans time
     * @param delay                delay
     */
    public boolean setTemperature(int adr, int temperature, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.LIGHT_CTL_TEMP_SET.getValue() : Opcode.LIGHT_CTL_TEMP_SET_NOACK.getValue());

        TemperatureMessage message = TemperatureMessage.createInstance(temperature);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * @param adr                  dest address
     * @param temperature100       0-100
     * @param rspMax               rsp max
     * @param ack                  if ack
     * @param transTimeMillisecond trans time
     * @param delay                delay
     * @see #setTemperature(int, int, boolean, int, long, byte, Object)
     */
    public boolean setTemperature100(int adr, int temperature100, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        int temp = UnitConvert.temp100ToTemp(temperature100);
        return this.setTemperature(adr, temp, ack, rspMax, transTimeMillisecond, delay, tag);
    }

    /**
     * get HSL
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getHSL(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.LIGHT_HSL_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * set HSL
     *
     * @param adr                  dest
     * @param hue                  hue 0-65535
     * @param saturation           saturation 0-65535
     * @param lightness            lightness 0-65535
     * @param rspMax               rspMax
     * @param ack                  ack
     * @param transTimeMillisecond transTimeMillisecond
     * @param delay                delay
     */
    public boolean setHSL(int adr, int hue, int saturation, int lightness, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {

        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr,
                ack ? Opcode.LIGHT_HSL_SET.getValue() : Opcode.LIGHT_HSL_SET_NOACK.getValue());

        HSLMessage message = HSLMessage.createInstance(hue, saturation, lightness);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * set HSL
     *
     * @param adr                  dest
     * @param hue                  hue 0-100
     * @param saturation           saturation 0-100
     * @param lightness            lightness 0-100
     * @param rspMax               rspMax
     * @param ack                  ack
     * @param transTimeMillisecond transTimeMillisecond
     * @param delay                delay
     */
    public boolean setHSL100(int adr, int hue, int saturation, int lightness, boolean ack, int rspMax, long transTimeMillisecond, byte delay, Object tag) {
        int h = UnitConvert.lum2lightness(hue);
        int s = UnitConvert.lum2lightness(saturation);
        int l = UnitConvert.lum2lightness(lightness);
        return this.setHSL(adr, h, s, l, ack, rspMax, transTimeMillisecond, delay, tag);
    }

    /**
     * get time
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getTime(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.TIME_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }


    public boolean setTime(int adr, int rspMax, long taiTime, int zoneOffset, Object tag) {
//        long time = Calendar.getInstance().getTimeInMillis() / 1000 - MeshLib.Constant.TAI_OFFSET_SECOND;
//        int offset = UnitConvert.getZoneOffset();
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax,
                adr, Opcode.TIME_SET.getValue());

        command.params = TimeMessage.createInstance(taiTime, zoneOffset).toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * get scheduler
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getScheduler(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.SCHD_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * get scheduler action
     *
     * @param adr    dest address
     * @param rspMax response max cnt
     */
    public boolean getSchedulerAction(int adr, int rspMax, int index, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex, this.appKeyIndex, rspMax, adr, Opcode.SCHD_ACTION_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }


    /**
     * set scheduler acion
     *
     * @param adr       dest
     * @param ack       ack
     * @param rspMax    rsp cnt
     * @param scheduler scheduler info
     */
    public boolean setSchedulerAction(int adr, boolean ack, int rspMax, Scheduler scheduler, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                ack ? Opcode.SCHD_ACTION_SET.getValue() : Opcode.SCHD_ACTION_SET_NOACK.getValue());
//        Scheduler scheduler = new Scheduler();
        command.params = scheduler.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * store scene
     */
    public boolean getScene(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                Opcode.SCENE_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * store scene
     */
    public boolean getSceneRegister(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                Opcode.SCENE_REG_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * store scene
     *
     * @param id scene id
     */
    public boolean storeScene(int adr, boolean ack, int rspMax, int id, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                ack ? Opcode.SCENE_STORE.getValue() : Opcode.SCENE_STORE_NOACK.getValue());
        command.params = SceneStoreMessage.createInstance(id).toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * recall scene
     *
     * @param id scene id
     *           int adr, int rspMax, int id, int ack
     */
    public boolean recallScene(int adr, boolean ack, int rspMax, int id, long transTimeMillisecond, byte delay, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                ack ? Opcode.SCENE_RECALL.getValue() : Opcode.SCENE_RECALL_NOACK.getValue());

        SceneRecallMessage message = SceneRecallMessage.createInstance(id);
        message.transTime = TransitionTime.fromTime(transTimeMillisecond).getValue();
        message.delay = delay;

        command.params = message.toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    public boolean getVersion(int adr, int rspMax, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                G_INFO_GET.getValue());
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    /**
     * store scene
     *
     * @param id scene id
     */
    public boolean deleteScene(int adr, boolean ack, int rspMax, int id, Object tag) {
        MeshCommand command = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                rspMax, adr,
                ack ? Opcode.SCENE_DEL.getValue() : Opcode.SCENE_DEL_NOACK.getValue());
        command.params = SceneStoreMessage.createInstance(id).toBytes();
        command.tag = tag;
        return this.sendMeshCommand(command);
    }

    // config

    /**
     * get device subscription
     *
     * @param nodeAdr    node address
     * @param elementAdr element address
     * @param modelId    model id 2 or 4 bytes
     * @param sig        if sig model or vendor model
     */
    public boolean getSubscription(int nodeAdr, int elementAdr, int modelId, boolean sig, Object tag) {
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr,
                sig ? Opcode.CFG_SIG_MODEL_SUB_GET.getValue() : Opcode.CFG_VENDOR_MODEL_SUB_GET.getValue());
        ModelMessage message;
        if (sig) {
            message = SubSigGetMessage.createInstance(elementAdr, modelId);
        } else {
            message = SubVendorGetMessage.createInstance(elementAdr, modelId);
        }
        meshCommand.params = message.toBytes();
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }

    /**
     * @param type    add: 0, delete: 1
     * @param nodeAdr node address
     * @param eleAdr  element address
     * @param subAdr  subscription address, usually group address
     * @param modelId model id: sig 2 bytes, vendor 4 bytes
     * @param sig     if is sig model
     */
    public boolean setSubscription(int type, int nodeAdr, int eleAdr, int subAdr, int modelId, boolean sig, Object tag) {

        int opcode = -1;
        if (type == 0) {
            opcode = Opcode.CFG_MODEL_SUB_ADD.getValue();
        } else if (type == 1) {
            opcode = Opcode.CFG_MODEL_SUB_DEL.getValue();
        }
        if (opcode == -1) {
            return false;
        }
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr, opcode);
        meshCommand.params = SubSetMessage.createInstance(eleAdr, subAdr, modelId, sig).toBytes();
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }


    /**
     * @param nodeAdr node address
     * @param eleAdr  element address
     * @param modelId model id
     * @param sig     is sig model
     */
    public boolean getPublication(int nodeAdr, int eleAdr, int modelId, boolean sig, Object tag) {
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr, Opcode.CFG_MODEL_PUB_GET.getValue());
        meshCommand.params = PubGetMessage.createInstance(eleAdr, modelId, sig).toBytes();
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }

    /**
     * set publish
     *
     * @param nodeAdr node address
     * @param message message
     */
    public boolean setPublication(int nodeAdr, PubSetMessage message, Object tag) {
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr, Opcode.CFG_MODEL_PUB_SET.getValue());
        meshCommand.params = message.toBytes();
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }

    public boolean setRelay(int nodeAdr, int relayValue, Object tag) {
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr, Opcode.CFG_RELAY_SET.getValue());
        meshCommand.params = RelaySetMessage.createInstance((byte) relayValue, 0, 0)
                .toBytes();
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }


    public boolean resetNode(int nodeAdr, Object tag) {
        MeshCommand meshCommand = MeshCommand.newInstance(this.netKeyIndex,
                this.appKeyIndex,
                1, nodeAdr, Opcode.NODE_RESET.getValue());
        meshCommand.tag = tag;
        return this.sendMeshCommand(meshCommand);
    }

    /*******************************************************************************************
     * online status
     *******************************************************************************************/
    public boolean getOnlineStatus() {
        return mMeshController.getOnlineStatus();
    }

    /*******************************************************************************************
     * deprecated cmd func
     *******************************************************************************************/

    /**
     * 获取订阅/分组信息
     *
     * @param nodeAdr 节点地址
     * @param eleAdr  暂同节点地址
     * @param modelId SIG_MD_G_ONOFF_S || SIG_MD_G_ONOFF_C
     * @see #getSubscription(int, int, int, boolean, Object)
     * @deprecated
     */
    public void cfgCmdSubGet(int nodeAdr, int eleAdr, int modelId) {
        mMeshController.cfgCmdSubGet(nodeAdr, eleAdr, modelId);
    }

    /**
     * 设置订阅/分组信息
     *
     * @param opCode  添加／删除 CFG_MODEL_SUB_ADD || CFG_MODEL_SUB_DEL
     * @param nodeAdr 节点地址
     * @param eleAdr  元素地址
     * @param subAdr  订阅地址
     * @param modelId SIG_MD_G_ONOFF_S || SIG_MD_G_ONOFF_C
     * @see #setSubscription(int, int, int, int, int, boolean, Object)
     * @deprecated
     */
    public boolean cfgCmdSubSet(int opCode, int nodeAdr, int eleAdr, int subAdr, int modelId, boolean sig) {
        return mMeshController.cfgCmdSubSet(opCode, nodeAdr, eleAdr, subAdr, modelId, sig);
    }


    /**
     * 获取public address
     *
     * @param nodeAdr
     * @param eleAdr
     * @param modelId
     * @see #getPublication(int, int, int, boolean, Object)
     * @deprecated
     */
    public void cfgCmdPubGet(int nodeAdr, int eleAdr, int modelId, boolean sig) {
        mMeshController.cfgCmdPubGet(nodeAdr, eleAdr, modelId, sig);
    }

    /**
     * 设置public address
     *
     * @param nodeAdr
     * @param eleAdr
     * @param pubAdr
     * @param modelId
     * @param sig     0 false, 1 true
     * @see #setPublication(int, PubSetMessage, Object) ()
     * @deprecated
     */
    public boolean cfgCmdPubSet(int nodeAdr, int eleAdr, int pubAdr, int modelId, byte[] params, boolean sig) {
        return mMeshController.cfgCmdPubSet(nodeAdr, eleAdr, pubAdr, modelId, params, sig);
    }

    /**
     * set relay
     *
     * @param nodeAdr
     * @param value
     * @see #setRelay(int, int, Object)
     * @deprecated
     */
    public boolean cfgCmdRelaySet(int nodeAdr, int value) {
        return mMeshController.cfgCmdRelaySet(nodeAdr, value);
    }


    /**
     * @see #getOnOff(int, int, Object)
     * @deprecated
     */
    public boolean cmdGetOnOff(int addr, int rspMax) {
        return mMeshController.cmdGetOnOff(addr, rspMax);
    }

    /**
     * @param adr    目标地址
     * @param rspMax means after sending the parameter,it will how many different packets back
     *               示例为2：列表中有2个设备
     * @param onOff  1 means on ,and 0 means off
     * @param ack    1 means send reliable cmd ,and the node will send rsp ,0 means unreliable ,will not send
     *               1 有回复 0 无回复
     * @see #setOnOff(int, byte, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdOnOff(int adr, byte rspMax, byte onOff, int ack) {
        mMeshController.cmdOnOff(adr, rspMax, onOff, ack);
    }

    /**
     * 获取level信息
     *
     * @param addr   地址
     * @param rspMax 回复次数
     * @see #getLevel(int, int, Object)
     * @deprecated
     */
    public boolean cmdGetLevel(int addr, int rspMax) {
        return mMeshController.cmdGetLevel(addr, rspMax);
    }

    /**
     * 设置level信息 ，addr可以是单灯，group，0xffff
     *
     * @param addr  地址
     * @param level lightness
     * @see #setLevel(int, int, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSetLevel(int addr, byte level) {
        mMeshController.cmdSetLevel(addr, level);
    }

    /**
     * @see #setDelta(int, int, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSetDelta(int addr, byte rspMax, int delta, int ack, byte[] transition) {
        mMeshController.cmdSetDelta(addr, rspMax, delta, ack, transition);
    }

    /**
     * @see #getLightness(int, int, Object)
     * @deprecated
     */
    public boolean cmdGetLum(int addr, int rspMax) {
        return mMeshController.cmdGetLum(addr, rspMax);
    }

    /**
     * @see #setLum(int, int, boolean, int, long, byte, Object)
     * @see #setLightness(int, int, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSetLum(int addr, byte lum) {
        mMeshController.cmdSetLum(addr, lum);
    }

    /**
     * @see #setTemperature(int, int, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSetTemp(int addr, byte temp) {
        mMeshController.cmdSetTemp(addr, temp);
    }

    /**
     * @see #getTemperature(int, int, Object)
     * @deprecated
     */
    public boolean cmdGetTemp(int addr, int rspMax) {
        return mMeshController.cmdGetTemp(addr, rspMax);
    }

    /**
     * @see #setHSL100(int, int, int, int, boolean, int, long, byte, Object)
     * @see #setHSL(int, int, int, int, boolean, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSetHSL100(int addr, byte rspMax, byte hue, byte saturation, byte lightness, int ack, byte[] transition) {
        mMeshController.cmdSetHSL100(addr, rspMax, hue, saturation, lightness, ack, transition);
    }

    /**
     * @deprecated
     */
    public boolean cmdGetCtl(int addr, int rspMax) {
        return mMeshController.cmdGetCtl(addr, rspMax);
    }


    /**
     * @see #setTime(int, int, long, int, Object)
     * @deprecated
     */
    public void cmdSetTime(int addr, int rspMax, long taiTime, int zoneOffset) {
        TelinkLog.d("taiTime: " + taiTime + " zone: " + zoneOffset);
        mMeshController.cmdSetTime(addr, rspMax, taiTime, zoneOffset);
    }

    /**
     * @see #setSchedulerAction(int, boolean, int, Scheduler, Object)
     * @deprecated
     */
    public void cmdSetScheduler(int addr, int rspMax, long schedulerParam, int sceneId, int ack) {
        mMeshController.cmdSetScheduler(addr, rspMax, schedulerParam, sceneId, ack);
    }

    /**
     * @see #getScheduler(int, int, Object)
     * @deprecated
     */
    public void cmdGetScheduler(int addr, int rspMax, byte index) {
        mMeshController.cmdGetScheduler(addr, rspMax, index);
    }

    /**
     * @see #storeScene(int, boolean, int, int, Object)
     * @deprecated
     */
    public void cmdSceneStore(int adr, int rspMax, int id, int ack) {
        mMeshController.cmdSceneStore(adr, rspMax, id, ack);
    }

    /**
     * @see #deleteScene(int, boolean, int, int, Object)
     * @deprecated
     */
    public void cmdSceneDelete(int adr, int rspMax, int id, int ack) {
        mMeshController.cmdSceneDelete(adr, rspMax, id, ack);
    }

    /**
     * @see #recallScene(int, boolean, int, int, long, byte, Object)
     * @deprecated
     */
    public void cmdSceneRecall(int adr, int rspMax, int id, int ack, byte[] transition) {
        mMeshController.cmdSceneRecall(adr, rspMax, id, ack, transition);
    }

    /**
     * kickOut device
     *
     * @param deviceInfo target device
     * @return if cmd sent
     * @see #resetNode(int, Object)
     * @deprecated
     */
    public boolean kickOut(DeviceInfo deviceInfo) {
        return mMeshController.kickOut(deviceInfo);
    }


}
