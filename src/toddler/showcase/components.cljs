(ns toddler.showcase.components
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [shadow.css :refer [css]]
   [toddler.ui :as ui :refer [defcomponent !]]
   [toddler.ui :refer [UI]]))

(defcomponent dropdown :my/dropdown)

(defnc dropdown-impl-1
  []
  (d/div
   {:className (css :p-4 :rounded-lg :text-rose-100 :bg-rose-800)}
   "This is first implementation"))

(defnc dropdown-impl-2
  []
  (d/div
   {:className (css :p-4 :rounded-lg :text-sky-100 :bg-sky-800)}
   "This is second implementation"))

(defnc MyApp
  []
  (let [[state set-state!] (hooks/use-state nil)]
    (! :row {:align :center}
       (! :column {:align :center}
          (! :row {:align :center}
             ($ UI
                {:components {:my/dropdown (if state dropdown-impl-2 dropdown-impl-1)}}
                ($ dropdown)))
          (! :row {:align :center}
             (! :button
                {:on-click (fn []
                             (set-state! not))}
                "Change"))))))

; (defnc BasedOnToddler
;   {:wrap [(provider/wrap-ui toddler.ui.components/default)]}
;   []
;   ;; Here goes you code and you are can use components from
;   ;; toddler.ui namespace because they are mapped to 
;   ;; toddler.ui.components/default implementation
;   ($ Stuff))

; (defnc MyAppWithSpecialPopup
;    {:wrap [(provider/extend-ui {:popup my-special-implementation})]}
;    []
;    )
