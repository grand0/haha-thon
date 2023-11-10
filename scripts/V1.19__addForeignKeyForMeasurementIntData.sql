alter table Measurement_int_data
 	add constraint measurement_name_id_fk foreign key (measurement_name_id) references Measurement_names (id) on delete restrict,
        add constraint sensor_id_fk foreign key (sensor_id) references Sensors (id) on delete cascade;