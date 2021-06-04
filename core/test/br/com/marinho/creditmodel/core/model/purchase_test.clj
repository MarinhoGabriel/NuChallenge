(ns br.com.marinho.creditmodel.core.model.purchase-test
  (:require [clojure.test :refer :all]
            [br.com.marinho.creditmodel.core.controller :as controller])
  (:import (java.text SimpleDateFormat))
  (:use clojure.pprint))

(defn- init "Initializes the test file by creating the URI used to connect and
  disconnect from database. The function runs in the beginning of the code and just once."
  []
  (controller/define-uri "datomic:dev://localhost:4334/creditmodeltest"))

(defn- setup "Sets the connection and starts it. The function runs at the beginning of
  each test."
  []
  (controller/start-connection)
  (controller/start-controller))

(defn- tear-down "Clears the database to start another test set."
  []
  (controller/drop-database))

; Calling init function.
(init)

; Defining the format variable, used to parse and format Date values.
(def date-format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss"))

(deftest purchase-creation
  (setup)
  (controller/save-client! "Gabriel" "000000" "gabriel@email.com")
  (controller/save-client! "Marinho" "111111" "marinho@email.com")
  (controller/add-card-to-client! "000000")

  (testing "If the purchase was created with all attribute values saved correctly."
    (controller/save-purchase! "McDonalds" "food" 20.99
                               (.parse date-format "2021-04-13 13:46:28") "000000")
    (let [purchase (first (controller/purchases-by-client? "000000"))]
      (is (true?
            (and
              (= "McDonalds" (:purchase/merchant purchase))
              (= "food" (:purchase/category purchase))
              (= 20.99 (:purchase/price purchase))
              (= "2021-04-13 13:46:28" (.format date-format (:purchase/date purchase))))))))

  (testing "If there is a problem if we try to insert a purchase on a client without
  card. The function must throw a NullPointerException with a \"Client has not a
  card\" message."
    (is (thrown-with-msg? NullPointerException #"Client has not a card."
                          (controller/save-purchase! "McDonalds" "food" 20.99
                                                     (.parse date-format "2021-04-13 13:46:28")
                                                     "111111"))))

  (testing "If the purchase is not inserted if the merchant name is nil."
    (is (thrown-with-msg? NullPointerException #"Fields cannot be nil."
                          (controller/save-purchase! nil "food" 20.99
                                                     (.parse date-format "2021-04-13 13:46:28")
                                                     "000000"))))
  (tear-down))

(deftest purchase-consumption
  (setup)
  (controller/save-client! "Gabriel" "000000" "gabriel@email.com")
  (controller/save-client! "Marinho" "111111" "marinho@email.com")
  (controller/add-card-to-client! "000000")
  (controller/add-card-to-client! "111111")

  (testing "If nothing is gonna be returned if no purchase was saved."
    (is (empty? (controller/get-purchase))))

  ;; Saving purchases.
  (controller/save-purchase! "McDonald's" "Food" 43.25
                             (.parse date-format "2021-03-19 21:05:33") "000000")
  (controller/save-purchase! "Nike" "Clothing" 749.99
                             (.parse date-format "2021-02-19 06:54:46") "111111")
  (controller/save-purchase! "Swedish Fish & Chips" "Food" 62.94
                             (.parse date-format "2021-01-10 10:47:09") "000000")
  (controller/save-purchase! "Au Vieux Grec" "Food" 226.60
                             (.parse date-format "2021-04-21 14:32:57") "000000")
  (controller/save-purchase! "Zoes Juice" "Food" 149.23
                             (.parse date-format "2021-03-27 08:30:40") "111111")
  (controller/save-purchase! "Adidas" "Clothing" 42.24
                             (.parse date-format "2021-03-03 10:11:14") "111111")
  (controller/save-purchase! "Abebe Skin" "Health care" 110.69
                             (.parse date-format "2021-03-31 09:11:53") "000000")

  (testing "If all purchases are going to be returned."
    (is (= 7 (count (controller/get-purchase)))))

  (testing "If the purchase count for one client is correct."
    (is (= 4 (count (controller/purchases-by-client? "000000")))))

  (testing "If the purchase count for one client is correct."
    (is (= 3 (count (controller/purchases-by-client? "111111")))))
  (tear-down))