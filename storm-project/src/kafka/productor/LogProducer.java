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
/**
 * UV Êý¾Ý
 */
public class LogProducer extends Thread {
	private final kafka.javaapi.producer.Producer<Integer, String> producer;
	private final String topic;
	private final Properties props = new Properties();

	public LogProducer(String topic) {
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "192.168.60.134:9092,192.168.60.135:9092");
		// Use random partitioner. Don't need the key type. Just set it to Integer.
		// The message is of type String.
		producer = new kafka.javaapi.producer.Producer<Integer, String>(new ProducerConfig(props));
		this.topic = topic;
	}
	
	/**
	 * host,session_id,date
	 */
	public void run() {
		Random random = new Random();
		String[] hosts = { "www.taobao.com" };
		String[] session_id = { "ABYH6Y4V4SCVXTG6DPB4VH9U123", "XXYH6YCGFJYERTT834R52FDXV9U34",
				"BBYH61456FGHHJ7JL89RG5VV9UYU7", "CYYH6Y2345GHI899OFG4V9U567", "VVVYH6Y4V4SFXZ56JI111PDPB4V678" };

		int i = 0;
		while (true) {
			i++;
			String messageStr = new String(hosts[0] + "\t" +
					session_id[random.nextInt(5)] + i + "\t"+ 
					DateFmt.getCountDate(null, DateFmt.date_long) + "\t");

			System.err.println("product : " + messageStr);
			producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
			Utils.sleep(500);
		}
	}

	public static void main(String[] args) {
		LogProducer producerThread = new LogProducer(KafkaProperties.Log_topic);
		producerThread.start();
	}

}
