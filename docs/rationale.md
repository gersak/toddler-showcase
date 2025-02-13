
## UI and Love-Hate

I've been coding UI for many years. Over time, it has been an emotional experience.
**I hate UI programming**, and I love it. Few things in programming feel as satisfying
as seeing what you imagined come to life exactly the way you wanted.

But why is this **so hard** most of the time? Styling, routing, positioning, responsiveness—
there are just so many details to handle to achieve **that UI**.

You look for help, turn to existing frameworks, and feel like you're making progress.
Then, after days, weeks, or months, everything **breaks**. Your decisions backfire,
your UI turns to chaos, and you feel like you're back to square one. **Reset. Repeat.**

I've used so many great libraries—[styled-components](https://styled-components.com/),
[react-table](https://tanstack.com/table/latest), [react-router](https://reactrouter.com/),
[react-spring](https://www.react-spring.dev/), [framer-motion](https://motion.dev/), and more.
Each helped in some way, but over time, every single one of them fell short.

Some bloated my application, others were exhausting to integrate with ClojureScript,
and many became overloaded with features—sometimes leaning too heavily on JSX.


Toddler is result of using and than avoiding above libraries. Just taking what I liked from
libraries and inserting what I thought was missing. 

There was no goal, no ambition. Toddler just came to life...



## Providing Components

During one of these UI struggles, a **brilliantly stupid** idea was born:
What if components were just **placeholders** in the code, and I could swap them out
using a shared context?

```clojure
(defmacro defcomponent
  "Defines a component that retrieves its implementation from the UI context."
  [_name key]
  `(helix.core/defnc ~_name [props# ref#]
     {:wrap [(toddler.ui/forward-ref)]}
     (let [components# (helix.hooks/use-context __components__)
           component# (get components# ~key)
           children# (helix.children/children props#)]
       (when component#
         (helix.core/$ component# {:ref ref# :& props#} children#)))))

```

This means I can **swap components dynamically** by changing the UI context.
Need to replace a dropdown? Just change the context—it will propagate automatically
throughout the app.


## How It Works (With an Example)

Let's say we want to create a dropdown that can **swap implementations** at runtime.
We define a generic `dropdown` component that looks for its implementation in the context under ```:my/dropdown``` key.

```clojure
(ns toddler.showcase.components
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [toddler.ui :as ui :refer [defcomponent]]
   [toddler.ui :refer [UI]))

;; Declare a general dropdown component
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

;; Swap dropdown implementations dynamically
(defnc MyApp []
  (let [[state set-state!] (hooks/use-state nil)]
    ($ ui/row
       {:align :center}
       ($ ui/column
          {:align :center}
          ($ ui/row
             ($ UI
                {:components {:my/dropdown (if state dropdown-impl-2 dropdown-impl-1)}}
                ($ dropdown)))
          ($ ui/row
             ($ ui/button {:on-click #(set-state! not)} "Change"))))))
```

Clicking the **"Change"** button swaps the dropdown component between two implementations.
With **Toddler**, components become placeholders, and **UI customization becomes effortless**.


## [Try It Out](https://github.com/gersak/toddler?tab=readme-ov-file#quickstart)

<div id="components-example"></div>


Toddler provides declared components in the `toddler.ui` namespace. It is recommended to keep  
all components within a single namespace to minimize complexity.  

The default implementations of Toddler components can be found in the following namespaces:  
`toddler.ui.elements`, `toddler.ui.fields`, `toddler.ui.tables`, and other `toddler.ui.*` namespaces.  

All implemented components are then grouped together in the `toddler.ui.components` namespace.  
This simplifies passing components as values to the `toddler.ui/__components__` context.  

Default components are packaged separately from **toddler.core**. You can find the JAR on Clojars:  
[dev.gersak/toddler-ui](https://clojars.org/dev.gersak/toddler-ui).  

```clojure
(defnc MyAppBasedOnToddlerComponents
  {:wrap [(ui/wrap-ui toddler.ui.components/default)]}
  []
  ;; You can use components from the toddler.ui namespace because
  ;; they are mapped to toddler.ui.components/default.
  ($ Stuff))

(defnc MyAppWithSpecialPopup
  {:wrap [(ui/extend-ui {:popup my-special-implementation})]}
  []
  ;; Override or extend the current UI context dynamically.
)
```

## Recommendation  
If you want to customize the default components, copy the `toddler.ui.*` namespaces  
and modify the copied code. There is nothing wrong with copy-pasting when working with UI,  
as long as the codebase remains manageable.  

Hope that Toddler makes it manageable... **Good luck and Godspeed!** 🚀

