(ns hbase-metrics.core
  (require [hbase-metrics.metrics :refer [run-query]])
  (:gen-class))

(defn -main
  "Run query over hbase timestamps to get average latency over a specified interval in ms.
  Usage: hadoop jar hbase-metrics-<version>-standalone.jar <input HDFS
  file path> <sample interval in ms> <optional: output HDFS file path,
  defaults to /metrics>"
  [input-filepath interval output-filepath]
  (run-query input-filepath (Long/parseLong interval) output-filepath))
