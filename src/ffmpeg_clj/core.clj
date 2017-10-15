(ns ffmpeg-clj.core
    (:require
      [clojure.java.shell :refer [sh]]
      [clojure.string :as s]))

(def ^:dynamic *bin* "ffmpeg")

(defn create-cmd-string-seq [& argv]
      (->> argv
           (map #(if (keyword? %) (str "-" (name %)) (str %)))
           (into [*bin*])))

(defn ffmpeg-factory [shell-call-fn]
      (fn [& args]
          (let [cmd! (apply shell-call-fn (apply create-cmd-string-seq args))
                {:keys [exit out err]} cmd!]
               (when-not (zero? exit)
                         (throw
                           (Exception. err)))
               out)))

(def ffmpeg! (ffmpeg-factory sh))

(defn version []
      (as-> (ffmpeg! "-version") o
            (re-find #"version \S+" o)
            (s/split o #" ")
            (last o)))
