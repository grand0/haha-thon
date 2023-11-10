alter table Measurement_names add primary key(id);

create sequence Measurement_names_seq;

alter table Measurement_names alter column id set default nextval('Measurement_names_seq');