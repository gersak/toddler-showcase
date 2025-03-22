(ns toddler.showcase.three
  (:require
   [helix.core :refer [$ defnc]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [toddler.ui :as ui]
   [shadow.css :refer [css]]
   ["@react-three/fiber" :as fiber]
   ["@react-three/drei" :as drei]))

(defnc ThreeBox
  [props]
  (let [_ref (hooks/use-ref nil)
        [hovered? hover!] (hooks/use-state false)
        [clicked? click!] (hooks/use-state false)]
    (fiber/useFrame
     (fn [_ delta]
       (when @_ref
         (let [current (.. @_ref -rotation -x)]
           (set! (.. @_ref -rotation -x) (+ current delta)))))
     #js [])
    ($ :mesh
       {& props
        :ref #(reset! _ref %)
        :scale (if clicked? 1.5 1)
        :on-click #(click! not)
        :on-pointer-over (fn [e] (.stopPropagation e) (hover! true))
        :on-pointer-out (fn [] (hover! false))}
       ($ :boxGeometry {:args #js [1 1 1]})
       ($ :meshStandardMaterial {:color (if hovered? "hotpink" "red")}))))

(defnc Basic
  []
  (d/div
   {:className (css
                :mt-10
                :mb-10
                :bg-normal+
                :border :border-normal :rounded-lg)}
   ($ fiber/Canvas
      {:style #js {:height 400}}
      ($ :ambientLight {:intensity (/ js/Math.PI 2)})
      ($ :spotLight {:position #js [10 10 10] :angle 0.15
                     :penumbra 1 :decay 0
                     :intensity js/Math.PI})
      ($ :pointLight {:position #js [-10 -10 -10] :decay 0 :intensity js/Math.PI})
      ($ ThreeBox {:position #js [-1.2 0 0]})
      ($ ThreeBox {:position #js [1.2 0 0]})
      ($ drei/OrbitControls))))
