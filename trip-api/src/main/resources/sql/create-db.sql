CREATE TABLE vehicle_trip (
	id         	BIGINT IDENTITY PRIMARY KEY,
	vehicle_id	VARCHAR(50),
	trip_no 	INTEGER, 
	trip_duration		VARCHAR(50),
	created_time  TIMESTAMP(2) WITH TIME ZONE	
);
