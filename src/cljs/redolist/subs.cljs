(ns redolist.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [reg-sub]]))


(reg-sub :display-type
         (fn [db]
           (:display-type db)))

(reg-sub :todos
         (fn [db]
           (vals (:todos db))))

(reg-sub :notifications
         (fn [db]
           (:notifications db)))

(reg-sub :todos/visible
         :<- [:display-type]
         :<- [:todos]
         :<- [:todos/completed]
         :<- [:todos/active]
         (fn [[type all completed active]]
           (case type
             :all all
             :completed completed
             :active active)))

(reg-sub :todos/completed :<- [:todos]
         (fn [todos]
           (filter :completed todos)))

(reg-sub :todos/active :<- [:todos]
         (fn [todos]
           (filter (complement :completed) todos)))

(reg-sub :todos/empty? :<- [:todos]
         (fn [todos]
           (empty? todos)))

(reg-sub :todos/all-complete? :<- [:todos/active]
         (fn [active]
           (empty? active)))

(reg-sub :todos/active-count :<- [:todos/active]
         (fn [active]
           (count active)))
