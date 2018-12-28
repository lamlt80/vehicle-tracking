package com.quantuminventions.threads;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;

@Component
public class QueueWriter {
	private static final Logger log = LoggerFactory.getLogger(QueueWriter.class);
	
	private String filePath;
	private int numberOfVehicle;
	private static Integer readCycle = 0;
	
	private final Queue<VehicleEvent> queue;
	private final EventProcessingProperties epProperties;
	
	public QueueWriter(Queue<VehicleEvent> queue, EventProcessingProperties epProperties) {
		this.queue = queue;
		this.epProperties = epProperties;
		this.filePath = this.epProperties.getEventQueue().getWriter().getDataFilePath();
		this.numberOfVehicle = this.epProperties.getEventQueue().getWriter().getNumberOfVehicle();
	}

	public void write() {
		log.info("readCycle: {}", readCycle);
		
		int skipLines;
		synchronized (readCycle) {
			skipLines = readCycle * numberOfVehicle;
			readCycle ++;
		}
		
		List<String> selectedLines = null;
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			selectedLines = lines
							.skip(skipLines)
							.limit(numberOfVehicle)
							.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Encounter exception when read event file with path: {}. Exception: ", filePath, e);
		}
		
		log.debug("selectedLines: {}", selectedLines);
		
		if (selectedLines == null || selectedLines.isEmpty()) {
			synchronized (readCycle) {
				readCycle --;
			}
			return;
		}
		
		selectedLines.forEach(line -> {
			String[] strArr = line.split(",");
			queue.add(new VehicleEvent(strArr[0], Event.valueOf(strArr[1]), LocalDateTime.now()));
		});
	}

}
