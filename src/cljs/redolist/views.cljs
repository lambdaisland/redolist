(ns redolist.views
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [reagent.core :as r]
            [redolist.helpers :refer [class->]]))

(def <sub (comp deref subscribe))

(defn toggle-all-checkbox []
  [:span
   [:input#toggle-all {:type "checkbox"
                       :checked (<sub [:todos/all-complete?])}]
   [:label {:for "toggle-all"} "Mark all as complete"]])

(defn todo-input []
  (let [title (r/atom "")]
    (fn []
      [:input#new-todo {:type "text"
                        :value @title
                        :placeholder "What needs to be done?"
                        :auto-focus true
                        :on-change #(reset! title (-> % .-target .-value))}])))

(defn todo-checkbox [id completed]
  [:input.toggle {:type "checkbox"
                  :checked completed}])

(defn todo-edit [id]
  (let [todo (subscribe [:todo-by-id id])
        title (r/atom (:title @todo))]
    (fn [id]
      [:input.edit {:type "text"
                    :value @title
                    :on-change #(reset! title (-> % .-target .-value))
                    :auto-focus true}])))

(defn todo-item [todo]
  (fn [{:keys [id completed title editing] :as todo}]
    [:li {:class (class-> completed "completed "
                          editing   "editing")}
     [:div.view
      [todo-checkbox id completed]
      [:label
       {:unselectable "on"
        :style {:user-select "none"
                :-moz-user-select "none"}}
       title]
      [:button.destroy {}]]
     (when editing
       [todo-edit id])]))

(defn todo-list []
  [:div#todo-list
   (for [todo (<sub [:todos])]
     ^{:key (:id todo)} [todo-item todo])])

(defn todo-count []
  (let [active-count (<sub [:todos/active-count])]
    [:span#todo-count
     [:strong active-count]
     (if (= 1 active-count) " item " " items ") "left"]))

(defn todo-filters []
  (let [selected #(if (= (<sub [:display-type]) %) "selected" "")]
    [:ul#filters
     [:li [:a {:class (selected :all)  :href "#/"} "All"]]
     [:li [:a {:class (selected :active) :href "#/active"} "Active"]]
     [:li [:a {:class (selected :completed) :href "#/completed"} "Completed"]]]))

(defn main-panel []
  [:div
   [:section#todoapp
    [:div
     [:header#header
      [todo-input]]
     (when-not (<sub [:todos/empty?])
       [:div
        [:section#main
         [toggle-all-checkbox]
         [todo-list]]
        [:footer#footer
         [todo-count]
         [todo-filters]]])]]])
