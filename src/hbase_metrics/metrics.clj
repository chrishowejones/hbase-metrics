(ns hbase-metrics.metrics
  (:require [cascalog.api :refer [?<- stdout hfs-textline div]]
            [cascalog.logic.def :as def]
            [cascalog.logic.ops :as c]
            [clojure.data.csv :as csv]
            [clojure.string :as str]))

(def timestamps
  (line-seq (clojure.java.io/reader "out-file.csv")))

(def/defmapfn timestamp-parser
  "Split the csv line into fields discarding the seqnum"
  [line]
  (map #(Long/parseLong (.trim %)) (rest (first (csv/read-csv line)))))

(def/defmapfn latency [msg-timestamp hbase-timestamp]
  (- msg-timestamp hbase-timestamp))

(def/defmapfn timestamp->bucket [msg-timestamp]
  (int (/ msg-timestamp 5000)))

(def/defbufferfn total-latency [tuples]
  [(reduce + (map first tuples))])

;; TODO add an additional field on input to represent latency
;; TODO calculate the interval 'bucket' time
;; TODO group by interval 'bucket' time

(defn run-query [file interval]
  (let [timestamp-tap (hfs-textline file)]
    (?<-
     (stdout)
     [?bucket ?avg-latency]
     (timestamp-tap :> ?line)
     (timestamp-parser :< ?line :> ?msg-timestamp ?hbase-timestamp)
     (- ?msg-timestamp ?hbase-timestamp :> ?msg-latency)
     (div ?msg-timestamp interval :> ?bucket)
     (c/avg ?msg-latency :> ?avg-latency))))



(comment

    (div ?msg-timestamp 5000 :> ?bucket)
    (c/avg ?msg-latency :> ?avg-latency)



  (run-query "out-file.csv" 5000)

  (def a ["123,456,789" "111,222,333"])

  (map #(str/split % #",") a)
  (doc str/split)

  )

(comment
                   [org.apache.hadoop/hadoop-client "2.7.1.2.3.0.0-2557"
                  :exclusions [[org.slf4j/slf4j-log4j12]]]


                   )
