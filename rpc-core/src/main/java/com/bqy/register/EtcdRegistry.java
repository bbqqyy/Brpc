package com.bqy.register;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.bqy.config.RegistryConfig;
import com.bqy.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.api.Event;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {
    private Client client;
    private KV kvClient;

    private static String KV_ROOT_PATH = "/rpc/";
    private static Set<String> localRegistryNodeKeySet = new HashSet<>();
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *",new Task(){

            @Override
            public void execute() {
                for(String key:localRegistryNodeKeySet){
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key,StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value,ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        long leaseId = leaseClient.grant(30).get().getID();
        String registerKey = KV_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        localRegistryNodeKeySet.add(registerKey);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = KV_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(KV_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        localRegistryNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList!=null){
            return cachedServiceMetaInfoList;
        }
        String searchPrefix = KV_ROOT_PATH + serviceKey + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix,StandardCharsets.UTF_8),
                    getOption)
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                String key = keyValue.getKey().toString();
                watch(key);
                String value = keyValue.getValue().toString();
                return JSONUtil.toBean(value,ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for(String key:localRegistryNodeKeySet){
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(key+"节点下线失败");
            }

        }
        if(kvClient!=null){
            kvClient.close();
        }
        if (client!=null){
            client.close();
        }
    }

    @Override
    public void watch(String key) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(key);
        if(newWatch){
            watchClient.watch(ByteSequence.from(key,StandardCharsets.UTF_8),watchResponse -> {
                for(WatchEvent watchEvent:watchResponse.getEvents()){
                    switch (watchEvent.getEventType()){
                        case DELETE:
                            localRegistryNodeKeySet.clear();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
