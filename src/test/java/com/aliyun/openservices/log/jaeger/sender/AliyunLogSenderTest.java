package com.aliyun.openservices.log.jaeger.sender;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Span;
import java.util.HashMap;
import java.util.Map;

import io.opentracing.Tracer;
import org.junit.Before;
import org.junit.Test;


public class AliyunLogSenderTest {

  private AliyunLogSender aliyunLogSender;

  private Tracer tracer;

  @Before
  public void setUp() {
    aliyunLogSender = buildAliyunLogSender();
    RemoteReporter remoteReporter = new RemoteReporter.Builder()
        .withSender(aliyunLogSender)
        .build();
    tracer = new JaegerTracer.Builder("test-service-name")
        .withReporter(remoteReporter)
        .withSampler(new ConstSampler(true))
        .build();
  }

  @Test
  public void testAppend() throws Exception {
    Map<String, Object> fields = new HashMap<String, Object>();
    fields.put("k", "v");

    Span span = tracer.buildSpan("operation-name").start();
    span = span.log(1, fields);
    span = span.setBaggageItem("foo", "bar");
    span = span.setTag("num", 10);
    span = span.setTag("str", "abc");
    span = span.setTag("bool", true);

    aliyunLogSender.append((JaegerSpan) span);
    aliyunLogSender.flush();
    aliyunLogSender.close();
  }

  @Test
  public void testAppendSimpleSpan() throws Exception {
    Span span = tracer.buildSpan("operation-name").start();
    aliyunLogSender.append((JaegerSpan) span);
    aliyunLogSender.flush();
    aliyunLogSender.close();
  }

  private AliyunLogSender buildAliyunLogSender() {
    String projectName = System.getenv("PROJECT");
    String logStore = System.getenv("LOG_STORE");
    String endpoint = System.getenv("ENDPOINT");
    String accessKeyId = System.getenv("ACCESS_KEY_ID");
    String accessKey = System.getenv("ACCESS_KEY_SECRET");
    return new AliyunLogSender.Builder(projectName, logStore, endpoint, accessKeyId, accessKey)
        .withTopic("test_topic").build();
  }
}
