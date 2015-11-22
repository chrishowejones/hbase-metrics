(defproject hbase-metrics "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [clj-time "0.11.0"]
                 [cascalog "2.1.1"]
                 [clj-time "0.11.0"]]
  :main ^:skip-aot hbase-metrics.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.apache.hadoop/hadoop-client "2.7.1"]]}
             :provided {:dependencies [[org.apache.hadoop/hadoop-client "2.7.1"]]}})
