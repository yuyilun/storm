/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kafka.productor;

import java.util.Properties;
import java.util.Random;

import backtype.storm.utils.Utils;
import cloudy.tools.DateFmt;
import kafka.KafkaProperties;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class SaleTopProducer extends Thread {
	private final kafka.javaapi.producer.Producer<Integer, String> producer;
	private final String topic;
	private final Properties props = new Properties();

	public SaleTopProducer(String topic) {
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "192.168.60.134:9092,192.168.60.135:9092");
		// Use random partitioner. Don't need the key type. Just set it to Integer.
		// The message is of type String.
		producer = new kafka.javaapi.producer.Producer<Integer, String>(new ProducerConfig(props));
		this.topic = topic;
	}

	public void run() {
		//order_id,order_amt,create_time,province_id
		Random random = new Random();
		String[] order_amt = { "14.00", "71.12", "42.42", "50.30", "89.00" };
		String[] province_id = { "1", "2", "3", "4", "5","6","7","8" };
		int i = 0;
		while (true) {
			i++;
			String messageStr = new String(i + "\t" + 
					order_amt[random.nextInt(5)] + "\t"+ 
					DateFmt.getCountDate(null, DateFmt.date_long) + "\t" + 
					province_id[random.nextInt(8)] + "\t");
			
			System.err.println("product : " + messageStr);
			producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
			Utils.sleep(200);
		}
	}

	public static void main(String[] args) {
		SaleTopProducer producerThread = new SaleTopProducer(KafkaProperties.order_topic);
		producerThread.start();
	}

}
