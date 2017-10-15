(ns ffmpeg-clj.core-test
    (:require [clojure.test :refer :all]
      [ffmpeg-clj.core :refer :all]
      [clojure.string :as s]))

(deftest cmd-creates-command-with-only-ffmpeg-when-no-args-provided
         (testing "If no args are provided to (cmd) it should create a list with only the command in it (\"ffmpeg\")."
                  (is (= (create-cmd-string-seq) ["ffmpeg"]))))

(deftest cmd-creates-command-with-one-option-and-one-value
         (testing "A ffmpeg call with an option (using a - before) from a keyword and a value from a string should be created."
                  (is (= (create-cmd-string-seq :codec "h264") ["ffmpeg", "-codec", "h264"]))))

(deftest cmd-combination-of-many-keywords-and-values
         (testing "Keywords must be seen as options for ffmpeg and string values as parameters"
                  (is (= (create-cmd-string-seq "value1" :option1 "value2" "value3" :option2 "value4" :option3 :option4) ["ffmpeg", "value1", "-option1", "value2", "value3", "-option2", "value4", "-option3", "-option4"]))))


(defn mocked-sh
      "A mocked sh function that return a map with the result values as in the arguments"
      [exit-code stdout err]
      (fn [& arguments-ignored-in-this-mock]
          {:exit exit-code :out  stdout :err err}))

(deftest ffmpeg-clj-when-exit-code-from-sh-call-is-not-zero-throws-err-as-exception
         (testing "Should throw the error of the shell call if the exit code is not 0"
                  (is (thrown? Exception ((ffmpeg-factory (mocked-sh 1 nil "Error")) "irrelevant_value")))))

(deftest ffmpeg-clj-when-exit-code-from-the-sh-is-zero-returns-the-out-value-call
         (testing "Should return the output of the shell call if the exit code is 0"
                  (is (= ((ffmpeg-factory (mocked-sh 0 "Test stdout value" nil)) "irrelevant_value") "Test stdout value"))))

(deftest ffmpeg-version
         (testing "Test the version of ffmpeg"
                  (is (not (s/blank? (version))))))