(ns ^{:doc "Tests for RemoteWebDriver server and client (driver) code"}
  clj-webdriver.test.remote
  (:use clojure.test
        [clj-webdriver.core :only [quit to]]
        [clj-webdriver.test.config :only [base-url]]
        [clj-webdriver.test.util :only [start-server]]
        [clj-webdriver.test.common :only [run-common-tests]]
        [clj-webdriver.remote.server :only [new-remote-session stop]])
  (:import [java.util.logging Level]))

(def server (atom nil))
(def driver (atom nil))

;; Fixtures
(defn start-session-fixture
  [f]
  (let [[this-server this-driver] (new-remote-session {:port 3003}
                                                      {:browser :firefox})]
    (-> this-driver :webdriver (.setLogLevel Level/OFF))
    (reset! server this-server)
    (reset! driver this-driver))
  (f))

(defn reset-browser-fixture
  [f]
  (to @driver (base-url))
  (f))

(defn quit-fixture
  [f]
  (f)
  (quit @driver)
  (stop @server))

(use-fixtures :once start-server start-session-fixture quit-fixture)
(use-fixtures :each reset-browser-fixture)

;; RUN TESTS HERE
(deftest test-common-features-via-remote-server
  (run-common-tests @driver))
