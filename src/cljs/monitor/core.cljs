(ns monitor.core
  "Shows some measurements."
  (:require
   [utils.widgets :as widgets]
   [utils.core :as utils]
   [reagent.core :as r :refer [atom]]))

(def topbar-actions {})

(defn rand-temp [bias]
  (+ bias (rand 40)))

(defn default-measurements
  []
  {:time [0]
   :extruders [[(rand-temp 280)]
               [(rand-temp 300)]
               [(rand-temp 320)]]})

(defonce measurements (atom (default-measurements)))

(defn reset-measurements! []
  (reset! measurements (default-measurements)))

(defn update-measurements [measurements]
  (let [t (:time measurements)
        [a b c] (:extruders measurements)]
    (assoc measurements
           :time (conj t (inc (last t)))
           :extruders [(conj a (rand-temp 280))
                       (conj b (rand-temp 300))
                       (conj c (rand-temp 320))])))

(def update-interval 3000)

(utils/set-interval "update-measurements"
                    #(swap! measurements update-measurements)
                    update-interval)

(defn contents
  "A view that can be rendered to monitor the machinekit configuration. It is
  used in app/core.cljs. This returns a reagent component that takes no props."
  [props]
  (let [measurements @measurements
        {:keys [time extruders]} measurements]
    [:div
     [widgets/line-plot {:data (mapv (fn [i] {:x time
                                              :y (get extruders i)
                                              :name (str "Extruder-" (inc i))
                                              :mode "lines"})
                                     (range 3))
                         :layout {:title "Extruder Temperatures"}}]
     [:button.btn.btn-default {:on-click reset-measurements!}
      "Reset"]]))
