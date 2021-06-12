(ns br.com.marinho.creditmodel.purchaser.logic
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.communication.consumer :as consumer]
            [br.com.marinho.creditmodel.core.constants.kafka-topics :as topics]
            [clojure.data.json :as json]
            [br.com.marinho.creditmodel.core.controller :as controller])
  (:import (java.text SimpleDateFormat)))

; Date formatter to save the date as an object.
(def date-format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSz"))

; Creating the producers that are going to send messages to the topics called
; "available_limit_or_balance" and "canceled_purchase", to inform that the purchase was
; confirmed in the case of not confirmed, respectively.
(def producer (producer/create-producer))

; Creating the consumer to consume the values coming from the Kafka server topic called
; "valid_purchase".
(def consumer (consumer/create-consumer "br.com.marinho.creditmodel.purchaser"))

(defn- produce-message "Produces a message to send to the kafka client using the
  `producer`. The function has the three parameters needed to use the
  `producer/send-message!` function."
  [topic key value]
  (producer/send-message! producer topic key value))

(defn- send-successful-message! "Sends a message to kafka server indicating the
  successfulness of the purchasing."
  [value]
  (when (nil? controller/uri)
    (controller/define-uri "datomic:dev://localhost:4334/creditmodel")
    (controller/start-connection))
  (let [purchase (json/read-str (.value value) :key-fn keyword)
        card (ffirst (controller/card-by-number (:card purchase)))]
    (controller/save-purchase-with-card! (:merchant purchase)
                                         (:category purchase)
                                         (:value purchase)
                                         (.parse date-format (:date purchase))
                                         (:card/id card))
    (produce-message topics/PURCHASE_CONCLUDED
                     (str {:service "br.com.marinho.creditmodel.purchaser"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn start-consumption "Starts the consumer agent."
  []
  (println "Starting Purchaser Service...")
  (consumer/consume consumer
                    [topics/AVAILABLE_LIMIT]
                    (fn [value] true)
                    (fn [value] (send-successful-message! value))
                    (fn [value] true)))
