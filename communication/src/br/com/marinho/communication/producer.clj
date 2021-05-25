(ns br.com.marinho.communication.producer
  (:import (java.util Properties)
           (org.apache.kafka.clients.producer ProducerConfig ProducerRecord Callback
                                              KafkaProducer)
           (org.apache.kafka.common.serialization StringSerializer)))

(defn- metadata-info? "Prints the metadata information in a formatted way to make the
  visualization better. The function has just one input, that is the metadata and
  returns a string with the formatted information about this metadata."
  [metadata]
  (str "\nMetadata:\n\tTopic name: " (.topic metadata)
    "\n\tPartition: " (.partition metadata)
    "\n\tOffset: " (.offset metadata)
    "\n\tTimestamp: " (.timestamp metadata)))

; Symbol representing a send callback, used when the producer sends a  message to the
; server and we want to have access to the results of the operation.
(def callback (reify Callback
                (onCompletion [_ metadata exception]
                  (if exception
                    (println exception)
                    (println (metadata-info? metadata))))))

(defn- create-properties "Creates the properties used to run the Kafka producer. Those
  properties refers to the serialization of the values that are sent to the server.
  Those values are in a String format and the serialization is made using the
  `org.apache.kafka.common.serialization.StringSerializer` class."
  []
  (doto (Properties.)
    (.put ProducerConfig/BOOTSTRAP_SERVERS_CONFIG "127.0.0.1:9092")
    (.put ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG (.getName StringSerializer))
    (.put ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG (.getName StringSerializer))))

(defn- create-message "Creates and returns a message that is going to be sent by the
  producer. The function has three parameters representing the topic name, the key and
  value of the message."
  [topic key value]
  (ProducerRecord. topic key value))

(defn- create-producer "Creates a new Kafka producer ready to send a message. The
  function gets the properties as the only parameter."
  [properties]
  (KafkaProducer. ^Properties properties))

(defn send-message! "Sends a message to the server using the topic specifies in the
  parameter. The other two parameters refer to the key and value of the message that is
  gonna be sent."
  [topic-name key value]
  (let [properties (create-properties)
        producer (create-producer properties)
        message (create-message topic-name key value)]
    (.send producer message callback)
    (.flush producer)))