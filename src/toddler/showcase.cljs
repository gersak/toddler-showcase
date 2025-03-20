(ns toddler.showcase
  {:shadow.css/include ["css/toddler.css"]}
  (:require
   ["react" :as react]
   ["react-dom/client" :refer [createRoot]]
   ; [taoensso.telemere :as t]
   [shadow.lazy :as lazy]
   [shadow.cljs.modern :refer [js-await]]
   [clojure.core.async :as async]
   [toddler.app :as app]
   [toddler.docs :as docs]
   [toddler.core :as toddler]
   [toddler.ui :as ui]
   ; [toddler.ui.components :as default]
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
   [toddler.showcase.icons.lazy :refer [Icons]]
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

(def ui-components
  (lazy/loadable toddler.ui.components/components))

(goog-define MD_BASE "")
(goog-define MD_REFRESH_PERIOD 3000)
(goog-define ROUTER_BASE "")
(goog-define SEARCH_INDEX "/docs.index.ednkitalabudova")

(defnc Showcase
  {:wrap [(router/wrap-link ::router/ROOT routes)
          (router/wrap-landing "/" false)
          (toddler/wrap-theme ::theme)
          (search/wrap-index SEARCH_INDEX)
          (md/wrap-show {:className ui.css/$md
                         :on-theme-change showcase.theme/change-highligh-js})
          (md/wrap-base MD_BASE)
          (md/wrap-refresh MD_REFRESH_PERIOD)
          (notifications/wrap-store {:class ui.css/$store})
          (popup/wrap-container)
          ; (wrap-ui (assoc default/components :markdown md/show))
          (router/wrap-router ROUTER_BASE)
          (window/wrap-window-provider)]}
  []
  (let [mobile? (toddler/use-window-width-test < 1000)
        [components set-components!] (helix.hooks/use-state nil)]
    (helix.hooks/use-effect
      :once
      (js-await
       [c (lazy/load ui-components)]
       (set-components! c)))
    (toddler/use-mouse-tracker)
    ($ ui/UI
       {:components (assoc components :markdown md/show)}
       (provider
        {:context app/locale
         :value :en}
        (provider
         {:context app/layout
          :value (if mobile? :mobile :desktop)}
         ($ docs/page
            {:max-width 1000
             :components routes})))))
  ;; TODO - Strict mode causes problems with popup window
  #_($ react/StrictMode
       ($ router/Provider
          ($ dev/playground {:components routes}))))

(defonce root (atom nil))

(defn start! []
  (.log js/console "Starting Toddler showcase development!")
  ; (t/set-min-level! :debug)
  ; (t/set-min-level! :log "toddler.md" :debug)
  ; (t/set-min-level! :log "toddler.routing" :debug)
  (let [target ^js (.getElementById js/document "app")]
    (when-not @root
      (.log js/console "Rendering playground")
      (reset! root ^js (createRoot target)))
    (.render ^js @root ($ Showcase))))
