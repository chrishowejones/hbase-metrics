(ns hbase-metrics.metrics
  (:require [cascalog.api :refer [?<- stdout hfs-textline]]
            [cascalog.logic.def :as def]
            [clojure.data.csv :as csv]
            [clojure.string :as str]))

(def timestamps
  (line-seq (clojure.java.io/reader "out-file.csv")))

(def/defmapfn timestamp-parser [line]
  (map #(Long/parseLong (.trim %)) (rest (first (csv/read-csv line)))))

(def/defmapfn latency [msg-timestamp hbase-timestamp]
  (- msg-timestamp hbase-timestamp))

;; TODO add an additional field on input to represent latency
;; TODO calculate the interval 'bucket' time
;; TODO group by interval 'bucket' time

(?<-
 (stdout)
 [?msg-timestamp ?hbase-timestamp ?msg-latency]
 (timestamps :> ?line)
 (timestamp-parser :< ?line :> ?msg-timestamp ?hbase-timestamp)
 (latency :< ?msg-timestamp ?hbase-timestamp :> ?msg-latency))


(comment

  (def a ["123,456,789" "111,222,333"])

  (map #(str/split % #",") a)
  (doc str/split)

  )

(comment
                   [org.apache.hadoop/hadoop-client "2.7.1.2.3.0.0-2557"
                  :exclusions [[org.slf4j/slf4j-log4j12]]]


                   )
