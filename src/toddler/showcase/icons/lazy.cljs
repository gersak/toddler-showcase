(ns toddler.showcase.icons.lazy
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [toddler.router :as router]
   [toddler.lazy :as lazy]))

(lazy/load-components
 ::Showcase toddler.showcase.icons/Showcase)

(defnc Icons
  {:wrap [(router/wrap-rendered :toddler.icons)]}
  []
  ($ Showcase))
