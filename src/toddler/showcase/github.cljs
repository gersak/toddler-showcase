(ns toddler.showcase.github
  {:shadow.css/include
   ["css/toddler.css"]}
  (:require
   ["react-dom/client" :refer [createRoot]]
   [helix.core :refer [$ defnc provider]]
   ; [taoensso.telemere :as t]
   [toddler.router :as router]
   [toddler.showcase :refer [Showcase]]
   [toddler.md.context :as md.context]))

(defonce root (atom nil))

(defnc LoadShowcase
  []
  (.log js/console "Mounting showcase component!")
  ($ router/Provider
     {:base "toddler"}
     (provider
      {:context md.context/refresh-period
       :value 0}
      (provider
       {:context md.context/base
        :value "https://raw.githubusercontent.com/gersak/toddler-showcase/refs/heads/main/docs"}
       (provider
        {:context search.context/base
         :value "https://raw.githubusercontent.com/gersak/toddler-showcase/refs/heads/main/docs"}
        ($ Showcase))))))

(defn ^:dev/after-load start! []
  (let [target ^js (.getElementById js/document "app")]
    (when-not @root
      (.log js/console "Rendering playground")
      (reset! root ^js (createRoot target)))
    (.render ^js @root ($ LoadShowcase))))
