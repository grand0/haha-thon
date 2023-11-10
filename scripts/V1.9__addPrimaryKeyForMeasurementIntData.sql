alter table Measurement_int_data add primary key(id);

create sequence Measurement_int_data_seq;

alter table Measurement_int_data alter column id set default nextval('Measurement_int_data_seq');