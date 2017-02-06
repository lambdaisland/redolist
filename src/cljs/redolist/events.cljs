(ns redolist.events
    (:require [re-frame.core :as re-frame]
              [redolist.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
