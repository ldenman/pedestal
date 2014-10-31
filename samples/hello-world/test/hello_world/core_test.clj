(ns hello-world.core-test
  (:require [clojure.test :refer :all]
            [hello-world.core :as core]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.test :refer :all]))

(def service-map core/service)

(defn service
  ([]
     (service service-map))
  ([serv-map]
     (::bootstrap/service-fn (bootstrap/create-servlet serv-map))))

(defn get [path]
  (response-for (service) :get path))

(deftest hello-world-test
  (testing "/hello endpoint"
    (are [resp expected] (= (:body resp) expected)
         (get "/hello") "Hello, World!"
         (get "/hello?name=Globe") "Hello, Globe!")))
