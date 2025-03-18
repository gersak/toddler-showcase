(ns toddler.showcase.three.lazy
  (:require
   [toddler.lazy :as lazy]))

(lazy/load-components
 "three"
 ::Basic toddler.showcase.three/Basic)
