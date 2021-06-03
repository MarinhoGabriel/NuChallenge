(ns br.com.marinho.creditmodel.limit-checker.logic
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.communication.consumer :as consumer]
            [clojure.data.json :as json]
            [br.com.marinho.creditmodel.core.controller :as controller]))

; Creating the producers that are going to send messages to the topics called
; "available_limit_or_balance" and "canceled_purchase", to inform that the purchase was
; confirmed in the case of not confirmed, respectively.
(def producer (producer/create-producer))

; Creating the consumer to consume the values coming from the Kafka server topic called
; "valid_purchase".
(def consumer (consumer/create-consumer "br.com.marinho.creditmodel.limitchecker"))

(defn- has-limit? "Function responsible for checking is the received message,
  representing a new purchase, has limit to conclude the operation. If it does, the
  purchase is made and, if doesn't, the purchase is canceled."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)
        card (ffirst (controller/card-by-number (:card purchase)))]
    (>= (:card/limit card) (:value purchase))))

(defn- produce-message ""
  [topic key value]
  (producer/send-message! producer topic key value))

(defn- send-successful-message! ""
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message "conclude_purchase"
                     (str {:service "br.com.marinho.creditmodel.limitchecker"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn send-failure-message! ""
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message "canceled_purchase"
                     (str {:service "br.com.marinho.creditmodel.limitchecker"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn start-consumption "Starts the consumer agent."
  []
  (println "Starting Limit Checker Service...")
  (consumer/consume consumer
                    ["available_limit"]
                    (fn [value] (has-limit? value))
                    (fn [value] (send-successful-message! value))
                    (fn [value] (send-failure-message! value))))