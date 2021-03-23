"# Spring batch + spring cloud dataflow" 

> task.server_task=maven://com.jump:config-server:4.0.0.00.000-SNAPSHOT
> source.server_source=maven://com.jump:config-server:4.0.0.00.000-SNAPSHOT
> processor.worker_processor=maven://com.jump:worker-service:4.0.0.00.000-SNAPSHOT


task.server_task=maven://com.jump:config-server:4.0.0.00.000-SNAPSHOT
source.server_source=maven://com.jump:config-server:4.0.0.00.000-SNAPSHOT
processor.worker_processor=maven://com.jump:worker-service:4.0.0.00.000-SNAPSHOT
sink.worker_sink=maven://com.jump:worker-service:4.0.0.00.000-SNAPSHOT