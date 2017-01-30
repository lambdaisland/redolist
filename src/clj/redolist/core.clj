(ns redolist.core
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [context GET POST]]
            [ring.middleware
             [defaults :refer [api-defaults wrap-defaults]]
             [format :refer [wrap-restful-format]]]
            [spicerack.core :as sr]
            [system.components
             [endpoint :refer [new-endpoint]]
             [handler :refer [new-handler]]
             [http-kit :refer [new-web-server]]
             [middleware :refer [new-middleware]]]))

(defrecord SpicerackComponent [path]
  component/Lifecycle
  (start [sr]
    (assoc sr :db (sr/open-database path)))
  (stop [{:keys [^org.mapdb.DB db] :as sr}]
    (.close db)
    (dissoc sr :db)))

(defn new-spicerack [path]
  (->SpicerackComponent path))

(defn new-routes [{:keys [db]}]
  (let [storage (sr/open-hashmap (:db db) "todos")]
    (context "/todos" _
      (GET "/" _
        {:body (get storage :todos [])})
      (POST "/" [title]
        {:body (sr/update! storage :todos (fn [todos]
                                            (if (nil? todos)
                                              [{:title title}]
                                              (conj todos {:title title}))))}))))

(defn app-system []
  (component/system-map
   :db (new-spicerack "./db.mapdb")
   :endpoint (-> (new-endpoint #'new-routes)
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
