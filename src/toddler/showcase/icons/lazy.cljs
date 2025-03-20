(ns toddler.showcase.icons.lazy
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [toddler.router :as router]
   [shadow.lazy :as lazy]
   [shadow.cljs.modern :refer [js-await]]))

(def showcase (lazy/loadable toddler.showcase.icons/Showcase))

(def icons (atom nil))

(defnc Icons
  []
  {:wrap [(router/wrap-rendered :toddler.icons)]}
  (let [[loaded? loaded!] (hooks/use-state nil)]
    (hooks/use-effect
      :once
      (when-not @icons
        (js-await [_icons (lazy/load showcase)]
                  (reset! icons _icons)
                  (loaded! true))))
    (when (or @icons loaded?)
      ($ @icons))))
