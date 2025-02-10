(ns toddler.showcase.rationale
  (:require
   [goog.string :refer [format]]
   [helix.core :refer [$ defnc <>]]
   [helix.dom :as d]
   [shadow.css :refer [css]]
   [toddler.core :as toddler]
   [toddler.ui :as ui :refer [!]]
   [toddler.layout :as layout]
   [toddler.md.lazy :as md]
   [toddler.router :as router]
   [toddler.showcase.components :refer [MyApp]]))

(def screeming-kid "https://giphy.com/embed/3oEduOnl5IHM5NRodO")
(def oops-kid "https://giphy.com/embed/9ZRHvd4j02Qda09B25")
(def dj-kid "https://giphy.com/embed/mDGL3G97HPIh3X7OXe")
(def smooth-slide "https://giphy.com/embed/h8n8aJWronkmvRTB0y")
(def kid-shocked "https://giphy.com/embed/WUgrpaWVu5FBiqpWtG")
(def pool-fail "https://giphy.com/embed/3oEdv4r6i9wbC8iX4I")

(defnc GiphyEmbed [{:keys [src width height]}]
  (d/div
   {;:className (css :border :rounded-md :overflow-hidden)
    ;:style {:width width :height height}
    :dangerouslySetInnerHTML
    #js {:__html (format
                  "<iframe src=\"%s\" width=\"%s\" height=\"%s\" style=\" \" frameBorder=\"0\" class=\"giphy-embed\" allowFullScreen></iframe><p><a href=\"https://giphy.com/gifs/studiosoriginals-2yLNN4wTy7Zr8JSXHB\"></a></p>"
                  src width height)}}))

(def no-js
  (toddler/ml
   "That is **not true**. But it is not far from it :sunglasses:"
   ""
   "Toddler is built on top of [Helix](https://github.com/lilactown/helix)"
   " Clojurescript library. Helix is thin wrapper around [React](https://react.dev/),"
   "so Toddler has minimal dependencies on **react** and **react-dom**."
   ""
   "That is it... **Everything else is Clojurescript code!**"))

(def ready-to-grow
  (toddler/ml
   "This is not a component library. Yes, Toddler includes ready-to-use components,"
   "but that’s **not** its primary purpose. You are encouraged to create **your own**"
   "components, tailored to your needs.  "
   ""
   " The real power of Toddler lies in **hooks** and **utility functions** found in the"
   "`toddler.core`, `toddler.routing`, `toddler.popup` and other [namespaces](https://gersak.github.io/toddler/codox/index.html)."
   "Default components exist mainly to **showcase that the system works**."))

(def themable
  (toddler/ml
   "Implemented components support themeing by supplying variables through"
   "css. [shadow-css](https://github.com/thheller/shadow-css) is used for styling default"
   "components."
   ""
   "[toddler.css](https://github.com/gersak/toddler/blob/main/themes/default/css/toddler.css)"
   "can be used as template for new theme. Create your own theme css file and load it"
   "in your code by using:"
   ""
   "```clojure"
   "(ns my.app"
   "  {:shadow.css/include [\"path/to/my-theme.css\"]})"
   "```"
   ""
   "There is second more brute way of themeing components and  that is"
   "to provide ```toddler.ui/__component__``` context with your"
   "implementation of UI components."
   ""
   "[More here](#providing-components)..."))

(defnc feature-section
  [{:keys [src text]}]
  ($ ui/row
     {:className (css :my-2 ["&:first-child" :mt-12])}
     (d/img
      {:src src
       :style {:min-width "170px"}})
     ($ md/show
        {:className (css :flex-grow
                         :mx-4
                         :ml-6
                         :my-2
                         :p-2
                         :border :rounded-lg :border+ :bg-normal+
                         {:grow "1"}
                         ["& p" :text-xxs]
                         ["& p > code" :text-xxxs])
         :content text})))

(defnc Rationale
  {:wrap [(router/wrap-link
           :toddler.rationale
           [{:id ::concepts
             :hash "concepts"}
            {:id ::future
             :hash "future"}])
          (router/wrap-rendered :toddler.rationale)]}
  []
  (let [{:keys [height width]} (layout/use-container-dimensions)]
    (! :simplebar {:style {:height height
                           :width width}}
       (! :row {:align :center}
          (! :column {:align :center
                      :style {:max-width "48rem"}
                      :className (css
                                  ["& .component" :my-6])}
             ($ feature-section
                {:src "/g93081.svg"
                 :text no-js})
             ($ feature-section
                {:src "/g59618.svg"
                 :text ready-to-grow})
             ($ feature-section
                {:src "/g63223.svg"
                 :text themable})
             #_(d/div
                {:className "bang"}
                (d/img
                 {:src "/g63210.svg"
                  :width "200px"}))
             #_(d/div
                {:className "bang"}
                (d/img
                 {:src "/g59634.svg"
                  :width "200px"}))
             #_(d/div
                {:className "bang"}
                (d/img
                 {:src "/g63223.svg"
                  :width "200px"}))
             #_(<>
                ($ GiphyEmbed
                   {:src screeming-kid
                    :width 400 :height 200})
                ($ GiphyEmbed
                   {:src oops-kid
                    :width 190 :height 250})

                ($ GiphyEmbed
                   {:src dj-kid
                    :width 200 :height 200})
                ($ GiphyEmbed
                   {:src smooth-slide
                    :width 200 :height 200})
                ($ GiphyEmbed
                   {:src kid-shocked
                    :width 200 :height 200})
                ($ GiphyEmbed
                   {:src pool-fail
                    :width 200 :height 200}))
             ($ md/watch-url {:url "/rationale.md"})
             ($ toddler/portal
                {:locator #(.getElementById js/document "components-example")}
                ($ MyApp)))))))
