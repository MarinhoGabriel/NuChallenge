(defproject communication "0.1.0"
  :description "Communication module using Kafka for communicating between the services."
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.kafka/kafka-clients "2.8.0"]
                 [org.slf4j/slf4j-api "1.6.1"]]
  :repl-options {:init-ns br.com.marinho.creditmodel.communication.core})