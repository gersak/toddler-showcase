## I like JS
Toddler is low on JS dependency. That isn't because JS is not good or Clojurescript is so awesome (and it is!).
Reason is that using JS libraries often is awkward and compromising and it comes with bagage.

Using JS libraries and not using code splitting (when possible) will bloat your application size very quickly.

In this section we will go through [shadow-cljs](https://github.com/thheller/shadow-cljs) code splitting example
where [chart.js](https://www.chartjs.org) library will be lazy loaded using `toddler.lazy/load` macro 
that utilizes `shadow.loader/load` functionality.



## Code Splitting


```clojure
{:builds {:dev
          {:target :browser
           :entries [toddler.showcase.dev]
           :module-loader true
           :modules {:main
                     {:entries [toddler.showcase.dev]
                      :init-fn toddler.showcase.dev/start!}
                     :markdown
                     {:entries [toddler.md]
                      :depends-on #{:main}}
                     :chartjs
                     {:entries [toddler.chart-js]
                      :depends-on #{:main}}}
           :output-dir "dev/js"
           :output-to "dev/js/main.js"}}}
```


<div id="chart-example"></div>
