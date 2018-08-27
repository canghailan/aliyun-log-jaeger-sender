package com.aliyun.openservices.log.jaeger.sender;

import com.aliyun.openservices.log.common.LogItem;
import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.References;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class LogItemSpanConverter {

  public static LogItem convertSpan(JaegerSpan span) {
    JaegerSpanContext context = span.context();

    boolean oneChildOfParent = span.getReferences().size() == 1
        && References.CHILD_OF.equals(span.getReferences().get(0).getType());

    LogItem logItem = new LogItem();
    logItem.PushBack(Constants.TRACE_ID, Long.toHexString(context.getTraceId()));
    logItem.PushBack(Constants.SPAN_ID, Long.toHexString(context.getSpanId()));
    logItem.PushBack(Constants.PARENT_SPAN_ID, oneChildOfParent ? Long.toHexString(context.getParentId()) : "0");
    logItem.PushBack(Constants.OPERATION_NAME, span.getOperationName());
    logItem.PushBack(Constants.FLAGS, Long.toHexString(context.getFlags()));
    logItem.PushBack(Constants.START_TIME, String.valueOf(TimeUnit.MICROSECONDS.toNanos(span.getStart())));
    logItem.PushBack(Constants.DURATION, String.valueOf(TimeUnit.MICROSECONDS.toNanos(span.getDuration())));

    // process
    logItem.PushBack(Constants.PROCESS + "." + Constants.SERVICE_NAME, span.getServiceName());
    for (Map.Entry<String, ?> e : span.getTracer().tags().entrySet()) {
      logItem.PushBack(Constants.PROCESS + ".tags."  + e.getKey(), String.valueOf(e.getValue()));
    }

    // tags
    for (Map.Entry<String, ?> e : span.getTags().entrySet()) {
      logItem.PushBack(Constants.TAGS + "."  + e.getKey(), String.valueOf(e.getValue()));
    }

//    logItem.PushBack(Constants.REFERENCES, buildReferences(span.getReferences()).toJSONString());
//    logItem.PushBack(Constants.TAGS, buildTags(span.getTags()).toJSONString());
//    logItem.PushBack(Constants.LOGS, buildLogs(span.getLogs()).toJSONString());
//    logItem.PushBack(Constants.PROCESS, buildProcess(span).toJSONString());

    return logItem;
  }

//  static JSONObject buildTags(Map<String, ?> tags) {
//    JSONObject jsonTags = new JSONObject();
//    if (tags != null) {
//      for (Map.Entry<String, ?> entry : tags.entrySet()) {
//        jsonTags.put(entry.getKey(), entry.getValue());
//      }
//    }
//    return jsonTags;
//  }
//
//  static JSONObject buildProcess(JaegerSpan span) {
//    JSONObject jsonProcess = new JSONObject();
//    if (span != null) {
//      jsonProcess.put(Constants.SERVICE_NAME, span.getServiceName());
//
//      Map<String, ?> tags = span.getTracer().tags();
//      JSONObject jsonTags = buildTags(tags);
//      jsonProcess.put(Constants.TAGS, jsonTags);
//    }
//    return jsonProcess;
//  }
//
//  static JSONArray buildReferences(List<Reference> references) {
//    JSONArray jsonRefs = new JSONArray();
//    if (references != null) {
//      for (Reference reference : references) {
//        JSONObject jsonRef = new JSONObject();
//        jsonRef.put(Constants.REF_TYPE, reference.getType());
//        jsonRef
//            .put(Constants.TRACE_ID, String.format("%x", reference.getSpanContext().getTraceId()));
//        jsonRef.put(Constants.SPAN_ID, String.format("%x", reference.getSpanContext().getSpanId()));
//        jsonRefs.add(jsonRef);
//      }
//    }
//    return jsonRefs;
//  }
//
//  static JSONArray buildLogs(List<LogData> logs) {
//    JSONArray jsonLogs = new JSONArray();
//    if (logs != null) {
//      for (LogData logData : logs) {
//        JSONObject jsonLog = new JSONObject();
//        jsonLog.put(Constants.TIMESTAMP, logData.getTime());
//        if (logData.getFields() != null) {
//          jsonLog.put(Constants.TAGS, buildTags(logData.getFields()));
//        } else {
//          JSONObject tags = new JSONObject();
//          if (logData.getMessage() != null) {
//            tags.put(Constants.EVENT, logData.getMessage());
//          }
//          jsonLog.put(Constants.TAGS, tags);
//        }
//        jsonLogs.add(jsonLog);
//      }
//    }
//    return jsonLogs;
//  }
}
