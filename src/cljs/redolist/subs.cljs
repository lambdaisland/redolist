(ns redolist.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [reg-sub]]))


(reg-sub :display-type
         (fn [db]
           (println "display-type")
           (:display-type db)))

(reg-sub :todos
         (fn [db]
           (println "todos")
           (vals (:todos db))))

(reg-sub :todos/visible
         :<- [:display-type]
         :<- [:todos]
         :<- [:todos/completed]
         :<- [:todos/active]
         (fn [[type all completed active]]
           (println "todos/visible")
           (case type
             :all all
             :completed completed
             :active active)))

(reg-sub :todos/completed :<- [:todos]
         (fn [todos]
           (println "todos/completed")
           (filter :completed todos)))

(reg-sub :todos/active :<- [:todos]
         (fn [todos]
           (println "todos/active")
           (filter (complement :completed) todos)))

(reg-sub :todos/empty? :<- [:todos]
         (fn [todos]
           (println "todos/empty?")
           (empty? todos)))

(reg-sub :todos/all-complete? :<- [:todos/active]
         (fn [active]
           (println "todos/all-complete?")
           (empty? active)))

(reg-sub :todos/active-count :<- [:todos/active]
         (fn [active]
           (println "todos/active-count")
           (count active)))
