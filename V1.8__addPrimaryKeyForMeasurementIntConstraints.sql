alter table Measurement_int_constraints add primary key(id);

create sequence Measurement_int_constraints_seq;

alter table Measurement_int_constraints alter column id set default nextval('Measurement_int_constraints_seq');