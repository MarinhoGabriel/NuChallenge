(ns br.com.marinho.creditmodel.logger.core
  (:import (java.util.regex Pattern))
  (:require [br.com.marinho.creditmodel.communication.consumer :as consumer]))

(defn run "Runs the logger service to inform all information about the services running
  on Kafka Client."
  []
  (let [consumer (consumer/create-consumer "br.com.marinho.creditmodel.logger")]
    (consumer/consume consumer
                      (Pattern/compile "br.com.marinho.creditmodel.*")
                      (fn [value] true)
                      (fn [value])
                      (fn [value]))))

; Running the service (in loop)
(run)