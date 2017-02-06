(ns redolist.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub :todos
         (fn [db _]
           (vals (:todos db))))
