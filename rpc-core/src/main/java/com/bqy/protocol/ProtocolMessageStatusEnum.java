package com.bqy.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",60);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text,int value){
        this.text = text;
        this.value = value;
    }
    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for(ProtocolMessageStatusEnum aEnum:ProtocolMessageStatusEnum.values()){
            if(value==aEnum.getValue()){
                return aEnum;
            }
        }
        return null;
    }


}
