(ns dwc.archive
  (:use clojure.java.io)
  (:use clojure.data.csv)
  (:use clojure.data.xml)
  (:import [java.util.zip ZipFile]))

(defn download
  "Download given URL zip into system temp dir and return the File"
  [url]
  (let [path (str (System/getProperty "java.io.tmpdir") "/" (hash url))]
    (if-not (.exists (file path))
      (do
        (.createNewFile (file path))
        (copy (input-stream (as-url url)) (file path))))
    (file path)))

(defn term2name
  "Takes a DwC term and turn it into a nice name keyword"
  [term] (keyword (.substring term 
                   (inc (.lastIndexOf term "/")))))

(defn get-core
  "Find the 'core' tag of a DwC-A"
  [zip]
  (let [metaxml (parse (reader 
                  (.getInputStream zip (.getEntry zip "/meta.xml"))))]
    (first (filter #(= :core (:tag %)) (:content metaxml)))))

(defn get-char
  "Transform the separator/quote csv char properly"
  [sep]
  (if (= "\\t" sep) \tab 
    (if (= "" sep) \u0000  
    (if (> (.length sep) 0) (.charAt sep 0)))))

(defn get-config
  "Get the configuration of the csv (separator, quote and if should ignore first line)"
  [core] 
  {:ignoreFirst (= "1" (get-in core [:attrs :ignoreHeaderLines])) 
   :separator   (get-char (get-in core [:attrs :fieldsTerminatedBy]))
   :quote   (get-char (get-in core [:attrs :fieldsEnclosedBy]))})

(defn get-file
  "Get the filename of the occurrences csv"
  [core] 
   (first (:content (first (:content (first (:content core)))))))

(defn get-fields
  "Get the fields of the occurrences csv"
  [core]
   (map #(hash-map :index (Integer/parseInt (:index %)) :name  (term2name (:term %)))
        (map :attrs (filter #(= :field (:tag %)) (:content core)))))

(defn read-archive-stream
  "Read the DwC-A zip as a stream, passing each occurrence to your function"
  [url fun] 
  (let [zip (ZipFile. (download url))
        core (get-core zip)
        file (get-file core)
        config (get-config core)
        fields  (get-fields core)]
    (with-open [incsv (reader (.getInputStream zip (.getEntry zip (str "/" file))))]
      (let [csv    (read-csv incsv :separator (:separator config) :quote (:quote config))
            lines  (if (:ignoreFirst config) (rest csv) csv)]
        (doseq [line lines]
         (fun
          (apply hash-map
            (flatten
              (for [field fields]
               [(:name field) (get line (:index field))])))))))))

(defn read-archive
  "Read the DwC-A as a whole returning all occurrences as a vector"
  [url]
  (let [occs (atom [])]
    (read-archive-stream url
      (fn [occ] 
        (swap! occs conj occ)))
    (deref occs)))

