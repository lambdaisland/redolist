(ns redolist.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [redolist.events]
              [redolist.subs]
              [redolist.routes :as routes]
              [redolist.views :as views]
              [redolist.config :as config]))


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
