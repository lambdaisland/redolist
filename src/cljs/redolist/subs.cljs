(ns redolist.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub :display-type (fn [db _] (:display-type db)))

(reg-sub :todos
         (fn [db _]
           (vals (:todos db))))

(reg-sub :todos/completed :<- [:todos]
         (fn [todos _]
           (filter :completed todos)))

(reg-sub :todos/active :<- [:todos]
         (fn [todos _]
           (remove :completed todos)))

(reg-sub :todos/empty? :<- [:todos]
         (fn [todos]
           (empty? todos)))

(reg-sub :todos/all-complete? :<- [:todos/active]
         (fn [active]
           (empty? active)))

(reg-sub :todos/active-count :<- [:todos/active]
         (fn [active]
           (count active)))

(reg-sub :todos/visible
         :<- [:display-type]
         :<- [:todos]
         :<- [:todos/completed]
         :<- [:todos/active]
         (fn [[type all completed active] query-v]
           (case type
             :all all
             :completed completed
             :active active)))
