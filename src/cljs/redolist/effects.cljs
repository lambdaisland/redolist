(ns redolist.effects
  (:require [re-frame.core :as rf :refer [reg-fx]]
            [ajax.core :as ajax]))

(reg-fx :api (fn [[method uri opts]]
               (let [{:keys [dispatch dispatch-error]} opts
                     opts (cond-> opts
                            dispatch (assoc :handler
                                            (fn [res]
                                              (println "response! " res)
                                              (rf/dispatch (conj dispatch res))))
                            dispatch-error (assoc :error-handler
                                                  (fn [res]
                                                    (rf/dispatch (conj dispatch res)))))]
                 (ajax/easy-ajax-request uri (name method) (dissoc opts :dispatch :dispatch-error)))))
