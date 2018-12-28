package com.quantuminventions.threads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.config.EventProcessingProperties.EventQueue;
import com.quantuminventions.config.EventProcessingProperties.EventQueue.Writer;
import com.quantuminventions.model.VehicleEvent;

public class QueueWriterTest {
	
	private static Queue<VehicleEvent> queue;
	private static EventProcessingProperties epProperties;
	private QueueWriter queueWriter;
	private static int numberOfVehicle = 4;
	
	@BeforeClass
	public static void classIni() {
		queue = new ConcurrentLinkedDeque<>();
		
		Writer writer = new Writer();
		writer.setDataFilePath("./src/test/resources/Event_Data_Test.txt");
		writer.setInterval(2);
		writer.setNumberOfVehicle(numberOfVehicle);
		
		EventQueue eventQueue = new EventQueue();
		eventQueue.setWriter(writer);
		
		epProperties  = new EventProcessingProperties();
		epProperties.setEventQueue(eventQueue);
	}
	
	@Before
	public void methodIni() {
		queue.clear();
		queueWriter = new QueueWriter(queue, epProperties);
	}
	
	@Test
	public void writeMethodTest() throws IOException {
		queueWriter.write();
		Assert.assertEquals(numberOfVehicle, queue.size());
		
		List<String> lines = Files.readAllLines(Paths.get("./src/test/resources/Event_Data_Test.txt"));
		String line;
		for (int i = 0; i < numberOfVehicle; i++) {
			line = lines.get(i);
			String vehicleId = line.split(",")[0];
			String event = line.split(",")[1];
			
			VehicleEvent ve = queue.poll();
			System.out.println(ve.toString());
			Assert.assertEquals(vehicleId, ve.getVehicleId());
			Assert.assertEquals(event, ve.getEvent().name());
		}
		
	}

}
