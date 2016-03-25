(ns app.core
  ""
  (:require
   [ini-editor.core :as ini-editor]
   [remote-manager.core :as remote-manager]
   [utils.server-interop :as server-interop]
   [utils.widgets :as widgets]
   [utils.core :as utils]
   [reagent.core :as r :refer [atom]]))


(defonce app-state (atom {:tab "Home"}))
(defn set-tab! [label] (swap! app-state assoc :tab label))

(defn home
  "reagent-component for home tab."
  [props]
  [:div {} "Home Tab"])

(defn app
  "Reagent component which describes the app. It is a tab bar followed by the
  contents of that tab. Here are the locations of the contents:
  \"Home\" - `machine-conf.core/home`
  \"Remote\" - `remote-manager.core/view`
  \"INI\" - `ini-editor.core/view`"
  [props]
  (let [tab (:tab @app-state)]
    [:div.app {}
     [widgets/tabs {:labels ["Home" "Remote" "INI"]
                    :selected tab
                    :on-change #(set-tab! %1)}]
     [:div.tab-content {:style {:margin "1rem"}}
      [:div.tab-pane.active {}
      (cond
        (= tab "Home") [home {}]
        (= tab "Remote") [remote-manager/view {}]
        (= tab "INI") [ini-editor/view {}]
        :else [:div {} "Unknown tab"])]]]))

(defn ^:export start
  "Renders the application onto the DOM element \"app\""
  []
  (r/render-component
   [app {}]
   (.getElementById js/document "app")))