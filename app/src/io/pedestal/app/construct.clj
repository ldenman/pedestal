(ns io.pedestal.app.construct
  (:require [io.pedestal.app.flow :as flow]
            [io.pedestal.app.model :as model]
            [io.pedestal.app.map :as mapper]
            [io.pedestal.app.route :as route])
  (:use [clojure.core.async :only [chan put!]]))

(defn- router-transforms [_ inform-message]
  (mapv (fn [[ _ event c config]]
          (cond (= event :channel-added)
                [[::router] :add [c config :*]]
                (= event :channel-removed)
                [[::router] :remove [c config :*]]))
        inform-message))

(defn build [init-model config]
  ;; TODO: How would we configure custom arg-fns for each mapper?
  ;; maybe the answer is: if you need to do that then wire it up yourself
  (let [in-config (conj (:in config) [router-transforms [::router] :*])
        in-transform-c (chan 10)
        router-c (chan 10)
        in-inform-c (mapper/inform->transforms in-config router-c)
        out-inform-c (mapper/inform->transforms (:out config) router-c)
        model-transform-c (if (:flow config)
                            (flow/transform->inform init-model (:flow config) out-inform-c)
                            (model/transform->inform init-model out-inform-c))
        router (route/router [::router] router-c)]
    (put! router-c [[::router] :add [model-transform-c [:info :* :**] :*]])
    in-inform-c))