(defproject dwc "0.0.35"
  :description "Simple (and rather limited) reader/consumer for DwC-A, DIGIR, TAPIR, CSV and xlsx resources, local or online."
  :url "http://github.com/CNCFlora/dwc"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/data.json "0.2.5"]
                 [dk.ative/docjure "1.6.0"]
                 [bigml/closchema "0.5"]
                 [diogok/cljts "0.4.3"]
                 [clj-http "0.7.6"]]
  :jvm-opts ["-Xmx4G" "-server"] 
  :repositories [["clojars" {:sign-releases false}]]
  :profiles {:uberjar {:aot :all}
             :jar {:aot :all}
             :dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
