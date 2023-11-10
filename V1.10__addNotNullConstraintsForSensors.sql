alter table Sensors
	alter column type_id set not null,
	alter column state set not null,
	alter column date set not null;