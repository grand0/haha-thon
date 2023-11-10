alter table Measurement_int_data
	alter column sensor_id set not null,
	alter column measurement_name_id set not null,
	alter column value set not null,
    	alter column time set not null;    