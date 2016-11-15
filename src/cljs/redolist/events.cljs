(ns redolist.events
  (:require [re-frame.core :as re-frame :refer [debug reg-event-db]]
            [redolist.db :as db]))

(reg-event-db :initialize-db
              (fn  [_ _]
                db/default-db))

(reg-event-db :set-active-panel
              (fn [db [_ active-panel]]
                (assoc db :active-panel active-panel)))

(reg-event-db :todos/add
              (fn [db [_ title]]
                (let [id (random-uuid)]
                  (update db :todos assoc id {:id id
                                              :title title}))))

(reg-event-db :todos/remove
              (fn [db [_ id]]
                (update db :todos dissoc id)))

(reg-event-db :todos/update [debug]
              (fn [db [_ id vals]]
                (update-in db [:todos id] merge vals)))

(reg-event-db :todos/toggle
              (fn [db [_ id]]
                (update-in db [:todos id :completed] not)))

(reg-event-db :todos/toggle-all
              (fn [db [_ id]]
                (let [ids (keys (:todos db))
                      todos (vals (:todos db))
                      any-active (->> todos
                                      (map :completed)
                                      (remove identity)
                                      not-empty
                                      boolean)]
                  (reduce (fn [db id]
                            (assoc-in db [:todos id :completed] any-active))
                          db ids))))

(reg-event-db :set-display-type
              (fn [db [_ type]]
                (assoc db :display-type type)))
