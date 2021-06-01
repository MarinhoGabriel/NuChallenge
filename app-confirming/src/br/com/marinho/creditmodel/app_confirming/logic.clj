(ns br.com.marinho.creditmodel.app-confirming.logic
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.communication.consumer :as consumer]
            [clojure.data.json :as json]))

; Creating the producers that are going to send messages to the topics called
; "available_limit_or_balance" and "canceled_purchase", to inform that the purchase was
; confirmed in the case of not confirmed, respectively.
(def producer (producer/create-producer))

; Creating the consumer to consume the values coming from the Kafka server topic called
; "valid_purchase".
(def consumer (consumer/create-consumer "br.com.marinho.creditmodel.appconfirming.userconfirm"))

(defn- confirm? "Confirms a purchase by reading the input from the terminal. If the
  input value is equal to 'confirm', the purchase is confirmed and a message is sent to
  the kafka broker topic called 'available_limit_or_balance' with the purchase
  information."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)
        c (consumer/create-consumer "br.com.marinho.creditmodel.appconfirming.userconfirm.confirmation")]
    (println "Please, confirm the purchase at"
             (:merchant purchase)
             "worth R$"
             (:value purchase)
             "in the app.")
    (let [record (consumer/consume-once c ["user_confirmation"])]
      (println "message received" (.value record))
      (= "confirm" (.value record)))))

(defn- produce-message "Produces a message to send to the kafka client using the
  `producer`. The function has the three parameters needed to use the
  `producer/send-message!` function."
  [topic key value]
  (producer/send-message! producer topic key value))

(defn- send-successful-message! "If the return of the function `confirm?` returns
 `true`, this function is called to do the procedure when it's confirmed."
  [value]
  (println "Successful operation! Purchase confirmed!")
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message "available_limit_or_balance"
                     (str {:service "br.com.marinho.creditmodel.appconfirming"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn send-failure-message! "If the return of the function `confirm?` returns
  `false`, this function is called to do the procedure when it's not confirmed."
  [value]
  (println "Purchase unconfirmed. Canceling the purchase.")
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message "canceled_purchase"
                     (str {:service "br.com.marinho.creditmodel.appconfirming"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn start-consumption "Starts the consumer agent."
  []
  (println "Starting App Confirming Service...")
  (consumer/consume consumer
                    ["valid_purchase"]
                    (fn [value] (confirm? value))
                    (fn [value] (send-successful-message! value))
                    (fn [value] (send-failure-message! value))))