alter table Sensors
 	add constraint type_id_fk foreign key (type_id) references Types (id) on delete restrict;