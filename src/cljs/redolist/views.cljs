(ns redolist.views
    (:require [re-frame.core :as re-frame :refer [dispatch subscribe]]
              [reagent.core :as r]
              [redolist.helpers :refer [class->]]))

(def enter-key-code 13)

(defn when-enter [key-event callback]
  (let [key-pressed (.-which key-event)]
    (when (= key-pressed enter-key-code)
      (callback))))

(defn todos-toggle []
  (let [all-complete? (subscribe [:todos/all-complete?])]
    (fn []
      [:span
       [:input#toggle-all {:type "checkbox"
                           :checked @all-complete?
                           :on-change #(dispatch [:todos/toggle-all])}]
       [:label {:for "toggle-all"} "Mark all as complete"]])))

(defn todo-input []
  (let [title (r/atom "")]
    (fn []
      [:input#new-todo {:type "text"
                        :value @title
                        :placeholder "What needs to be done?"
                        :auto-focus true
                        :on-change #(reset! title (-> % .-target .-value))
                        :on-key-down #(when-enter %
                                        (fn []
                                          (dispatch [:todos/add @title])
                                          (reset! title "")))}])))

(defn todo-checkbox [id completed]
  [:input.toggle {:type "checkbox"
                  :checked completed
                  :on-change #(dispatch [:todos/toggle id])}])

(defn todo-edit [todo]
  (let [title (r/atom (:title todo))]
    (fn [{:keys [id] :as todo}]
      (let [dispatch-update #(dispatch [:todos/update id {:title @title
                                                          :editing false}])]
        [:input.edit {:type "text"
                      :value @title
                      :on-change #(reset! title (-> % .-target .-value))
                      :on-blur dispatch-update
                      :on-key-down #(when-enter % dispatch-update)
                      :auto-focus true}]))))

(defn todo-item [todo]
  (fn [{:keys [id completed title editing] :as todo}]
    [:li {:class (class-> completed "completed "
                          editing   "editing")}
     [:div.view
      [todo-checkbox id completed]
      [:label
       {:on-double-click #(dispatch [:todos/update id {:editing true}])
        :unselectable "on"
        :style {:user-select "none"
                :-moz-user-select "none"}}
       title]
      [:button.destroy {:on-click #(dispatch [:todos/remove id])}]]
     (if editing
       [todo-edit todo])]))

(defn todos-list []
  (let [todos (subscribe [:todos/visible])]
    (fn []
      [:ul#todo-list
       (for [todo @todos]
         ^{:key (:id todo)} [todo-item todo])])))



(defn todos-count []
  (let [active-count (subscribe [:todos/active-count])]
    (fn []
      [:span#todo-count
       [:strong @active-count]
       (if (= 1 @active-count) " item " " items ") "left"])))

(defn todos-filters []
  (let [display-type (subscribe [:display-type])]
    (fn []
      (let [selected #(if (= @display-type %) "selected" "")]
        [:ul#filters
         [:li [:a {:class (selected :all)  :href "#/"} "All"]]
         [:li [:a {:class (selected :active) :href "#/active"} "Active"]]
         [:li [:a {:class (selected :completed) :href "#/completed"} "Completed"]]]))))

(defn main-panel []
  (let [todos-empty? (subscribe [:todos/empty?])]
    (fn []
      [:div
       [:section#todoapp
        [:header#header
         [title]
         [todo-input]]
        (if-not @todos-empty?
          [:div
           [:section#main
            [todos-toggle]
            [todos-list]]
           [:footer#footer
            [todos-count]
            [todos-filters]]])]])))
