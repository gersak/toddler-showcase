(ns toddler.showcase.camera
  (:require
   [clojure.core.async :as async]
   [helix.core :refer [defnc defhook $ create-context provider]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [helix.children :refer [children]]
   [toddler.ui :as ui]
   [toddler.layout :as layout]
   [shadow.css :refer [css]]
   [toddler.core :as toddler]
   [toddler.router :as router]))

(def -cameras- (create-context))
(def -select-camera- (create-context))
(def -camera- (create-context))

(defhook use-camera-stream
  "Hook will use target node element for camera stream
  based on prefered camera type `[\"back\", \"front\"]"
  ([^js el] (use-camera-stream el "user"))
  ([^js el camera-type] (use-camera-stream el camera-type [1920 1080]))
  ([^js el camera-type [width height]]
   (hooks/use-effect
     [el camera-type]
     (when el
       (async/go
         (let [stream (async/promise-chan)
               platform (condp re-find (.-userAgent js/navigator)
                          #"iPhone|iPad|iPod" :ios
                          #"(?i)android" :android
                          :desktop)]
           (->
            (.getUserMedia
             js/navigator.mediaDevices
             #js {:video
                  (case platform
                    :desktop true
                    #js {:facingMode
                         #js {:exact (case camera-type
                                      ;; if back camera
                                       ("back" :back "environment" :environment)
                                       "environment"
                                      ;; else
                                      ; ("front" :front "user" :user)
                                       "user")}
                         :width width :height height})})
            (.then (fn [s] (async/put! stream s)))
            (.catch (fn [err]
                      (async/put! stream
                                  (ex-info
                                   "No stream found"
                                   {:facing "back"
                                    :error err})))))
           (let [real-stream (async/<! stream)]
             (when-not (instance? js/Error real-stream)
               (set! (.-srcObject el) real-stream)
               (.play el)))))))))

(let [ratio (/ 16 9)]
  (defnc camera-test
    []
    (let [[video-ref set-video!] (hooks/use-state nil)
          [pic set-pic!] (hooks/use-state nil)
          canvas-ref (hooks/use-ref nil)
          [camera-type set-camera-type!] (hooks/use-state "back")
          {window-width :width
           window-height :height} (layout/use-container-dimensions)
          width (min (- window-width 10) 400)]
      (use-camera-stream video-ref camera-type [2560 1920])
      ($ ui/simplebar
         {:style {:width window-width
                  :height window-height}}
         ($ ui/column
            ($ ui/row
               {:align :center}
               (d/video
                {:ref #(set-video! %)
                 :className (css
                             :border
                             :border-normal
                             :rounded-md
                             :overflow-hidden)
                    ; :ref (fn [el]
                    ;        (when (and el stream)
                    ;          (set! (.-srcObject el) stream)))
                 :style {:display (when pic "none")}
                 :id "video-test"
                 :playsInline true
                 :autoPlay true
                 :width width}))
            (d/canvas
             {:ref #(reset! canvas-ref %)
              :style {:display "none"}})
            (when pic
              ($ ui/row
                 {:align :center}
                 (d/img
                  {:src pic
                   :style {:width width}
                   :className (css
                               :border
                               :border-normal
                               :rounded-md
                               :overflow-hidden)})))
            ($ ui/row
               {:align :center
                :className (css :pt-2)}
               ($ ui/button
                  {:on-click (fn []
                               (set-camera-type!
                                (case camera-type
                                  "front" "back"
                                  "back" "front")))}
                  "Switch")
               ($ ui/button
                  {:on-click (fn []
                               (if pic
                                 (set-pic! nil)
                                 (let [width (.-videoWidth video-ref)
                                       height (.-videoHeight video-ref)
                                       c @canvas-ref
                                       _ (do
                                           (set! (.-width c) width)
                                           (set! (.-height c) height))
                                       context (.getContext c "2d")]
                                   (.clearRect context 0 0 width height)
                                   (.drawImage context video-ref 0 0 width height)
                                   (set-pic! (.toDataURL c "image/png")))))}
                  (if pic "New" "Capture"))))))))

(defnc Camera
  {:wrap [(router/wrap-rendered :toddler.camera)]}
  []
  (let []
    ($ camera-test)))
