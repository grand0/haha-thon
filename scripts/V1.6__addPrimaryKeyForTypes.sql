alter table Types add primary key(id);

create sequence Types_seq;

alter table Types alter column id set default nextval('Types_seq');