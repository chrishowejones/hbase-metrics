(ns hbase-metrics.core
  (require [hbase-metrics.metrics :refer [run-query]]
           [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [["-f" "--file FILE" "input file path" :default "out-file.csv"]
   ["-i" "--interval INTERVAL" "sample interval over which a mean of latency will be taken (in ms)"
    :parse-fn #(Long/parseLong %)
    :default 5000]
   ["-o" "--output OUTPUT" "output directory path" :default "metrics"]
   ["-h" "--help" "Displays this help dialog"]])

(defn- display-help [summary]
  (println summary)
  (System/exit 0))

(defn execute-query
  [{:keys [file interval output]}]
  (run-query file interval output))

(defn -main
  "Run query over hbase timestamps to get average latency over a specified interval in ms.
  Usage: hadoop jar hbase-metrics-<version>-standalone.jar <input HDFS
  file path> <sample interval in ms> <optional: output HDFS file path,
  defaults to /metrics>"
  [& args]
  (let [{:keys [options summary errors]} (parse-opts args cli-options)]
    (when options
      (if (:help options)
        (display-help summary)
        (execute-query options)))))
