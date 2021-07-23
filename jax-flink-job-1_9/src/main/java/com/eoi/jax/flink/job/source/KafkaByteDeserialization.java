/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.flink.job.source;

import cn.hutool.core.bean.BeanUtil;
import com.eoi.jax.flink.job.common.KafkaSourceMetaData;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class KafkaByteDeserialization implements KafkaDeserializationSchema<Map<String,Object>> {

    private String byteArrayFieldName;
    private String metaFieldName;

    public KafkaByteDeserialization(String byteArrayFieldName, String metaFieldName) {
        this.byteArrayFieldName = byteArrayFieldName;
        this.metaFieldName = metaFieldName;
    }

    @Override
    public boolean isEndOfStream(Map<String,Object> nextElement) {
        return false;
    }

    @Override
    public Map<String,Object> deserialize(ConsumerRecord<byte[],byte[]> record) {
        Map<String,Object> msg = new HashMap<>();
        msg.put(byteArrayFieldName, record.value());

        KafkaSourceMetaData metaData = new KafkaSourceMetaData();
        metaData.setOffset(record.offset());
        metaData.setPartition(record.partition());
        metaData.setTimestamp(record.timestamp());
        metaData.setTopic(record.topic());
        if (record.key() != null) {
            try {
                metaData.setKey(new String(record.key(), Charset.forName("UTF-8")));
            } catch (Exception ignore) {
                metaData.setKeyBytes(record.key());
            }
        }
        msg.put(metaFieldName, BeanUtil.beanToMap(metaData));

        return msg;
    }

    @Override
    public TypeInformation<Map<String,Object>> getProducedType() {
        return TypeInformation.of(new TypeHint<Map<String,Object>>(){});
    }
}
