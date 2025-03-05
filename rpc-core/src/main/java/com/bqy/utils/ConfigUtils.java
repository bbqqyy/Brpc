package com.bqy.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.bqy.config.RpcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    public static <T> T loadConfig(Class<T> tClass,String prefix,String environment){
        StringBuilder str = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            str.append("-").append(environment);
        }
        str.append(".properties");
        Props props = new Props(str.toString());
        props.autoLoad(true);
        return props.toBean(tClass,prefix);
    }
    public static RpcConfig loadConfigYml(String environment)throws IOException {
        StringBuilder str = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            str.append("-").append(environment);
        }
        str.append(".yml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(str.toString()), RpcConfig.class);
    }
    public static RpcConfig loadConfigYml() throws IOException{
        return loadConfigYml("");
    }
    public static <T> T loadConfig(Class<T> tClass,String prefix){
        return loadConfig(tClass,prefix,"");
    }
}
