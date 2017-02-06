(ns redolist.system
  (:require [com.stuartsierra.component :as component]
            [redolist.routes :refer [new-routes]]
            [redolist.spicerack :refer [new-spicerack]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.http-kit :refer [new-web-server]]
            [system.components.middleware :refer [new-middleware]]))

(defn prod-system [{:keys [db-file web-port]}]
  (component/system-map
   :db (new-spicerack db-file)
   :endpoint (-> (new-endpoint new-routes)
                 (component/using [:db]))
   :middleware (new-middleware {:middleware [[wrap-defaults api-defaults]
                                             wrap-restful-format]})
   :handler (-> (new-handler)
                (component/using [:endpoint :middleware]))
   :web (-> (new-web-server web-port)
            (component/using [:handler]))))
