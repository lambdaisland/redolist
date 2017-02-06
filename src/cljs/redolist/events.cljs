(ns redolist.events
  (:require [re-frame.core :refer [reg-event-db]]
            [redolist.db :as db]))

(reg-event-db :initialize-db
              (fn  [_ _]
                db/default-db))

(reg-event-db :set-display-type
              (fn [db [_ type]]
                (assoc db :display-type type)))
