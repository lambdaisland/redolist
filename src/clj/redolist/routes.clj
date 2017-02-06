(ns redolist.routes
  (:require [compojure.core :refer [context DELETE GET POST PUT routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :as res]
            [spicerack.core :as sr])
  (:import java.util.UUID))

(defn new-routes [{:keys [db]}]
  (let [storage (sr/open-hashmap (:db db) "todos")]
    (routes
     (GET "/" _
       (some-> (res/resource-response "public/index.html")
               (res/content-type "html")))
     (context "/todos" _
       (GET "/" _
         {:body (get storage :todos [])})

       (POST "/" {:keys [params]}
         {:body (sr/update! storage :todos (fn [todos]
                                             (if (nil? todos)
                                               [params]
                                               (conj todos params))))})

       (PUT "/:id" {:keys [params]}
         (let [params (update params :id #(UUID/fromString %))]
           {:body (sr/update! storage :todos (fn [todos]
                                               (mapv #(if (= (:id %) (:id params))
                                                        params
                                                        %) todos)))}))

       (DELETE "/:id" [id]
         (let [id (UUID/fromString id)]
           {:body (sr/update! storage :todos (fn [todos]
                                               (vec (remove #(= (:id %) id) todos))))})))

     (resources "/"))))
