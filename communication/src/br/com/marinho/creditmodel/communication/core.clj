(ns br.com.marinho.creditmodel.communication.core
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.core.model.purchase :as purchase]
            [clojure.data.json :as json])
  (:import (java.text SimpleDateFormat)))

(def producer (producer/create-producer))
(def date-format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss"))

;(let [new-purchase (purchase/new-purchase "Netflix"
;                                          "Streaming"
;                                          28.90
;                                          (.parse date-format "2021-03-19 21:05:33")
;                                          5888873461857775)]
;  (producer/send-message! producer
;                          "new_purchase"
;                          (json/write-str new-purchase)
;                          (json/write-str new-purchase)))