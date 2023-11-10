alter table Measurement_int_constraints
 	add constraint measurement_name_id_fk foreign key (measurement_name_id) references Measurement_names (id) on delete restrict;