(ns redolist.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [redolist.events]
              [redolist.subs]
              [redolist.views :as views]
              [redolist.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (routes/app-routes)
  (dev-setup)
  (mount-root))
