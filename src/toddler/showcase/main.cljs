(ns toddler.showcase.main
  {:shadow.css/include
   ["css/toddler.css"]}
  (:require
   [shadow.lazy :as lazy]
   [shadow.cljs.modern :refer [js-await]]))

(defonce root (atom nil))

(def showcase (lazy/loadable toddler.showcase/start!))

(defn ^:dev/after-load start! []
  (.log js/console "Starting Toddler showcase!")
  ; (t/set-min-level! :debug)
  ; (t/set-min-level! :log "toddler.md" :debug)
  ; (t/set-min-level! :log "toddler.routing" :debug)
  (js-await [start (lazy/load showcase)]
            (start)))
