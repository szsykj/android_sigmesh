/********************************************************************************************************
 * @file     DeviceOtaActivity.java 
 *
 * @brief    for TLSR chips
 *
 * @author	 telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
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
package com.telink.sig.mesh.demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.telink.sig.mesh.demo.FileSystem;
import com.telink.sig.mesh.demo.R;
import com.telink.sig.mesh.demo.TelinkMeshApplication;
import com.telink.sig.mesh.demo.ui.file.FileSelectActivity;
import com.telink.sig.mesh.demo.ui.file.ZipUtil;
import com.telink.sig.mesh.demo.ui.fragment.ShareExportFragment;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.OtaEvent;
import com.telink.sig.mesh.event.ScanEvent;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.OtaDeviceInfo;
import com.telink.sig.mesh.util.TelinkLog;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 */

public class DeviceOtaActivity extends BaseActivity implements View.OnClickListener, EventListener<String> {

//    private Button btn_back;

    private TextView selectFile, info, progress, tv_version_info;
    private Button startOta;

    //    private String mPath;
    private byte[] mFirmware;
    private String mac;

    private final static int REQUEST_CODE_GET_FILE = 1;
    private final static int MSG_PROGRESS = 11;
    private final static int MSG_INFO = 12;


    private Handler mInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_PROGRESS) {
                progress.setText(msg.obj + "%");
            } else if (msg.what == MSG_INFO) {
                info.append("\n" + msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ota);
        setTitle("OTA");
        enableBackNav(true);
        initViews();

        Intent intent = getIntent();
        if (intent.hasExtra("mac")) {
            mac = intent.getStringExtra("mac");
        } else {
            toastMsg("device error");
            finish();
            return;
        }
        TelinkMeshApplication.getInstance().addEventListener(OtaEvent.EVENT_TYPE_OTA_SUCCESS, this);
        TelinkMeshApplication.getInstance().addEventListener(OtaEvent.EVENT_TYPE_OTA_PROGRESS, this);
        TelinkMeshApplication.getInstance().addEventListener(OtaEvent.EVENT_TYPE_OTA_FAIL, this);
        TelinkMeshApplication.getInstance().addEventListener(ScanEvent.SCAN_TIMEOUT, this);

        MeshService.getInstance().idle(false);
    }


    private void initViews() {
        selectFile = (TextView) findViewById(R.id.selectFile);
        info = (TextView) findViewById(R.id.log);
        progress = (TextView) findViewById(R.id.progress);
        tv_version_info = (TextView) findViewById(R.id.tv_version_info);
        tv_version_info.setText(getString(R.string.version, "null"));

        startOta = (Button) findViewById(R.id.startOta);

        selectFile.setOnClickListener(this);
        startOta.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.startOta:



                info.setText("start OTA");

                progress.setText("");

//                InputStream is = getResources().openRawResource(R.raw.t04);
//                int length = 0;
//                try {
//                    length = is.available();
//                    byte[] mFirmware = new byte[length];
//                    is.read(mFirmware);
//                    is.close();
                    if (mFirmware == null) {
//                    toastMsg("firmware error");
                        toastMsg("select firmware!");
                        return;
                    }
                    MeshService.getInstance().startOta(mac, mFirmware);
                    startOta.setEnabled(false);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


//                mDevice.startOta(firmware);
                break;

            case R.id.selectFile:
                startActivityForResult(new Intent(this, FileSelectActivity.class).putExtra(FileSelectActivity.FILE_SUFFIX, ".zip"), REQUEST_CODE_GET_FILE);
                break;
        }
    }

    private void onOtaComplete(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startOta.setEnabled(true);
            }
        });
    }

    @Override
    public void performed(final Event<String> event) {
        super.performed(event);
        switch (event.getType()) {
            case OtaEvent.EVENT_TYPE_OTA_SUCCESS:
                MeshService.getInstance().idle(false);
                mInfoHandler.obtainMessage(MSG_INFO, "OTA_SUCCESS").sendToTarget();
                onOtaComplete();
                break;

            case OtaEvent.EVENT_TYPE_OTA_FAIL:
                MeshService.getInstance().idle(true);
                mInfoHandler.obtainMessage(MSG_INFO, "OTA_FAIL").sendToTarget();
                onOtaComplete();
                break;


            case OtaEvent.EVENT_TYPE_OTA_PROGRESS:
                OtaDeviceInfo deviceInfo = ((OtaEvent) event).getDeviceInfo();
                mInfoHandler.obtainMessage(MSG_PROGRESS, deviceInfo.progress).sendToTarget();
                break;


            case ScanEvent.SCAN_TIMEOUT:
                mInfoHandler.obtainMessage(MSG_INFO, "SCAN TIMEOUT").sendToTarget();
                MeshService.getInstance().idle(true);
                onOtaComplete();
                break;

            case MeshEvent.EVENT_TYPE_DISCONNECTED:
                mInfoHandler.obtainMessage(MSG_INFO, "DISCONNECTED").sendToTarget();
                break;
        }

    }

    private void readFirmware(String fileName) {
        try {
            InputStream stream = new FileInputStream(fileName);
            int length = stream.available();
            mFirmware = new byte[length];
            stream.read(mFirmware);
            stream.close();

            byte[] version = new byte[4];
            System.arraycopy(mFirmware, 2, version, 0, 4);
            String firmVersion = new String(version);
            tv_version_info.setText(getString(R.string.version, firmVersion));
            selectFile.setText(fileName.replace(".bin",".zip"));
            new File(fileName).delete();
        } catch (IOException e) {
            e.printStackTrace();
            mFirmware = null;
            tv_version_info.setText(getString(R.string.version, "null"));
            selectFile.setText("file error");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || requestCode != REQUEST_CODE_GET_FILE)
            return;



        try {
            String mPath = data.getStringExtra("path");
            String fileName = new File(mPath).getName().replace(".zip","");
            String destPath = FileSystem.getSettingPath().getAbsolutePath();
            String destFilePath = destPath + File.separator +fileName+ ".bin";
            ZipUtil.unZip(mPath,destPath, "nvccloudiot");
            TelinkLog.d("select: " + mPath);
//        File f = new File(mPath);
            readFirmware(destFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
