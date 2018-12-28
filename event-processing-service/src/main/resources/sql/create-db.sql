CREATE TABLE vehicle_event (
	id         	BIGINT IDENTITY PRIMARY KEY,
	vehicle_id	VARCHAR(50),
	event		VARCHAR(30),
	event_time  TIMESTAMP(2) WITH TIME ZONE,
	trip_no 	INTEGER
);


