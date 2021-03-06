(ns viz.controls
  "Move the camera around using the mouse in a 3D three scene. Main
  functionality is implemented in JavaScript."
  (:require
   [cljsjs.trackball-controls]
   [cljsjs.three]))

(defn new-controls!
  "Creates controls for the camera. It steals mouse actions such as the mouse
  wheel. Use `stop-controls!` to give back mouse actions."
  [camera]
  (js/TrackballControls. camera))

(defn update-controls!
  [controls]
  (if (some? controls)
    (.update controls)))

(def dispose!
  (js/eval "var disposeffix=function(o){o.dispose()};disposeffix"))

(defn stop-controls!
  [controls]
  (if (some? controls)
    (dispose! controls))
  nil)
