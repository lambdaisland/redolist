(ns redolist.subs
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(re-frame/reg-sub
 :visible-todos
 (fn [db query-v]
   (remove :completed (:todos db))))
