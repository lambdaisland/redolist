(ns redolist.undo
  (:require [re-frame.core :as re-frame]))

(def undo-stack (atom ()))

(defn undoable []
  (re-frame/->interceptor
   :id :undoable
   :before (fn [context]
             (swap! undo-stack #(conj % (get-in context [:coeffects :db])))
             context)))

(defn undo-handler [_ _]
  (let [db (first @undo-stack)]
    (swap! undo-stack next)
    db))
