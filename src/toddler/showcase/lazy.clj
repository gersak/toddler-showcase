(ns toddler.showcase.lazy
  (:require
   [clojure.string :as str]
   [clojure.data.json :as json]))

(def files
  ["al.json"
   "an.json"
   "au.json"
   "ax.json"
   "be.json"
   "bk.json"
   "bo.json"
   "bu.json"
   "cy.json"
   "da.json"
   "dx.json"
   "ee.json"
   "ei.json"
   "en.json"
   "ez.json"
   "fi.json"
   "fo.json"
   "fr.json"
   "gi.json"
   "gk.json"
   "gm.json"
   "gr.json"
   "hr.json"
   "hu.json"
   "ic.json"
   "im.json"
   "it.json"
   "je.json"
   "jn.json"
   "kv.json"
   "lg.json"
   "lh.json"
   "lo.json"
   "ls.json"
   "lu.json"
   "md.json"
   "mj.json"
   "mk.json"
   "mn.json"
   "mt.json"
   "nl.json"
   "no.json"
   "pl.json"
   "po.json"
   "ri.json"
   "ro.json"
   "si.json"
   "sm.json"
   "sp.json"
   "sv.json"
   "sw.json"
   "sz.json"
   "uk.json"
   "up.json"
   "vt.json"])

(defn ->float
  [text]
  (when (not-empty text)
    (when-let [n (re-find #"[\d\.]+(?=\s*)" text)]
      (Float/parseFloat n))))

(defn ->int
  [text]
  (when (not-empty text)
    (when-let [n (re-find #"[\d,]+(?=\s*)" text)]
      (Integer/parseInt (str/replace n #"," "")))))

(defn read-stats [file]
  (json/read-str (slurp (str (System/getProperty "user.home") "/dev/factbook.json/europe/" file))))

(defn country [stats] (get-in stats ["Government" "Country name" "conventional short form" "text"]))

(defn fertility-rate [stats]
  (when-let [n (get-in stats ["People and Society" "Total fertility rate" "text"])]
    (->float n)))

(defn gross-reproduction-rate
  [stats]
  (when-let [n (get-in stats ["People and Society" "Gross reproduction rate" "text"])]
    (->float n)))

(defn median-age [stats]
  (let [{{total "text"} "total"
         {male "text"} "male"
         {female "text"} "female"} (get-in stats ["People and Society" "Median age"])]
    {:total (->float total)
     :male (->float male)
     :female (->float female)}))

(defn population [stats]
  (let [{{total "text"} "total"
         {male "text"} "male"
         {female "text"} "female"} (get-in stats ["People and Society" "Population"])]
    {:total (->int total)
     :male (->int male)
     :female (->int female)}))

(defn alcohol-use [stats]
  (let [{{total "text"} "total"
         {beer "text"} "beer"
         {wine "text"} "wine"
         {spirits "text"} "spirits"
         {other "text"} "other alcohols"} (get-in stats ["People and Society" "Alcohol consumption per capita"])]
    {:total (->float total)
     :beer (->float beer)
     :wine (->float wine)
     :spirits (->float spirits)
     :other (->float other)}))

(defn tobacco-use [stats]
  (let [{{total "text"} "total"
         {male "text"} "male"
         {female "text"} "female"} (get-in stats ["People and Society" "Tobacco use"])]
    {:total (->int total)
     :male (->int male)
     :female (->int female)}))

(defn make-statistics
  []
  (mapv
   (fn [file]
     (zipmap
      [:country :population :alchocol-use :median-age :gross-production-rate
       :tobacco-use :fertility-rate]
      ((juxt country population alcohol-use median-age gross-reproduction-rate
             tobacco-use fertility-rate)
       (read-stats file))))
   files))

(comment
  (def file "ax.json")
  (county stats)
  (spit "showcase/docs/data/country_stats.edn" (make-statistics))
  (get-in stats ["People and Society"])
  (def stats (read-stats (first files))))
