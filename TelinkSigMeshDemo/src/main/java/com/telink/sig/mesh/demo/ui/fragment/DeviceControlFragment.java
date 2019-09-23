/********************************************************************************************************
 * @file DeviceControlFragment.java
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
package com.telink.sig.mesh.demo.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.telink.sig.mesh.demo.R;
import com.telink.sig.mesh.demo.TelinkMeshApplication;
import com.telink.sig.mesh.demo.ui.adapter.SwitchListAdapter;
import com.telink.sig.mesh.light.PublicationDataGenerator;
import com.telink.sig.mesh.model.NotificationInfo;
import com.telink.sig.mesh.model.SigMeshModel;
import com.telink.sig.mesh.demo.ui.widget.ColorPanel;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.util.MeshUtils;
import com.telink.sig.mesh.util.TelinkLog;
import com.telink.sig.mesh.util.UnitConvert;

import java.util.List;

/**
 * 设备控制fragment
 * Created by kee on 2017/8/18.
 */

public class DeviceControlFragment extends BaseFragment implements EventListener<String>, View.OnClickListener {
    ColorPanel color_panel;
    View color_presenter;
    ScrollView sv_container;
    private View ll_hsl, ll_lum, ll_lum_level, ll_temp, ll_temp_level;
    DeviceInfo deviceInfo;
    TextView tv_rgb, tv_hsl, tv_lum, tv_temp, tv_lum_level, tv_temp_level;
    SeekBar sb_color, sb_lum, sb_temp;
    RadioGroup rg_mode;
    TextView tvVersion;

    private long preTime;
    private static final int DELAY_TIME = 320;

    private SparseBooleanArray lumEleInfo;
    private SparseBooleanArray tempEleInfo;
    private int hslEleAdr;
    private List<Integer> onOffEleAdrList;
    SwitchListAdapter switchListAdapter;
    int delta = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_control, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        double max = 0xFFFF;
        double stepCnt = 10;

        // 防止精度丢失导致取不到最大值
        delta = (int) Math.ceil(max / stepCnt);
        final int address = getArguments().getInt("address");
        deviceInfo = TelinkMeshApplication.getInstance().getMesh().getDeviceByMeshAddress(address);
        lumEleInfo = deviceInfo.getLumEleInfo();
        tempEleInfo = deviceInfo.getTempEleInfo();
        hslEleAdr = deviceInfo.getTargetEleAdr(SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId);
        onOffEleAdrList = deviceInfo.getOnOffEleAdrList();

        initView(view);
        setVisibility();

//        refreshDesc(color_panel.getColor(), Color.HSVToColor(color_panel.));
        TelinkMeshApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
        TelinkMeshApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_LEVEL_STATUS, this);
        TelinkMeshApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY, this);
        TelinkMeshApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_VERSION, this);
        TelinkMeshApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
    }

    private void initView(View view) {
        RecyclerView rv_switch = view.findViewById(R.id.rv_switch);
        rv_switch.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        switchListAdapter = new SwitchListAdapter(getActivity(), onOffEleAdrList);
        rv_switch.setAdapter(switchListAdapter);
        sv_container = view.findViewById(R.id.sv_container);
        color_panel = view.findViewById(R.id.color_panel);
        color_panel.setOnTouchListener(this.colorPanelTouchListener);
        color_panel.setColorChangeListener(colorChangeListener);
        color_panel.setColor(Color.WHITE);

        color_presenter = view.findViewById(R.id.color_presenter);

        ll_hsl = view.findViewById(R.id.ll_hsl);
        ll_lum = view.findViewById(R.id.ll_lum);
        ll_lum_level = view.findViewById(R.id.ll_lum_level);
        ll_temp = view.findViewById(R.id.ll_temp);
        ll_temp_level = view.findViewById(R.id.ll_temp_level);

        tv_rgb = view.findViewById(R.id.tv_rgb);
        tv_hsl = view.findViewById(R.id.tv_hsl);
        tv_lum = view.findViewById(R.id.tv_lum);
        tv_temp = view.findViewById(R.id.tv_temp);
        tv_lum_level = view.findViewById(R.id.tv_lum_level);
        tv_temp_level = view.findViewById(R.id.tv_temp_level);

        sb_color = view.findViewById(R.id.sb_color);
        sb_lum = view.findViewById(R.id.sb_brightness);
        sb_temp = view.findViewById(R.id.sb_temp);


        sb_lum.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sb_temp.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sb_color.setOnSeekBarChangeListener(onSeekBarChangeListener);

        view.findViewById(R.id.iv_lum_add).setOnClickListener(this);
        view.findViewById(R.id.iv_lum_minus).setOnClickListener(this);
        view.findViewById(R.id.iv_temp_add).setOnClickListener(this);
        view.findViewById(R.id.iv_temp_minus).setOnClickListener(this);

        rg_mode = view.findViewById(R.id.rg_mode);
        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb1:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 3, 500, (byte) 0, null);
                        break;
                    case R.id.rb2:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 4, 500, (byte) 0, null);
                        break;
                    case R.id.rb3:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 5, 500, (byte) 0, null);
                        break;
                    case R.id.rb4:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 6, 500, (byte) 0, null);
                        break;
                    case R.id.rb5:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 8, 500, (byte) 0, null);
                        break;
                    case R.id.rb6:
                        MeshService.getInstance().recallScene(deviceInfo.meshAddress, false, 0, 14, 500, (byte) 0, null);
                        break;
                }
            }
        });

        tvVersion = view.findViewById(R.id.tv_version);
        Button bt_get_version = view.findViewById(R.id.bt_get_version);
        bt_get_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeshService.getInstance().getVersion(deviceInfo.meshAddress,0,null);
            }
        });
    }


    private void setVisibility() {
        if (hslEleAdr == -1) {
            ll_hsl.setVisibility(View.GONE);
        }

        if (lumEleInfo == null) {
            ll_lum.setVisibility(View.INVISIBLE);
        } else {
            tv_lum.setText(getString(R.string.lum_progress, Math.max(1, deviceInfo.lum), Integer.toHexString(lumEleInfo.keyAt(0))));
            sb_lum.setProgress(deviceInfo.lum);
            if (!lumEleInfo.get(lumEleInfo.keyAt(0))) {
                ll_lum_level.setVisibility(View.INVISIBLE);
            } else {
                tv_lum_level.setText(getString(R.string.lum_level, Integer.toHexString(lumEleInfo.keyAt(0))));
            }
        }

        if (tempEleInfo == null) {
            ll_temp.setVisibility(View.INVISIBLE);
        } else {
            tv_temp.setText(getString(R.string.temp_progress, deviceInfo.temp, Integer.toHexString(tempEleInfo.keyAt(0))));
            sb_temp.setProgress(deviceInfo.temp);
            if (!tempEleInfo.get(tempEleInfo.keyAt(0))) {
                ll_temp_level.setVisibility(View.INVISIBLE);
            } else {
                tv_temp_level.setText(getString(R.string.temp_level, Integer.toHexString(tempEleInfo.keyAt(0))));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(this);
    }

    private ColorPanel.ColorChangeListener colorChangeListener = new ColorPanel.ColorChangeListener() {


        @Override
        public void onColorChanged(float[] hsv, boolean touchStopped) {
            int color = Color.HSVToColor(hsv);
            float[] hslValue = new float[3];
            ColorUtils.colorToHSL(color, hslValue);

            refreshDesc(hslValue, color);
            long currentTime = System.currentTimeMillis();
            if ((currentTime - preTime) >= DELAY_TIME || touchStopped) {
                preTime = currentTime;

                /*MeshService.getInstance().cmdSetHSL100(hslEleAdr, (byte) 0,
                        (byte) (hslValue[0] * 100 / 360),
                        (byte) (hslValue[1] * 100),
                        (byte) (hslValue[2] * 100),
                        1, new byte[]{0, 0});*/

                MeshService.getInstance().setHSL100(hslEleAdr, (int) (hslValue[0] * 100 / 360),
                        (int) (hslValue[1] * 100),
                        (int) (hslValue[2] * 100),
                        false, 0, 500, (byte) 0, null);
            } else {
                TelinkLog.w("CMD reject : color set");
            }
        }
    };


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
                onProgressUpdate(seekBar, progress, false);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        private static final String TAG = "DeviceControlFragment";
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onProgressUpdate(seekBar, seekBar.getProgress(), true);
        }

        void onProgressUpdate(SeekBar seekBar, int progress, boolean immediate) {

            if (seekBar == sb_color) {
                float lightness = ((float) progress) / 100;
                if (color_panel != null) {
                    color_panel.refreshLightness(lightness, true);
                }
            } else {
                long currentTime = System.currentTimeMillis();
                if (seekBar == sb_lum) {
                    int lum = Math.max(1, progress);
                    tv_lum.setText(getString(R.string.lum_progress, lum, Integer.toHexString(lumEleInfo.keyAt(0))));
                    if ((currentTime - preTime) >= DELAY_TIME || immediate) {
                        preTime = currentTime;
                        Log.d(TAG, "onProgressUpdate() called with:  progress = [" + lum + "], immediate = [" + immediate + "]");

//                        MeshService.getInstance().cmdSetLum(lumEleInfo.keyAt(0), (byte) (progress));
                        MeshService.getInstance().setLum(lumEleInfo.keyAt(0), lum, false, 0, 500, (byte) 0, null);
                    }
                } else if (seekBar == sb_temp) {
                    tv_temp.setText(getString(R.string.temp_progress, progress, Integer.toHexString(tempEleInfo.keyAt(0))));
                    if ((currentTime - preTime) >= DELAY_TIME || immediate) {
                        preTime = currentTime;
//                        MeshService.getInstance().cmdSetTemp(tempEleInfo.keyAt(0), (byte) progress);
                        MeshService.getInstance().setTemperature100(tempEleInfo.keyAt(0), progress, false, 0, 500, (byte) 0, null);
                    }
                }
            }

            /*if ((currentTime - preTime) >= DELAY_TIME || immediate) {
                preTime = currentTime;
                if (seekBar == sb_color) {
                    float lightness = ((float) progress) / 100;
                    if (color_panel != null) {
                        color_panel.refreshLightness(lightness, true);
                    }
                } else if (seekBar == sb_lum) {
                    MeshService.getInstance().cmdSetLum(lumEleInfo.keyAt(0), (byte) (progress));
                    tv_lum.setText(getString(R.string.lum_progress, Math.max(1, progress), Integer.toHexString(lumEleInfo.keyAt(0))));
                } else if (seekBar == sb_temp) {
                    MeshService.getInstance().cmdSetTemp(tempEleInfo.keyAt(0), (byte) progress);
                    tv_temp.setText(getString(R.string.temp_progress, progress, Integer.toHexString(tempEleInfo.keyAt(0))));
                }
            } else {
                TelinkLog.w("CMD reject: " + progress);
            }*/
        }
    };

    private View.OnTouchListener colorPanelTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                sv_container.requestDisallowInterceptTouchEvent(false);
            } else {
                sv_container.requestDisallowInterceptTouchEvent(true);
            }
            return false;
        }
    };

    private void refreshDesc(float[] hslValue, int color) {

        color_presenter.setBackgroundColor(color);
        String rgb = "RGB: \n\tR -- " + ((color >> 16) & 0xFF) +
                "\n\tG -- " + ((color >> 8) & 0xFF) +
                "\n\tB -- " + ((color) & 0xFF);
        tv_rgb.setText(rgb);

// Hue Saturation Hue
//        float[] hslValue = new float[3];
        String hsl = "HSL: \n\tH -- " + (hslValue[0]) + "(" + (byte) (hslValue[0] * 100 / 360) + ")" +
                "\n\tS -- " + (hslValue[1]) + "(" + (byte) (hslValue[1] * 100) + ")" +
                "\n\tL -- " + (hslValue[2] + "(" + (byte) (hslValue[2] * 100) + ")"
        );
        tv_hsl.setText(hsl);
    }


    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED)) {

        } else if (event.getType().equals(NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY)) {

        } else if (event.getType().equals(NotificationEvent.EVENT_TYPE_DEVICE_LEVEL_STATUS)) {
            TelinkLog.d("device control fragment # EVENT_TYPE_DEVICE_LEVEL_STATUS");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setVisibility();
                }
            });
        } else if (event.getType().equals(NotificationEvent.EVENT_TYPE_DEVICE_VERSION)){
            byte[] data = ((NotificationEvent) event).getNotificationInfo().params;
            final String version = parseVersion(data);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvVersion.setText(version);
                }
            });

        }
    }

    /**
     * 固件版本类型-发布版
     */
    public static final int VERSION_TYPE_RELEASE = 1;
    /**
     * 固件版本类型-测试版
     */
    public static final int VERSION_TYPE_TEST = 2;
    /**
     * 固件版本类型-APP开发版
     */
    public static final int VERSION_TYPE_APP = 3;
    private String parseVersion(byte[] data) {
        int t = data[0] & 0xff;
        int y = data[1] & 0xff;
        int m = data[2] & 0xff;
        int b = data[3] & 0xff;
        int versionMode = (data[4] & 0xff) >> 6;
        int info = data[4] & 0x3f;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("T").append(t).append(".")
                .append(String.format("%02x",y)).append(".")
                .append("M").append(String.format("%02x",m)).append(".")
                .append("B").append(String.format("%03x",b));
        if (versionMode == VERSION_TYPE_RELEASE) {
            stringBuffer.append("_r").append(String.format("%02x",info));
        }else if (versionMode == VERSION_TYPE_TEST){
            stringBuffer.append("_t").append(String.format("%02x",info));
        }else if (versionMode == VERSION_TYPE_APP){
            stringBuffer.append("_a").append(String.format("%02x",info));
        }
        String version = stringBuffer.toString();
        return version;
    }

    @Override
    public void onClick(View v) {
        TelinkLog.d("delta: " + delta);
        switch (v.getId()) {
            case R.id.iv_lum_add:
//                MeshService.getInstance().cmdSetDelta(lumEleInfo.keyAt(0), (byte) 1, delta, 1, null);
                MeshService.getInstance().setDelta(lumEleInfo.keyAt(0), delta, true, 1, 0, (byte) 0, null);
                break;

            case R.id.iv_lum_minus:
                MeshService.getInstance().setDelta(lumEleInfo.keyAt(0), -delta, true, 1, 0, (byte) 0, null);
//                MeshService.getInstance().cmdSetDelta(lumEleInfo.keyAt(0), (byte) 1, -delta, 1, null);
                break;

            case R.id.iv_temp_add:
                MeshService.getInstance().setDelta(tempEleInfo.keyAt(0), delta, true, 1, 0, (byte) 0, null);
//                MeshService.getInstance().cmdSetDelta(tempEleInfo.keyAt(0), (byte) 1, delta, 1, null);
                break;

            case R.id.iv_temp_minus:
                MeshService.getInstance().setDelta(tempEleInfo.keyAt(0), -delta, true, 1, 0, (byte) 0, null);
//                MeshService.getInstance().cmdSetDelta(tempEleInfo.keyAt(0), (byte) 1, -delta, 1, null);
                break;

        }
    }

}
