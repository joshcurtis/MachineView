(ns ini-editor.view
  "Provides the view for the INI editor."
  (:require
   [ini-editor.controller :as controller]
   [ini-editor.model :as model]
   [ini-editor.parser :as parser]
   [machine-conf.utils :as utils]
   [machine-conf.widgets :as widgets]
   [reagent.core :as r :refer [atom]]))

(defn- ini-key
  [props]
  (let [{:keys [section key metadata value]} props
        comments (:comments metadata)
        type (or (:type metadata) "text")
        disabled false]
    [:div.ini-key.form-group {}
     [:label.control-label {:title comments} key]
     (cond
       (= type "text")
       [:input.form-control {:disabled disabled
                             :on-change #(->>
                                          %1
                                          .-target
                                          .-value
                                          (controller/set-ini-value! section key))
                             :value value
                             :type "text"}]

       (= type "options") [widgets/dropdown-input
                           {:disabled disabled
                            :options (:options metadata)
                            :on-change #(controller/set-ini-value! section key %1)
                            :value value}]

       (= type "multiline") [widgets/list-input
                             {:disabled disabled
                              :on-change #(controller/set-ini-value! section key %1)
                              :value value
                              :default-value ""}]

       :else
       [:span {} (str "Unknown:" props)])]))

(defn- ini-section
  [props]
  (let [{:keys [section
                key-metadata
                key-order
                metadata
                values
                expanded?]} props
        comments (:comments metadata)]
    [:div.ini-section.panel.panel-default {}
     [:div.panel-heading {}
      [:legend.panel-title {:stlye {:font-weight "bold"}
                            :title comments
                            :on-click #(controller/toggle-expanded! section)}
       section]]
    [:fieldset {}
      (if expanded?
        [:div.panel-body.form-group {}
         (map (fn [k] [ini-key {:section section
                                :key k
                                :metadata (get key-metadata k)
                                :value (get values k)}])
              key-order)])]]))

(defn ini-editor
  "Renders a component for editing the current ini.
  # Props - same hashmap are present in `ini-editor/model`.
  :key-metadata
  :key-order
  :section-metadata
  :section-order
  :values
  :expanded?"
  [props]
  (let [{:keys [key-metadata
                key-order
                section-metadata
                section-order
                values
                expanded?]} props]
    [:div.ini-editor {:style {:margin "1rem"} }
     [:h1 {} "MachineKit INI Configuration"]
     (map (fn [section] [ini-section {:key section ;; for react/reagent
                                      :section section
                                      :key-metadata (get key-metadata section)
                                      :key-order (get key-order section)
                                      :metadata (get section-metadata section)
                                      :values (get values section)
                                      :expanded? (contains? expanded? section)}])
          section-order)]))

(defn menubar
  "Renders a menubar for misc. actions such as loading and saving a file."
  [props]
  (let []
    [:ul.nav.nav-pills {}
     [:li.dropdown {}
      [:a.dropdown-toggle {:data-toggle "dropdown"
                           :aria-expanded false}
       [:span {} "File"]]
      [:ul.dropdown-menu {}
       [:li {} [:a {} [widgets/file-input
                       {:id "file-input"
                        :file-types ".ini"
                        :element "Open"
                        :on-change #(if (first %1)
                                      (utils/read-file
                                       (first %1)
                                       controller/load-str!))}]]]
       [:li {} [:a {} [widgets/file-save {:element "Save"
                                          :filename "configuration.ini"
                                          :str-func model/ini-str}]]]]]]))