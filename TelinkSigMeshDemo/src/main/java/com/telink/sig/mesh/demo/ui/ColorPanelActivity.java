/********************************************************************************************************
 * @file     ColorPanelActivity.java 
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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.telink.sig.mesh.demo.R;
import com.telink.sig.mesh.demo.ui.widget.ColorPanel;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.util.TelinkLog;

/**
 * Created by kee on 2018/8/28.
 */

public class ColorPanelActivity extends BaseActivity {
    ColorPanel color_panel;
    View color_presenter;
    int address;
    TextView tv_rgb, tv_hsl;
    SeekBar sb_lightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_panel);
        enableBackNav(true);
        setTitle("HSL");
        color_panel = findViewById(R.id.color_panel);
        color_panel.setColorChangeListener(colorChangeListener);
        color_panel.setColor(Color.WHITE);

        color_presenter = findViewById(R.id.color_presenter);
        address = getIntent().getIntExtra("address", 0);

        tv_rgb = findViewById(R.id.tv_rgb);
        tv_hsl = findViewById(R.id.tv_hsl);
        sb_lightness = findViewById(R.id.sb_lightness);
        sb_lightness.setOnSeekBarChangeListener(onSeekBarChangeListener);
        refreshDesc(color_panel.getColor());
    }

    private ColorPanel.ColorChangeListener colorChangeListener = new ColorPanel.ColorChangeListener() {

        private long preTime;
        private static final int DELAY_TIME = 320;


        @Override
        public void onColorChanged(float[] hsv, boolean touchStopped) {

            color_presenter.setBackgroundColor(Color.HSVToColor(hsv));
            refreshDesc(hsv);
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.preTime) >= DELAY_TIME || touchStopped) {
                this.preTime = currentTime;

                /*MeshService.getInstance().cmdSetHSL100(address, (byte) 0,
                        (byte) (hsv[0] * 100 / 360),
                        (byte) (hsv[1] * 100),
                        (byte) (hsv[2] * 100),
                        1, new byte[]{0, 0});*/

                MeshService.getInstance().setHSL100(address,(int)(hsv[0] * 100 / 360),
                        (int)(hsv[1] * 100),
                        (int)(hsv[2] * 100),
                        false, 0, 0 , (byte)0, null);
            } else {
                TelinkLog.w("CMD reject : color set");
            }


        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float lightness = ((float) progress) / 100;
            if (color_panel != null) {
                color_panel.refreshLightness(lightness, false);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            float lightness = ((float) seekBar.getProgress()) / 100;
            if (color_panel != null) {
                color_panel.refreshLightness(lightness, true);
            }
        }
    };

    private void refreshDesc(float[] hslValue) {
        int color = Color.HSVToColor(hslValue);
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
}
