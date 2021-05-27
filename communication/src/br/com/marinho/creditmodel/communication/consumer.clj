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
  (str "Topic:" (.topic record)
       "\n\tPartition:" (.partition record)
       "\n\tTotal:" (.offset record)
       "\n\tTimestamp:" (.timestamp record)
       "\n\tKey:" (.key record)
       "\n\tValue:" (.value record)))

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
  reading information until a stop command is sent."
  [consumer topic]
  (with-open [cons consumer]
    (.subscribe cons topic)
    (loop []
      (let [duration (Duration/ofSeconds 1)
            poll-records (seq (.poll cons duration))]
        (when poll-records (println (record-info? (first poll-records))))
        (recur)))))