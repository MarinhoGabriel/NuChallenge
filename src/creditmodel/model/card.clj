(ns creditmodel.model.card
  (:import (java.text SimpleDateFormat)))

; Date format used to format the card validity date.
(def date-format (SimpleDateFormat. "MM/yyyy"))

(defn new-card "Creates a new card."
  []
  {:card/number   (long (rand 10000000000000000))
   :card/cvv      (long (rand 1000))
   :card/validity (.parse date-format (str (+ 1 (long (rand 12))) "/" (+ 2021 (long (rand 10)))))
   :card/limit    (* (+ (double (rand 10)) 1) 1000)})