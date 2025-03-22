## I like JS
Toddler is low on JS dependency. That isn't because JS is not good and Clojurescript 
is so awesome (and it is!). Reason is that using JS libraries often is awkward and
compromising and it comes with bagage.

Using JS libraries and not using code splitting (when possible) will bloat your
application size very quickly.

In this section we will go through [shadow-cljs](https://github.com/thheller/shadow-cljs)
code splitting example where [chart.js](https://www.chartjs.org) library 
and [three.js](https://threejs.org/) will be lazy loaded using `toddler.lazy/load` macro
that utilizes `shadow.loader/load` functionality.


## Code Splitting
Code splitting helps reduce the initial JavaScript bundle size by breaking your application
into smaller chunks that are loaded only when needed. This can significantly improve load
times and user experience, especially for large applications.

[Shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html#CodeSplitting)
simplifies code splitting in ClojureScript by providing an intuitive approach
using module configurations.

```clojure
{:builds {:main {:target :browser
                 :module-loader true
                 :modules  {:main {:entries [toddler.showcase.main]
                                   :init-fn toddler.showcase.main/start!}
                            :ui {:entries [toddler.ui.components]
                                 :depends-on #{:main}}
                            :markdown {:entries [toddler.md]
                                       :depends-on #{:ui}}
                            :showcase {:entries [toddler.showcase]
                                       :depends-on #{:markdown}}
                            :icons {:entries [toddler.showcase.icons]
                                    :depends-on #{:showcase :ui :markdown}}
                            :chartjs {:entries [toddler.chart-js]
                                      :depends-on #{:showcase :ui :markdown}}
                            :three {:entries [toddler.showcase.three]
                                    :depends-on #{:showcase :ui :markdown}}}
                 :output-dir "dev/js"
                 :output-to "dev/js/main.js"}}}
```

Above configuration is used in [toddler-showcase](https://github.com/gersak/toddler-showcase)
repo, that is used to generate **this** documentation/showcase site.

`shadow-cljs` will use configuration to split codebase into seven JS files and output it to `output-dir`.
Files will be named *main.js*, *ui.js*, *markdown.js* etc.

For showcase I wanted to demonstrate that other JS libraries can be used as well. `markdown-it` is used
for rendering markdown files, `chart.js` and `three.js` are used only when you are at [Lazy JS](/toddler/lazy).

That would imply that those modules should be loaded only when frontend is rendering components using
code that is placed in those files. How?


## Lazy loading

<!-- **:main**  will load at startup because it has no dependencies. Other modules should be lazily-->
<!--loaded with `shadow.lazy/load` function in combination with `shadow.cljs.modern/js-await` functions.-->

How this works... Lets say that you want to use `markdown-it` JS library. You create wrapper around
`markdown-it` and define Helix components that leverage that library. Lets say that this will be coded
in `toddler.md` namespace.

Now if you use that namespace anywhere in your *main* module, than `markdown-it` javascript code will
be bundled with *main.js* and you app size will with grow with each new JS dependency. Quickly it will
be 1Mb, than 3Mb, than >5Mb.

What do you do? You create lazy namespace that will mirror original namespace. I.E. `toddler.md.lazy`
and in that namespace you use `toddler.lazy/load-components` macro to define lazy versions of components
from `toddler.md` namespace.

```clojure
(ns toddler.md.lazy
  (:require
   [helix.core :refer [$ provider fnc]]
   [toddler.lazy :as lazy]
   [toddler.md.context :as md.context]))

(lazy/load-components
 ::show toddler.md/show
 ::from-url toddler.md/from-url
 ::watch-url toddler.md/watch-url
 ::img toddler.md/img)
```

Macro `load-components` expects sequence of bindings where odd values are keywords that 
are used to cache loaded component and even values are symbols (components) that will 
be lazily loaded.

Under the hood macro will create Helix component using `(defnc ~(symbol (name k))`
where k is odd keyword from provided bindings. For example above that would feel like:

```clojure
(ns toddler.md.lazy
  (:require
   [helix.core :refer [$ provider fnc]]
   [toddler.lazy :as lazy]
   [toddler.md.context :as md.context]))

(defnc show [props _ref] ($ toddler.lazy/show {:ref _ref & props}))
(defnc from-url [props _ref] ($ toddler.lazy/from-url {:ref _ref & props}))
(defnc watch-url [props _ref] ($ toddler.lazy/watch-url {:ref _ref & props}))
(defnc img [props _ref] ($ toddler.lazy/img {:ref _ref & props}))
```
Created lazy components have *hook* that will check if component was already loaded before
and if it was it will use that component. If it wasn't it will call `shadow-cljs` functions
to lazy load target components and cache them.


Bellow are examples for `chart.js` and `three.js` libraries. If you open developer tools and
go to *Network* tab, than you should expirience lazy loading this libraries only when **Lazy JS**
navigation option is selected.

#### Chart.js example
<div id="chart-example"></div>

#### Three.js example

<div id="three-example"></div>
