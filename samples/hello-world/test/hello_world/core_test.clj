(ns hello-world.core-test
  (:require [clojure.test :refer :all]
            [hello-world.core :as core]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.test :refer :all]))

(defn service
  ([]
     (service core/service))
  ([serv-map]
     (::bootstrap/service-fn (bootstrap/create-servlet serv-map))))

(defn get
  "Make a GET request on our service using response-for."
  [& args]
  (apply response-for (service) :get args))

(deftest hello-world-test
  (testing "/hello endpoint"
    (let [default-response (get "/hello")
          custom-response (get "/hello" :query-params {:name "globe"})]
      (is
       (= (:body default-response) "Hello, World!")
       (= (:body custom-response) "Hello, globe!")))))
