(ns redolist.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            ^:refactor-nrepl/keep [redolist.events]
            ^:refactor-nrepl/keep [redolist.effects]
            ^:refactor-nrepl/keep [redolist.subs]
            ^:refactor-nrepl/keep [redolist.routes :as routes]
            ^:refactor-nrepl/keep [redolist.views :as views]
            ^:refactor-nrepl/keep [redolist.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
