package com.example.kafka.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka Producer サンプルAP
 * @author Administrator
 *
 */
public class ProducerMain {
	/** AP制御用プロパティ */
	/* トピック名 */
	static final String TOPIC_NAME = "topic.name";
	/* 送信メッセージ数 */
	static final String MSG_CNT = "message.cnt";
	/* メッセージテンプレート */
	static final String MSG_TEMPLATE = "message.template";
	/* 送信メッセージ待ち時間 */
	static final String WAIT_TIME = "wait.time";

	/** ロガー */
	private static final Logger log = LoggerFactory.getLogger(ProducerMain.class);

	/**
	 * main
	 * @param args プロパティファイルパス
	 */
	public static void main(String[] args) {
		try {
			outputLogMsg("[[[[[ START ProducerSample ]]]]]");

			// プロパティ読み込み
			Properties producerConfig = new Properties();
			InputStream is = new FileInputStream(new File(args[0]));
			producerConfig.load(is);

			// producerプロパティ設定(KafkaAPIのJavadocに記載されている例を使用)
			producerConfig.put("bootstrap.servers", "localhost:9092");
			producerConfig.put("acks", "all");
			producerConfig.put("retries", 0);
			producerConfig.put("batch.size", 16384);
			producerConfig.put("linger.ms", 1);
			producerConfig.put("buffer.memory", 33554432);
			producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

			// AP制御用プロパティ
			String topicName = producerConfig.getProperty(TOPIC_NAME);
			String template = producerConfig.getProperty(MSG_TEMPLATE);
			int cnt = Integer.parseInt(producerConfig.getProperty(MSG_CNT));
			long waitTime = Long.parseLong(producerConfig.getProperty(WAIT_TIME));

			Producer<String, String> producer = new KafkaProducer<String, String>(producerConfig);
//			producer.beginTransaction();

			outputLogMsg("send to topic:" + topicName + "/ template:" + template + "/ cnt:" + cnt);

			for(int i=0; i < cnt; i++) {
				// メッセージ生成
				// メッセージテンプレート + 連番
				String message = template + String.valueOf(i);
				outputLogMsg(message);
				// メッセージ送信
				producer.send(new ProducerRecord<String, String>(topicName, message));
				Thread.sleep(waitTime);
			}

//			producer.commitTransaction();

			producer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		outputLogMsg("[[[[[ END ProducerSample ]]]]]");

	}

	/**
	 * コンソールとロガーにメッセージ出力する
	 *
	 * @param msg
	 */
	private static void outputLogMsg(String msg) {
		System.out.println(msg);
		log.debug(msg);
	}
}
