package com.igniubi.core.zipkin.instrument.redis;

import brave.Tracer;
;
import com.igniubi.core.zipkin.instrument.TraceEnumKeys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnBean(Tracer.class)
public class RedisTraceAutoConfig {

    public static class InnerNested {
        @Bean
        @ConditionalOnClass(name="com.igniubi.redis.operations.RedisValueOperations")
        public TraceRedisAspect traceRedisAspect(Tracer tracer, TraceEnumKeys traceKeys) {
            return new TraceRedisAspect(tracer, traceKeys);
        }

    }

}
