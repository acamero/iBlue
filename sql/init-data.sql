insert into `map_fdm`.`street_types` (str_description, bl_free_parking_ind) values ('Sin clasificar',1);

insert into `map_fdm`.`weight_types` (str_description, str_name) values ('Distancia en metros calculada a partir de las esquinas','Distancia');

insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('trunk', 'Carretera nacional');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('trunk_link', 'Enlace a carretera nacional');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('secondary_link', 'Enlace a carretera autonómica 3 nivel');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('primary_link', 'Enlace a carretera autonómica 2 nivel');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('living_street', 'Calle residencial');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('motorway_link', 'Enlace a autovía');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('motorway', 'Autovía');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('primary', 'Carretera autonómica 2 nivel');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('tertiary', 'Carretera local');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('secondary', 'Carretera autonómica 2 nivel');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('unclassified', 'Sin clasificar');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('service', 'Vía de servicio');
insert into `map_fdm`.`named_street_types` (str_named_street_type, str_description) values ('residential', 'Calle o avenida');

insert into `map_fdm`.`tile_range` (pk_id, decimal_latitude_range, decimal_longitude_range) values (1,0.2372263,0.0289512);

