(ns toddler.showcase.lazy
  (:require
   [clojure.edn :as edn]
   [clojure.core.async :as async]
   [helix.core :refer [defnc $ <>]]
   [helix.hooks :as hooks]
   [shadow.css :refer [css]]
   [toddler.ui :as ui]
   [toddler.layout :as layout]
   [toddler.md.context :as md.context]
   [toddler.core :as toddler]
   [toddler.router :as router]
   [toddler.lazy :as lazy]
   [toddler.md.lazy :as md]
   [toddler.chart-js.lazy :refer [Chart]]
   #_[toddler.showcase.three :as three]
   [toddler.showcase.three.lazy :as three]))

(defnc example
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
        width (min 600 (- window-width 40))]
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
            ; data (take 10 (sort-by pred > data))
            data (sort-by :country  data)
            countries (map :country data)]
        (set-chart!
         {:type "bar"
          :options {:plugins {:legend {:display false}}}
          :data
          {:labels countries
           :datasets [{:label label
                       :data (mapv pred data)}]}})))
    (<>
     ($ ui/row
        {:align :center
         :className (css :pt-10 :pb-4)}
        ($ ui/dropdown-field
           {:search-fn :name
            :value topic
            :style {:max-width width}
            :options options
            :on-change set-topic!}))
     ($ ui/row
        {:align :center}
        ($ ui/row
           {:className (css
                        :border :border-normal :rounded-lg
                        :bg-normal+)
            :style {:max-width width}}
           ($ Chart {:config chart}))))))

(defnc doc
  []
  (let [{:keys [height width]} (layout/use-container-dimensions)]
    ($ ui/simplebar
       {:style {:height height
                :width width}}
       ($ ui/row {:align :center}
          ($ ui/column
             {:align :center
              :style {:max-width (min width 600)}
              :className (css
                          ["& .example-field" :my-5])}

             ($ md/watch-url {:url "/lazy.md"})
             ($ toddler/portal
                {:locator #(.getElementById js/document "chart-example")}
                ($ example))
             ($ toddler/portal
                {:locator #(.getElementById js/document "three-example")}
                ($ three/Basic)))))))

(defnc Lazy
  {:wrap [(router/wrap-rendered :toddler.lazy)
          (router/wrap-link
           :toddler.lazy
           [{:id ::js
             :name "I like JS"
             :segment "js"
             :hash "i-like-js"}
            {:id ::code-splitting
             :name "Code Splitting"
             :segment "code-splitting"
             :hash "code-splitting"}
            {:id ::loading
             :name "Loading"
             :segment "loading"
             :hash "lazy-loading"}])]}
  []
  ($ doc))
