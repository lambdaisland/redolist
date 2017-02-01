(ns redolist.events
  (:require [re-frame.core :as re-frame :refer [debug reg-event-db reg-event-fx]]
            [redolist.db :as db]))

(reg-event-fx :initialize-db
              (fn  [_ _]
                {:db db/default-db
                 :api [:GET "/todos" {:dispatch [:todos/set]}]}))

(reg-event-db :set-active-panel
              (fn [db [_ active-panel]]
                (assoc db :active-panel active-panel)))

(reg-event-db :todos/set
              (fn [db [_ todos]]
                (assoc db :todos (reduce (fn [m t]
                                           (assoc m (:id t) t)) {} todos))))

(reg-event-fx :todos/add
              (fn [{:keys [db]} [_ title]]
                (let [id (random-uuid)
                      todo {:id id :title title}
                      notification [:todos/added todo]]
                  {:db (-> db
                           (update :todos assoc id todo)
                           (update :notifications conj notification))
                   :api [:POST "/todos" {:params todo}]
                   :dispatch-later [{:ms 3000 :dispatch [:notifications/remove notification]}]})))

(reg-event-db :notifications/remove
              (fn [db [_ n]]
                (update db :notifications (partial remove #(identical? n %)))))

(reg-event-fx :todos/remove
              (fn [{:keys [db]} [_ id]]
                {:db (update db :todos dissoc id)
                 :api [:DELETE (str "/todos/" id)]}))

(reg-event-db :todos/update [debug]
              (fn [db [_ id vals]]
                (update-in db [:todos id] merge vals)))

(reg-event-fx :todos/toggle
              (fn [{:keys [db]} [_ id]]
                (let [todo (get-in db [:todos id])
                      todo (update todo :completed not)
                      completed (:completed todo)]
                  {:db (assoc-in db [:todos id] todo)
                   :api [:PUT (str "/todos/" id) {:params todo}]})))

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
