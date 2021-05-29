(ns br.com.marinho.creditmodel.communication.consumer
  (:use clojure.pprint)
  (:import (java.util Properties)
           (org.apache.kafka.common.serialization StringDeserializer)
           (org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer)
           (java.time Duration)))

(defn- record-info? "Prints all (interesting) information from a record that was got on
  Kafka Client by the consumer. Information like the topic name and the key and value
  is also listed in the string. The function has just one parameter, referring to the
  record itself."
  [record]
  (str "=================Consumed message================="
       "\nTopic:" (.topic record)
       "\nPartition:" (.partition record)
       "\nTotal:" (.offset record)
       "\nTimestamp:" (.timestamp record)
       "\nKey:" (.key record)
       "\nValue:" (.value record)
       "\n=================================================="))

(defn- create-properties "Creates the properties used to run the Kafka consumer. Those
  properties refers to the deserialization of the values that are consumed from the
  server. Those values are in a String format and the deserialization is made using the
  `org.apache.kafka.common.serialization.StringDeserializer` class.
  The functions has a parameter referring the if of the group that is being consumed by
   the consumer."
  [group-id]
  (doto (Properties.)
    (.put ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG "127.0.0.1:9092")
    (.put ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG (.getName StringDeserializer))
    (.put ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG (.getName StringDeserializer))
    (.put ConsumerConfig/GROUP_ID_CONFIG group-id)))

(defn create-consumer "Creates a new Kafka consumer ready to consume a topic."
  [group-id]
  (KafkaConsumer. ^Properties (create-properties group-id)))

(defn consume "Function responsible for start the consumption of the informed topic(s)
  using the consumer, also passed as parameter. The function uses a recur to keep
  reading information until a stop command is sent. Also, the function has 3 parameters
   representing the functions used to
   1. validate the value got from the server,
   2. do an operation when the validation returns true,
   3. do another (or not) operation when the validation returns false."
  [consumer topic valid? success fail]
  (with-open [cons consumer]
    (.subscribe cons topic)
    (loop []
      (let [duration (Duration/ofSeconds 1)
            poll-records (seq (.poll cons duration))]
        (when poll-records
          (let [record (first poll-records)]
            (println (record-info? record))
            (if (valid? record)
              (success record)
              (fail record)))))
      (recur))))