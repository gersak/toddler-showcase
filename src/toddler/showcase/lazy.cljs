(ns toddler.showcase.lazy
  (:require
   [clojure.edn :as edn]
   [clojure.core.async :as async]
   [helix.core :refer [defnc $ <>]]
   [helix.hooks :as hooks]
   [shadow.css :refer [css]]
   [toddler.ui :as ui]
   [toddler.md.context :as md.context]
   [toddler.core :as toddler]
   [toddler.router :as router]
   [toddler.chart-js.lazy :refer [Chart]]))

(defnc Lazy
  {:wrap [(router/wrap-rendered :toddler.lazy)]}
  []
  (let [[data set-data!] (hooks/use-state nil)
        [chart set-chart!] (hooks/use-state nil)
        base (hooks/use-context md.context/base)
        options [{:pred (comp :total :population)
                  :name "Population"
                  :label "Population "}
                 {:pred (comp :total :alchocol-use)
                  :name "Alcohol Usage"
                  :label "Alcohol Use [L] "}
                 {:pred (comp :total :tobacco-use)
                  :name "Tobacco Usage"
                  :label "Tobacco Use [%] "}
                 {:pred (comp :total :median-age)
                  :name "Median Age"
                  :label "Median Age [years] "}]
        [topic set-topic!]  (hooks/use-state (nth options 0))
        window-width (toddler/use-window-width)
        width (min 400 window-width)]
    (hooks/use-effect
      :once
      []
      (async/go
        (let [response (async/<! (toddler/fetch (str base "/data/country_stats.edn")))]
          (set-data! (edn/read-string response)))))
    (hooks/use-effect
      [data topic]
      (let [{:keys [pred label]} topic
            data (remove (comp nil? :country) data)
            data (take 10 (sort-by pred > data))
            countries (map :country data)]
        (set-chart!
         {:type "pie"
          :options {:plugins {:legend {:display false}}}
          :data
          {:labels countries
           :datasets [{:label label
                       :data (mapv pred data)}]}})))
    (<>
     ($ ui/row
        {:align :center
         :className (css :py-4)}
        ($ ui/dropdown-field
           {:search-fn :name
            :value topic
            :style {:max-width width}
            :options options
            :on-change set-topic!}))
     ($ ui/row
        {:align :center}
        ($ ui/row
           {:style {:max-width width}}
           ($ Chart {:config chart}))))))
