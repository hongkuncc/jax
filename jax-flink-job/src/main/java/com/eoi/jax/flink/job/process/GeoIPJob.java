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

package com.eoi.jax.flink.job.process;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;


@Job(
        name = "GeoIPJob",
        display = "IP地址反查地理信息",
        description = "通过IPv4地址查询地理信息，可输出国家、省、市、乡镇、运营商",
        icon = "GeoIPJob.svg",
        doc = "GeoIPJob.md",
        category = OperatorCategory.TEXT_ANALYSIS
)
public class GeoIPJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        GeoIPJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            GeoIPJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        return dataStream.map(new GeoIPFunction(config))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());

    }

    @Override
    public GeoIPJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        GeoIPJobConfig config = new GeoIPJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
