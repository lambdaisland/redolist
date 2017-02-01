(ns redolist.core
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [context GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.util.response :as res]
            [spicerack.core :as sr]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.http-kit :refer [new-web-server]]
            [system.components.middleware :refer [new-middleware]]))

(set! *warn-on-reflection* true)

(defn add-shutdown-hook [^Thread thread]
  (.addShutdownHook (Runtime/getRuntime) thread))

(defn remove-shutdown-hook [^Thread thread]
  (.removeShutdownHook (Runtime/getRuntime) thread))

(defrecord SpicerackComponent [path]
  component/Lifecycle
  (start [this]
    (if (:db this)
      this ;; idempotent
      (let [^org.mapdb.DB db (sr/open-database path)
            hook (Thread. (fn [] (.close db)))]
        (add-shutdown-hook hook) ;; prevent corruption when the JVM is killed
                                 ;; without first stopping the system
        (assoc this :db db :hook hook))))

  (stop [{:keys [^org.mapdb.DB db ^Thread hook] :as this}]
    (when db
      (.close db))
    (when hook
      (remove-shutdown-hook hook))
    (dissoc this :db :hook)))

(defn new-spicerack [path]
  (->SpicerackComponent path))

(defn new-routes [{:keys [db]}]
  (let [storage (sr/open-hashmap (:db db) "todos")]
    (routes
     (GET "/" _
       (some-> (res/resource-response "public/index.html")
               (res/content-type "html")))
     (context "/todos" _
       (GET "/" _
         {:body (get storage :todos [])})
       (POST "/" [title]
         {:body (sr/update! storage :todos (fn [todos]
                                             (if (nil? todos)
                                               [{:title title}]
                                               (conj todos {:title title}))))}))
     (resources "/"))))

(defn app-system []
  (component/system-map
   :db (new-spicerack "./db.mapdb")
   :endpoint (-> (new-endpoint new-routes)
                 (component/using [:db]))
   :middleware (new-middleware {:middleware [[wrap-defaults api-defaults]
                                             wrap-restful-format]})
   :handler (-> (new-handler)
                (component/using [:endpoint :middleware]))
   :web (-> (new-web-server 3000)
            (component/using [:handler]))))

(comment
  (with-open [db (sr/open-database "./baking-db")]
    (let [ingredients (sr/open-hashmap db "ingredient-hashmap")]
      (put! ingredients :apple-pie [:flour :butter :sugar :apples])
      ;;=> [:flour :butter :sugar :apples]
      (update! ingredients :apple-pie #(conj % :cinnamon)))))
    ;;=> [:flour :butter :sugar :apples :cinnamon]
