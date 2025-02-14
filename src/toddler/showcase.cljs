(ns toddler.showcase
  {:shadow.css/include ["css/toddler.css"]}
  (:require
   ["react" :as react]
   ["react-dom/client" :refer [createRoot]]
   [helix.dom :as d]
   [taoensso.telemere :as t]
   [toddler.dev :as dev]
   [toddler.ui :refer [wrap-ui]]
   [toddler.ui.components :as default]
   [helix.core :refer [$ defnc provider]]
   [toddler.head :as head]
   [toddler.showcase.layout :refer [Layout]]
   [toddler.showcase.inputs :refer [Inputs]]
   [toddler.showcase.table :refer [Table TableGrid]]
   [toddler.showcase.calendar :refer [Calendar]]
   [toddler.showcase.popup :refer [Popup]]
   [toddler.showcase.i18n :refer [i18n]]
   [toddler.showcase.routing :refer [Routing]]
   [toddler.showcase.icons :refer [Icons]]
   [toddler.showcase.modal :refer [Modal]]
   [toddler.showcase.notifications :refer [Notifications]]
   [toddler.showcase.rationale :refer [Rationale]]
   [toddler.showcase.theme :as showcase.theme]
   [toddler.notifications :as notifications]
   [toddler.router :as router]
   [toddler.ui.css :as ui.css]
   [toddler.md.context :as md.context]
   toddler.i18n.number
   toddler.i18n.time
   toddler.i18n
   toddler.i18n.common))

;; TODO - this was attempt to remove unnecessary formating
;; for time and number formaters. But it turned out
;; that it app size didn't get significantly smaller
; (toddler.i18n.time/add-symbols [:hr :de :en :fr :es :ja :zh_CN])
; (toddler.i18n.number/add-symbols [:hr :de :en :fr :es :ja :zh_CN])

; (toddler.i18n.time/init-all-symbols)
; (toddler.i18n.number/init-all-symbols)

; (println "SYMBOLS: " toddler.i18n/locales)
; (cljs.pprint/pprint toddler.i18n.number/*symbols*)

(.log js/console "Loaded showcase!")

(defonce root (atom nil))

(def routes
  [{:id :toddler.rationale
    :name "Rationale"
    :render Rationale
    :segment "rationale"
    :landing 10}
   {:id :toddler.inputs
    :name "Inputs"
    :render Inputs
    :segment "inputs"}
   {:id :toddler.table
    :name "Table"
    :render Table
    :segment "tables"}
   {:id :toddler.calendar
    :name "Calendar"
    :render Calendar
    :segment "calendar"}
   {:id :toddler.layout
    :name "Layout"
    :render Layout
    :segment "layout"}
   {:id :toddler.popup
    :name "Popup"
    :render Popup
    :segment "popup"}
   {:id :toddler.modal
    :name "Modal"
    :render Modal
    :segment "modal"}
   {:id :toddler.notifications
    :name "Notifications"
    :render Notifications
    :segment "notifications"}
   {:id :toddler.routing
    :name :showcase.routing
    :render Routing
    :segment "routing"}
   {:id :toddler.i18n
    :name :showcase.i18n
    :render i18n
    :segment "i18n"}
   {:id :toddler.icons
    :name :showcase.icons
    :render Icons
    :segment "icons"}])

(defnc Showcase
  {:wrap [(notifications/wrap-store {:class ui.css/$store})
          (router/wrap-landing "/" false)
          (wrap-ui default/components)]}
  []
  (provider
   {:context md.context/show
    :value {:className ui.css/$md
            :on-theme-change showcase.theme/change-highligh-js}}
   ($ dev/playground
      {:max-width 1000
       :components routes}))
  ;; TODO - Strict mode causes problems with popup window
  #_($ react/StrictMode
       ($ router/Provider
          ($ dev/playground {:components routes}))))

(defnc LoadShowcase
  []
  ($ router/Provider
     {:base "toddler"}
     (provider
      {:context md.context/refresh-period
       :value 0}
      (provider
       {:context md.context/base
          ; :value "https://raw.githubusercontent.com/gersak/toddler/refs/heads/prep/github-page/dev"}
        :value "https://raw.githubusercontent.com/gersak/toddler-showcase/refs/heads/main/docs"}))))

(defn start! []
  (.log js/console "Starting Toddler showcase!")
  (t/set-min-level! :info)
  ; (t/set-min-level! :log "toddler.md" :debug)
  ; (t/set-min-level! :log "toddler.routing" :debug)
  (let [target ^js (.getElementById js/document "app")]
    (when-not @root
      (reset! root ^js (createRoot target)))
    (.log js/console "Rendering playground")
    (.render ^js @root ($ LoadShowcase))))

(set! (.-start js/window) start!)
