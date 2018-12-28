package com.quantuminventions.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "event-processing")
public class EventProcessingProperties {

	private EventQueue eventQueue;
	private EventProcessing eventProcessing;

	public EventQueue getEventQueue() {
		return eventQueue;
	}

	public void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	public EventProcessing getEventProcessing() {
		return eventProcessing;
	}

	public void setEventProcessing(EventProcessing eventProcessing) {
		this.eventProcessing = eventProcessing;
	}

	public static class EventQueue {

		private static Writer writer;
		private static Reader reader;

		public Writer getWriter() {
			return writer;
		}

		public void setWriter(Writer writer) {
			EventQueue.writer = writer;
		}

		public Reader getReader() {
			return reader;
		}

		public void setReader(Reader reader) {
			EventQueue.reader = reader;
		}

		public static class Writer {
			private int interval;
			private String dataFilePath;
			private int numberOfVehicle;

			public int getInterval() {
				return interval;
			}

			public void setInterval(int interval) {
				this.interval = interval;
			}

			public String getDataFilePath() {
				return dataFilePath;
			}

			public void setDataFilePath(String dataFilePath) {
				this.dataFilePath = dataFilePath;
			}

			public int getNumberOfVehicle() {
				return numberOfVehicle;
			}

			public void setNumberOfVehicle(int numberOfVehicle) {
				this.numberOfVehicle = numberOfVehicle;
			}
		}

		public static class Reader {
			private int interval;

			public int getInterval() {
				return interval;
			}

			public void setInterval(int interval) {
				this.interval = interval;
			}
		}
	}

	public static class EventProcessing {
		private int threadPoolSize;
		private String webserviceURL;

		public int getThreadPoolSize() {
			return threadPoolSize;
		}

		public void setThreadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
		}

		public String getWebserviceURL() {
			return webserviceURL;
		}

		public void setWebserviceURL(String webserviceURL) {
			this.webserviceURL = webserviceURL;
		}

	}
}
