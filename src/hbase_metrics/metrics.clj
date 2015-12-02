(ns hbase-metrics.metrics
  (:require [cascalog.api :refer [?<- stdout hfs-textline div]]
            [cascalog.logic.def :as def]
            [cascalog.logic.ops :as c]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clj-time.core :as t]
            [clj-time.format :as f]))


(def/defmapfn timestamp-parser
  "Split the csv line into fields discarding the seqnum"
  [line]
  (map #(Long/parseLong (.trim %)) (rest (first (csv/read-csv line)))))

(def/defmapfn latency [msg-timestamp hbase-timestamp]
  (- msg-timestamp hbase-timestamp))

(def/defmapfn timestamp->bucket [interval msg-ts]
    (int (/ msg-ts interval)))

(def/defbufferfn total-latency [tuples]
  [(reduce + (map first tuples))])

(defn out-filename
  ([] (out-filename "metrics"))
  ([filepath] (str filepath "-" (f/unparse (f/formatters :basic-date-time-no-ms) (t/now)))))

(defn run-query [in-filepath interval out-filepath]
  (let [timestamp-tap (hfs-textline in-filepath)]
    (?<-
     (hfs-textline (out-filename out-filepath))
     [?bucket ?avg-latency ?avg-storm-latency ?avg-stormhbase-latency]
     (timestamp-tap :> ?line)
     (timestamp-parser :< ?line :> ?msg-timestamp ?storm-timestamp ?stormhbase-timestamp ?hbase-timestamp)
     (- ?hbase-timestamp ?msg-timestamp :> ?msg-latency)
     (- ?storm-timestamp ?msg-timestamp :> ?storm-latency)
     (- ?stormhbase-timestamp ?msg-timestamp :> ?stormhbase-latency)
     (timestamp->bucket interval ?msg-timestamp :> ?bucket)
     (c/avg ?msg-latency :> ?avg-latency)
     (c/avg ?storm-latency :> ?avg-storm-latency)
     (c/avg ?stormhbase-latency :> ?avg-stormhbase-latency))))


(comment

  (run-query "../query-txn-hbase/out-file.csv" 5000 "output")

  (def a ["123,456,789" "111,222,333"])

  (map #(str/split % #",") a)
  (doc str/split)

  )
