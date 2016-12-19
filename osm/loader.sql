DROP TABLE IF EXISTS `map_tmp`.`intersections`;
CREATE TABLE `map_tmp`.`intersections` (
  node_id bigint,
  lat_deg decimal(10,7),
  lon_deg decimal(10,7),
  northing float(16,6),
  easting float(16,6),
  zone_number tinyint,
  zone_letter char(1)
);

LOAD DATA LOCAL INFILE 'tmp/nodes.csv' INTO TABLE `map_tmp`.`intersections`
  FIELDS TERMINATED BY ','
  IGNORE 1 LINES;

-- --------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `map_tmp`.`named_streets`;
CREATE TABLE `map_tmp`.`named_streets` (
  way_id bigint,
  name varchar(255),
  ref_name varchar(255),
  way_desc varchar(255)
);

LOAD DATA LOCAL INFILE 'tmp/named.csv' INTO TABLE `map_tmp`.`named_streets`
  CHARACTER SET utf8
  FIELDS TERMINATED BY ','
  IGNORE 1 LINES;

-- --------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `map_tmp`.`geo_streets`;
CREATE TABLE `map_tmp`.`geo_streets` (
  way_id bigint,
  node_id_from bigint,
  node_id_to bigint,
  one_way_ind TINYINT,
  no_lanes TINYINT,
  lanes_backward TINYINT,
  lanes_forward TINYINT,
  bl_routable_ind TINYINT
);

LOAD DATA LOCAL INFILE 'tmp/geo.csv' INTO TABLE `map_tmp`.`geo_streets`
  FIELDS TERMINATED BY ','
  IGNORE 1 LINES;

-- --------------------------------------------------------------------------------------

insert into `map_fdm`.`named_streets` (pk_id, str_name, str_reference, fk_named_street_type_id)
select tmp.way_id, tmp.name, tmp.ref_name, stypes.pk_id
from `map_tmp`.`named_streets` tmp
  left join `map_fdm`.`named_streets` dest
    on tmp.way_id = dest.pk_id
  join `map_fdm`.`named_street_types` stypes
    on tmp.way_desc = stypes.str_named_street_type
where
  dest.pk_id is null;

insert into `map_fdm`.`intersections` 
  (pk_id, 
  decimal_latitude,
  decimal_longitude, 
  float_northing,
  float_easting,
  int_longitude_zone,
  char_latitude_zone)
select 
  tmp.node_id,
  tmp.lat_deg,
  tmp.lon_deg,
  tmp.northing,
  tmp.easting,
  tmp.zone_number,
  tmp.zone_letter
from `map_tmp`.`intersections` tmp
  left join `map_fdm`.`intersections` dest
    on tmp.node_id = dest.pk_id
where
  dest.pk_id is null;

insert into `map_fdm`.`geo_streets` (
  fk_named_street_id, 
  fk_street_type_id,
  fk_intersection_from_id, 
  fk_intersection_to_id, 
  bl_oneway_ind, 
  int_lanes, 
  int_lanes_forward, 
  int_lanes_backward, 
  bl_routable_ind, 
  int_parking_capacity, 
  int_status)
select 
  tmp.way_id, 
  8,
  tmp.node_id_from, 
  tmp.node_id_to, 
  tmp.one_way_ind, 
  tmp.no_lanes, 
  tmp.lanes_forward, 
  tmp.lanes_backward,  
  tmp.bl_routable_ind,
  0,
  1
from `map_tmp`.`geo_streets` tmp
  left join `map_fdm`.`geo_streets` dest
    on tmp.node_id_from = dest.fk_intersection_from_id
    and tmp.node_id_to = dest.fk_intersection_to_id
  join `map_fdm`.`intersections` i_from
    on tmp.node_id_from = i_from.pk_id
  join `map_fdm`.`intersections` i_to
    on tmp.node_id_to = i_to.pk_id
where
  dest.pk_id is null;


