package com.telink.sig.mesh.model;

import com.telink.sig.mesh.model.message.config.PubSetMessage;

import java.io.Serializable;

/**
 * @see com.telink.sig.mesh.model.message.config.PubSetMessage
 * Created by kee on 2018/12/18.
 */

public class PublishModel {
    //元素地址
    public int elementAddress;
    //模型id
    public int modelId;

    //地址
    public int address;

//    public byte[] params;

    //周期
    public int period;

    //消息跳转次数
    public int ttl;

    //证书
    public int credential;

    //传输
    public int transmit;


    public PublishModel(int elementAddress, int modelId, int address, int period) {
        this(elementAddress, modelId, address, period, PubSetMessage.TTL_DEFAULT, PubSetMessage.CREDENTIAL_FLAG_DEFAULT,
                (byte) ((PubSetMessage.RETRANSMIT_COUNT_DEFAULT & 0b111) | (PubSetMessage.RETRANSMIT_INTERVAL_STEP_DEFAULT << 3)));
    }


    public PublishModel(int elementAddress, int modelId, int address, int period, int ttl, int credential, int transmit) {
        this.elementAddress = elementAddress;
        this.modelId = modelId;
        this.address = address;
        this.period = period;
        this.ttl = ttl;
        this.credential = credential;
        this.transmit = transmit;
    }

    // 高5
    public int getTransmitInterval() {
        return (transmit & 0xFF) >> 3;
    }

    // 低3
    public int getTransmitCount() {
        return (transmit & 0xFF) & 0b111;
    }


}
