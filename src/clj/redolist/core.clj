(ns redolist.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [redolist.config :as config]
            [redolist.spicerack :refer [add-shutdown-hook]]
            [redolist.system :as system]))

(def system nil)

(add-shutdown-hook
 (Thread.
  (fn []
    (when system
      (alter-var-root #'system component/stop)
      (println "Clean shutdown.")))))

(defn -main [& _]
  (alter-var-root #'system
                  (-> config/config
                      system/prod-system
                      component/start
                      constantly))
  (println "Server started at" (str "http://localhost:" (:web-port config/config))))
