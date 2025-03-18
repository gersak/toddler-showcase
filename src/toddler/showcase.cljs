(ns toddler.showcase
  {:shadow.css/include ["css/toddler.css"]}
  (:require
   ["react" :as react]
   ; [taoensso.telemere :as t]
   [clojure.core.async :as async]
   [toddler.app :as app]
   [toddler.docs :as docs]
   [toddler.core :as toddler]
   [toddler.ui :refer [wrap-ui]]
   [toddler.ui.components :as default]
   [toddler.window :as window]
   [toddler.notifications :as notifications]
   [toddler.popup :as popup]
   [helix.core :refer [$ defnc provider]]
   [helix.hooks :as hooks]
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
   [toddler.showcase.camera :refer [Camera]]
   [toddler.showcase.lazy :refer [Lazy]]
   [toddler.showcase.theme :as showcase.theme]
   [toddler.router :as router]
   [toddler.ui.css :as ui.css]
   [toddler.md.lazy :as md]
   [toddler.search :as search]
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
    :name "Routing"
    :render Routing
    :segment "routing"}
   {:id :toddler.i18n
    :name "i18n"
    :render i18n
    :segment "i18n"}
   {:id :toddler.icons
    :name "Icons"
    :render Icons
    :segment "icons"}
   #_{:id :toddler.camera
      :name "Camera"
      :render Camera
      :segment "camera"}
   {:id :toddler.lazy
    :name "Lazy JS"
    :render Lazy
    :segment "lazy"}])

(defnc Showcase
  {:wrap [(router/wrap-link ::router/ROOT routes)
          (toddler/wrap-theme ::theme)
          (search/wrap-index "/docs.index.edn")
          (md/wrap-show {:className ui.css/$md
                         :on-theme-change showcase.theme/change-highligh-js})
          (notifications/wrap-store {:class ui.css/$store})
          (router/wrap-landing "/" false)
          (popup/wrap-container)
          (wrap-ui (assoc default/components :markdown md/show))
          (window/wrap-window-provider)]}
  []
  (let [mobile? (toddler/use-window-width-test < 1000)]
    (provider
     {:context app/locale
      :value :en}
     (provider
      {:context app/layout
       :value (if mobile? :mobile :desktop)}
      ($ docs/page
         {:max-width 1000
          :components routes}))))
  ;; TODO - Strict mode causes problems with popup window
  #_($ react/StrictMode
       ($ router/Provider
          ($ dev/playground {:components routes}))))
