(ns redolist.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub :todos
         (fn [db _]
           (vals (:todos db))))

(reg-sub :todos/completed :<- [:todos]
         (fn [todos _]
           (filter :completed todos)))
