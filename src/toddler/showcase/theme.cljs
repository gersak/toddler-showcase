(ns toddler.showcase.theme
  (:require
   [toddler.head :as head]))

(defn change-highligh-js
  [theme]
  (let [dark-url "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/base16/tomorrow-night.min.css"
        light-url "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/base16/atelier-lakeside-light.min.css"]
    (letfn [(fetch-dark []
              (head/remove
               :link
               {:href light-url
                :rel "stylesheet"})
              (head/add
               :link
               {:href dark-url
                :rel "stylesheet"}))
            (fetch-light []
              (head/remove
               :link
               {:href dark-url
                :rel "stylesheet"})
              (head/add
               :link
               {:href light-url
                :rel "stylesheet"}))]
      (case theme
        "light" (fetch-light)
        "dark" (fetch-dark)
        nil))))
