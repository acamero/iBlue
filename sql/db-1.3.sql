DROP DATABASE IF EXISTS `map_fdm`;
DROP DATABASE IF EXISTS `map_tmp`;
DROP DATABASE IF EXISTS `map_vw`;
CREATE DATABASE `map_fdm` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `map_tmp` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `map_vw` /*!40100 DEFAULT CHARACTER SET latin1 */;

-- ------------------------------------------------------------------

CREATE USER IF NOT EXISTS 'mapuser'@'%' IDENTIFIED BY 'zaq1xsw2';
GRANT USAGE ON *.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_fdm`.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_tmp`.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_vw`.* TO 'mapuser'@'%';

-- ------------------------------------------------------------------

-- intersections
DROP TABLE IF EXISTS `map_fdm`.`intersections`;
CREATE TABLE `map_fdm`.`intersections` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `decimal_latitude` decimal(10,7) NOT NULL,
  `decimal_longitude` decimal(10,7) NOT NULL,  
  `float_northing` float(16,6) NOT NULL,
  `float_easting` float(16,6) NOT NULL,
  `int_longitude_zone` tinyint,
  `char_latitude_zone` char(1),
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- street types ("línea blanca", "zona azul", "zona verde")
DROP TABLE IF EXISTS `map_fdm`.`street_types`;
CREATE TABLE `map_fdm`.`street_types` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `str_description` varchar(255) NOT NULL,
  `bl_free_parking_ind` TINYINT, 
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- named street types ("autopista", "calle", etc)
DROP TABLE IF EXISTS `map_fdm`.`named_street_types`;
CREATE TABLE `map_fdm`.`named_street_types` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `str_named_street_type` varchar(50) NOT NULL,
  `str_description` varchar(255), 
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- logic streets
DROP TABLE IF EXISTS `map_fdm`.`named_streets`;
CREATE TABLE `map_fdm`.`named_streets` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fk_named_street_type_id` bigint(20),
  `str_name` varchar(255) NOT NULL,
  `str_reference` varchar(255),
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`pk_id`),
  FOREIGN KEY fk_named_street_type (`fk_named_street_type_id`) REFERENCES `map_fdm`.`named_street_types` (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- store the map of streets
DROP TABLE IF EXISTS `map_fdm`.`geo_streets`;
CREATE TABLE `map_fdm`.`geo_streets` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT, 
  `fk_named_street_id` bigint(20),
  `fk_street_type_id` bigint(20),
  `fk_intersection_from_id` bigint(20) NOT NULL,  
  `fk_intersection_to_id` bigint(20) NOT NULL,

  `bl_oneway_ind` TINYINT DEFAULT 0,
  `int_lanes` TINYINT DEFAULT 0,
  `int_lanes_forward` TINYINT DEFAULT 0,
  `int_lanes_backward` TINYINT DEFAULT 0, 
  `bl_routable_ind` TINYINT DEFAULT 0, 
  `int_parking_capacity` smallint,
    
  `float_line_coeff_a` float(16,6),
  `float_line_coeff_b` float(16,6),
  `float_line_coeff_c` float(16,6),
  `float_line_sqrt_a2_b2` float(16,6),
  
  `float_least_easting` float(16,6),
  `float_greatest_easting` float(16,6),
  `float_least_northing` float(16,6),
  `float_greatest_northing` float(16,6),
  `float_mid_easting` float(16,6),
  `float_mid_northing` float(16,6),  
  
  `int_status` TINYINT DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  FOREIGN KEY fk_named_street (`fk_named_street_id`) REFERENCES `map_fdm`.`named_streets` (`pk_id`),
  FOREIGN KEY fk_street_type (`fk_street_type_id`) REFERENCES `map_fdm`.`street_types` (`pk_id`),
  FOREIGN KEY fk_intersection_from (`fk_intersection_from_id`) REFERENCES `map_fdm`.`intersections` (`pk_id`),
  FOREIGN KEY fk_intersection_to (`fk_intersection_to_id`) REFERENCES `map_fdm`.`intersections` (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

CREATE INDEX street_from ON `map_fdm`.`geo_streets`(fk_intersection_from_id);
CREATE INDEX street_to ON `map_fdm`.`geo_streets`(fk_intersection_to_id);
CREATE INDEX status ON `map_fdm`.`geo_streets` (int_status);

-- parking spots reported by users
DROP TABLE IF EXISTS `map_fdm`.`spots`;
CREATE TABLE `map_fdm`.`spots` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `decimal_latitude` decimal(10,7) NOT NULL,
  `decimal_longitude` decimal(10,7) NOT NULL,
  `float_northing` float(16,6) NOT NULL,
  `float_easting` float(16,6) NOT NULL,
  `int_longitude_zone` tinyint,
  `char_latitude_zone` char(1),
  `str_mac` varchar(45) DEFAULT NULL,
  `fk_geo_street_id` bigint(20),
  `int_status` tinyint(4) DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  FOREIGN KEY fk_geo_street (`fk_geo_street_id`) REFERENCES `map_fdm`.`geo_streets` (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

CREATE INDEX status ON `map_fdm`.`spots` (int_status);
CREATE INDEX ts_create ON `map_fdm`.`spots` (ts_create);
CREATE INDEX fk_id ON `map_fdm`.`spots` (fk_geo_street_id);
CREATE INDEX loc ON `map_fdm`.`spots` (decimal_latitude,decimal_longitude,str_mac);

-- user defined weights for geo_streets
DROP TABLE IF EXISTS `map_fdm`.`weight_types`;
CREATE TABLE `map_fdm`.`weight_types` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `str_description` varchar(255) NOT NULL,
  `str_name` varchar(50) NOT NULL,  
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- parking spots reported by users
DROP TABLE IF EXISTS `map_fdm`.`geo_street_weights`;
CREATE TABLE `map_fdm`.`geo_street_weights` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fk_weight_type_id` bigint(20),
  `fk_geo_street_id` bigint(20),
  `float_weight` float(16,6) DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  FOREIGN KEY fk_geo_street (`fk_geo_street_id`) REFERENCES `map_fdm`.`geo_streets` (`pk_id`),
  FOREIGN KEY fk_weight_type (`fk_weight_type_id`) REFERENCES `map_fdm`.`weight_types` (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- tiles (pre calculated matrices)
DROP TABLE IF EXISTS `map_fdm`.`tile_container`;
CREATE TABLE `map_fdm`.`tile_container` (
  `pk_latitude_id` bigint(20) NOT NULL,
  `pk_longitude_id` bigint(20) NOT NULL,
  `byte_tile` longblob,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_latitude_id`,`pk_longitude_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------
-- Pre compute calculations on geo_street new or updated record

DROP TRIGGER IF EXISTS `map_fdm`.`insert_calc_l_g_m`;
DELIMITER $$
CREATE TRIGGER `map_fdm`.`insert_calc_l_g_m` BEFORE INSERT ON `map_fdm`.`geo_streets`
FOR EACH ROW
BEGIN
SET
  new.float_least_easting = (select least(inter_from.float_easting, inter_to.float_easting) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_greatest_easting = (select greatest(inter_from.float_easting, inter_to.float_easting) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_mid_easting = (select (inter_from.float_easting + inter_to.float_easting) / 2
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_least_northing = (select least(inter_from.float_northing, inter_to.float_northing) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_greatest_northing = (select greatest(inter_from.float_northing, inter_to.float_northing) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_mid_northing = (select (inter_from.float_northing + inter_to.float_northing) / 2
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_line_coeff_a = ( select case
                                      when i_from.float_easting = i_to.float_easting then 1
                                      when i_from.float_northing = i_to.float_northing then 0
                                      else (i_to.float_northing - i_from.float_northing) / (i_to.float_easting - i_from.float_easting)
                                    end as coeff_a
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_coeff_b = ( select case
                                      when i_from.float_easting = i_to.float_easting then 0
                                      when i_from.float_northing = i_to.float_northing then 1
                                      else -1
                                    end as coeff_b
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_coeff_c = ( select case
                                      when i_from.float_easting = i_to.float_easting then -i_from.float_easting
                                      when i_from.float_northing = i_to.float_northing then -i_from.float_northing
                                      else i_from.float_northing - i_from.float_easting * (i_to.float_northing - i_from.float_northing) / (i_to.float_easting - i_from.float_easting)
                                    end as coeff_c
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_sqrt_a2_b2 = sqrt(new.float_line_coeff_a*new.float_line_coeff_a + new.float_line_coeff_b*new.float_line_coeff_b)
;
END $$
DELIMITER ;

DROP TRIGGER IF EXISTS `map_fdm`.`update_calc_l_g_m`;
DELIMITER $$
CREATE TRIGGER `map_fdm`.`update_calc_l_g_m` BEFORE UPDATE ON `map_fdm`.`geo_streets`
FOR EACH ROW
BEGIN
SET
  new.float_least_easting = (select least(inter_from.float_easting, inter_to.float_easting) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_greatest_easting = (select greatest(inter_from.float_easting, inter_to.float_easting) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_mid_easting = (select (inter_from.float_easting + inter_to.float_easting) / 2
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_least_northing = (select least(inter_from.float_northing, inter_to.float_northing) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_greatest_northing = (select greatest(inter_from.float_northing, inter_to.float_northing) 
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_mid_northing = (select (inter_from.float_northing + inter_to.float_northing) / 2
                             from `map_fdm`.`intersections` inter_from, 
                                  `map_fdm`.`intersections` inter_to
                             where
                               new.fk_intersection_from_id = inter_from.pk_id
                               and new.fk_intersection_to_id = inter_to.pk_id),
  new.float_line_coeff_a = ( select case
                                      when i_from.float_easting = i_to.float_easting then 1
                                      when i_from.float_northing = i_to.float_northing then 0
                                      else (i_to.float_northing - i_from.float_northing) / (i_to.float_easting - i_from.float_easting)
                                    end as coeff_a
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_coeff_b = ( select case
                                      when i_from.float_easting = i_to.float_easting then 0
                                      when i_from.float_northing = i_to.float_northing then 1
                                      else -1
                                    end as coeff_b
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_coeff_c = ( select case
                                      when i_from.float_easting = i_to.float_easting then -i_from.float_easting
                                      when i_from.float_northing = i_to.float_northing then -i_from.float_northing
                                      else i_from.float_northing - i_from.float_easting * (i_to.float_northing - i_from.float_northing) / (i_to.float_easting - i_from.float_easting)
                                    end as coeff_c
                             from `map_fdm`.`intersections` i_from, 
                                  `map_fdm`.`intersections` i_to
                             where
                               new.fk_intersection_from_id = i_from.pk_id
                               and new.fk_intersection_to_id = i_to.pk_id),
  new.float_line_sqrt_a2_b2 = sqrt(new.float_line_coeff_a*new.float_line_coeff_a + new.float_line_coeff_b*new.float_line_coeff_b)
;
END $$
DELIMITER ;

-- ------------------------------------------------------------------

-- obtain the number of spots allocated (in use)
DROP VIEW IF EXISTS `map_vw`.`vw_geo_streets_usage`;
CREATE VIEW `map_vw`.`vw_geo_streets_usage` AS
SELECT sp.fk_geo_street_id, count(*) usage_number
FROM `map_fdm`.`spots` sp
WHERE
	sp.int_status = 1
GROUP BY sp.fk_geo_street_id;


DROP VIEW IF EXISTS `map_fdm`.`vw_geo_streets_weighted`;
CREATE VIEW `map_fdm`.`vw_geo_streets_weighted` AS
SELECT 
  st.pk_id as street_id,
  st.fk_street_type_id,
  st.int_parking_capacity,
  st.fk_intersection_from_id,
  st.fk_intersection_to_id,  
  st.bl_oneway_ind,
  st.int_lanes,
  st.int_lanes_forward,
  st.int_lanes_backward,
  w.pk_id as weight_id,
  w.fk_weight_type_id,
  w.float_weight,
  ifr.decimal_latitude as from_lat,
  ifr.decimal_longitude as from_lon,
  ito.decimal_latitude as to_lat,
  ito.decimal_longitude as to_lon
FROM  `map_fdm`.`geo_streets` st 
  JOIN `map_fdm`.`geo_street_weights` w
    ON st.pk_id = w.fk_geo_street_id
  JOIN `map_fdm`.`intersections` ifr
    ON st.fk_intersection_from_id = ifr.pk_id
  JOIN `map_fdm`.`intersections` ito
    ON st.fk_intersection_to_id = ito.pk_id
WHERE
  st.bl_routable_ind = 1
  AND st.int_status = 1

UNION 

SELECT 
  st.pk_id as street_id,
  st.fk_street_type_id,
  st.int_parking_capacity,
  st.fk_intersection_to_id as fk_intersection_from_id,
  st.fk_intersection_from_id as fk_intersection_to_id, 
  st.bl_oneway_ind,
  st.int_lanes,
  st.int_lanes_backward as int_lanes_forward,
  st.int_lanes_forward as int_lanes_backward,  
  w.pk_id as weight_id,
  w.fk_weight_type_id,
  w.float_weight,  
  ito.decimal_latitude as from_lat,
  ito.decimal_longitude as from_lon,
  ifr.decimal_latitude as to_lat,
  ifr.decimal_longitude as to_lon
FROM  `map_fdm`.`geo_streets` st 
  JOIN `map_fdm`.`geo_street_weights` w
    ON st.pk_id = w.fk_geo_street_id
  JOIN `map_fdm`.`intersections` ifr
    ON st.fk_intersection_from_id = ifr.pk_id
  JOIN `map_fdm`.`intersections` ito
    ON st.fk_intersection_to_id = ito.pk_id
WHERE
  st.bl_routable_ind = 1
  AND st.int_status = 1
  AND st.bl_oneway_ind = 0
;

-- ------------------------------------------------------------------

-- 0.0015 ~ 150 m 
-- get the id of the geo_street which is closer to the given spot
DROP PROCEDURE IF EXISTS `map_fdm`.`get_nearest_street` ;
DELIMITER $$
CREATE PROCEDURE `map_fdm`.`get_nearest_street` 
(IN lat FLOAT(10,6), IN lon FLOAT(10,6), IN northing FLOAT(16,6), IN easting FLOAT(16,6))
BEGIN
DECLARE delta FLOAT(10,6);
SET delta = 0.0015;
SELECT 
  st.pk_id, 
  ABS(st.float_line_coeff_a*easting + st.float_line_coeff_b*northing+st.float_line_coeff_c) / st.float_line_sqrt_a2_b2 
    +
    CASE
      WHEN easting BETWEEN st.float_least_easting AND st.float_greatest_easting
        OR northing BETWEEN st.float_least_northing AND st.float_greatest_northing THEN 0
      ELSE ABS( easting-st.float_mid_easting) + ABS(northing-st.float_mid_northing)
    END AS distance
FROM `map_fdm`.`geo_streets` st
  JOIN `map_fdm`.`intersections` nodes
    ON st.fk_intersection_from_id = nodes.pk_id
WHERE 
  nodes.decimal_latitude BETWEEN lat - delta AND lat + delta
  AND nodes.decimal_longitude BETWEEN lon - delta AND lon + delta
  AND st.int_status = 1
ORDER BY distance ASC
LIMIT 1;
END $$
DELIMITER ;


-- 0.05 ~ 5.5km 
-- get a list of streets near input location, including usage
DROP PROCEDURE IF EXISTS `map_fdm`.`get_near_area_map`;
DELIMITER $$
CREATE PROCEDURE `map_fdm`.`get_near_area_map`
(IN lat FLOAT(10,6), IN lon FLOAT(10,6))
BEGIN 
DECLARE delta FLOAT(10,6);
SET delta = 0.05;
SELECT
  st.pk_id
  ,nodes_from.decimal_latitude as decimal_latitude_1
  ,nodes_from.decimal_longitude as decimal_longitude_1
  ,nodes_to.decimal_latitude as decimal_latitude_2
  ,nodes_to.decimal_longitude as decimal_longitude_2
  ,coalesce(usp.usage_number,0) as usage_number
  ,st.int_parking_capacity
  ,st.fk_street_type_id
FROM `map_fdm`.`geo_streets` st
  JOIN `map_fdm`.`intersections` nodes_from
    ON st.fk_intersection_from_id = nodes_from.pk_id
  JOIN `map_fdm`.`intersections` nodes_to
    ON st.fk_intersection_to_id = nodes_to.pk_id
  LEFT JOIN `map_vw`.`vw_geo_streets_usage` usp
    ON st.pk_id = usp.fk_geo_street_id
WHERE 
  nodes_from.decimal_latitude BETWEEN lat - delta AND lat + delta
  AND nodes_from.decimal_longitude BETWEEN lon - delta AND lon + delta
  AND st.int_status = 1;
END $$
DELIMITER ;


-- 0.015 ~ 1.5km 
DROP PROCEDURE IF EXISTS `map_fdm`.`get_closest_parking` ;
DELIMITER $$
CREATE PROCEDURE `map_fdm`.`get_closest_parking`
(IN lat FLOAT(10,6), IN lon FLOAT(10,6))
BEGIN 
DECLARE delta FLOAT(10,6);
SET delta = 0.015;
SELECT
  st.pk_id
  ,nodes_from.decimal_latitude as decimal_latitude_1
  ,nodes_from.decimal_longitude as decimal_longitude_1
  ,nodes_to.decimal_latitude as decimal_latitude_2
  ,nodes_to.decimal_longitude as decimal_longitude_2
  ,coalesce(usp.usage_number,0) as usage_number
  ,st.int_parking_capacity
  ,st.fk_street_type_id
  ,st.int_parking_capacity - coalesce(usp.usage_number,0) as availability
  ,ABS(nodes_from.decimal_latitude - lat) + ABS(nodes_from.decimal_longitude - lon) as approx_distance
FROM `map_fdm`.`geo_streets` st
  JOIN `map_fdm`.`intersections` nodes_from
    ON st.fk_intersection_from_id = nodes_from.pk_id
  JOIN `map_fdm`.`intersections` nodes_to
    ON st.fk_intersection_to_id = nodes_to.pk_id
  LEFT JOIN `map_vw`.`vw_geo_streets_usage` usp
    ON st.pk_id = usp.fk_geo_street_id
WHERE 
  nodes_from.decimal_latitude BETWEEN lat - delta AND lat + delta
  AND nodes_from.decimal_longitude BETWEEN lon - delta AND lon + delta
  AND st.int_status = 1
  AND st.int_parking_capacity > 0
HAVING availability > 0 
ORDER BY approx_distance ASC
LIMIT 1 ;
END $$
DELIMITER ;




   




