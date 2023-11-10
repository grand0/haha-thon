alter table Sensors add primary key(id);

create sequence Sensors_seq;

alter table Sensors alter column id set default nextval('Sensors_seq');
