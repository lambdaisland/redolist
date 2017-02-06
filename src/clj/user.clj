(ns user
  (:require [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.repl-api :as fw-repl-api]
            [figwheel-sidecar.system :as fw-sys]
            [garden-watcher.core :refer [new-garden-watcher]]
            ^:refactor-nrepl/keep
            [reloaded.repl :refer [go init reset reset-all start stop suspend resume system clear]]
            [redolist.system :as system]
            [redolist.config :as config]))

(defn dev-system [{:keys [css-paths css-ns] :as config}]
  (assoc
   (system/prod-system config)
   :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
   :css-watcher (fw-sys/css-watcher {:watch-paths css-paths})
   :garden-watcher (new-garden-watcher css-ns)))

(defn cljs-repl
  ([]
   (cljs-repl nil))
  ([id]
   (when (get-in reloaded.repl/system [:figwheel-system :system-running] false)
     (fw-sys/cljs-repl (:figwheel-system reloaded.repl/system) id)
     (fw-repl-api/cljs-repl))))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! #(dev-system config/config))
