(ns hbase-metrics.core
  (require [hbase-metrics.metrics :refer [run-query]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-query (first args) (Long/parseLong (second args))))
