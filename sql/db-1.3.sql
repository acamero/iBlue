CREATE DATABASE `map_fdm` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `map_tmp` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `map_vw` /*!40100 DEFAULT CHARACTER SET latin1 */;

-- ------------------------------------------------------------------

CREATE USER 'mapuser'@'%' IDENTIFIED BY 'zaq1xsw2';
GRANT USAGE ON *.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_fdm`.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_tmp`.* TO 'mapuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `map_vw`.* TO 'mapuser'@'%';

-- ------------------------------------------------------------------

-- intersections
DROP TABLE IF EXISTS `map_fdm`.`intersections`;
CREATE TABLE `map_fdm`.`intersections` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `decimal_latitude` decimal(10,6) NOT NULL,
  `decimal_longitude` decimal(10,6) NOT NULL,  
  `float_northing` float(16,6) NOT NULL,
  `float_easting` float(16,6) NOT NULL,
  `int_longitude_zone` tinyint,
  `char_latitude_zone` char(1),
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- street types ("línea blanca", "zona azul", "zona verde")
DROP TABLE IF EXISTS `map_fdm`.`street_types`;
CREATE TABLE `map_fdm`.`street_types` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `str_description` varchar(255) NOT NULL,
  `bl_free_parking_ind` tinyint(1),  
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- street types ("línea blanca", "zona azul", "zona verde")
DROP TABLE IF EXISTS `map_fdm`.`named_streets`;
CREATE TABLE `map_fdm`.`named_streets` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `str_name` varchar(255) NOT NULL,
  `str_reference` varchar(255),  
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- store the map of streets
DROP TABLE IF EXISTS `map_fdm`.`geo_streets`;
CREATE TABLE `map_fdm`.`geo_streets` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT, 
  `fk_named_street_id` int(11),
  `fk_street_type_id` int(11),
  `fk_intersection_from_id` int(11) NOT NULL,  
  `fk_intersection_to_id` int(11) NOT NULL,

  `bl_oneway_ind` tinyint(1) DEFAULT 0,
  `int_lanes` tinyint(1) DEFAULT 0,
  `int_lanes_forward` tinyint(1) DEFAULT 0,
  `int_lanes_backward` tinyint(1) DEFAULT 0, 
  `routable` tinyint(1) DEFAULT 0, 
  `int_parking_capacity` smallint,
    
  `float_line_coeff_a` float(16,6) NOT NULL,
  `float_line_coeff_b` float(16,6) NOT NULL,
  `float_line_coeff_c` float(16,6) NOT NULL,
  `float_line_sqrt_a2_b2` float(16,6) NOT NULL,
  
  `float_least_easting` float(16,6),
  `float_greatest_easting` float(16,6),
  `float_least_northing` float(16,6),
  `float_greatest_northing` float(16,6),
  `float_mid_easting` float(16,6),
  `float_mid_northing` float(16,6),  
  
  `int_status` tinyint(1) DEFAULT NULL,
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
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `decimal_latitude` decimal(10,6) NOT NULL,
  `decimal_longitude` decimal(10,6) NOT NULL,
  `float_northing` float(16,6) NOT NULL,
  `float_easting` float(16,6) NOT NULL,
  `int_longitude_zone` tinyint,
  `char_latitude_zone` char(1),
  `str_mac` varchar(45) DEFAULT NULL,
  `fk_geo_street_id` INT(11),
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

-- ------------------------------------------------------------------
-- Pre compute calculations on geo_street new or updated record

DROP PROCEDURE IF EXISTS `map_fdm`.`calc_l_g_m`;
DELIMITER $$
CREATE PROCEDURE `map_fdm`.`calc_l_g_m` 
(IN in_pk_id INT(11))
BEGIN
UPDATE `map_fdm`.`geo_streets` geo, `map_fdm`.`intersections` inter_from, `map_fdm`.`intersections` inter_to
SET
  float_least_easting = least(inter_from.float_easting, inter_to.float_easting),
  float_greatest_easting = greatest(inter_from.float_easting, inter_to.float_easting),
  float_mid_easting = (inter_from.float_easting + inter_to.float_easting)/2,
  float_least_northing = least(inter_from.float_northing, inter_to.float_northing),
  float_greatest_northing = greatest(inter_from.float_northing, inter_to.float_northing),
  float_mid_northing = (inter_from.float_northing + inter_to.float_northing)/2
WHERE
  geo.fk_intersection_from_id = inter_from.pk_id
  AND geo.fk_intersection_to_id = inter_to.pk_id
  AND geo.pk_id = in_pk_id ;
END $$
DELIMITER ;

DROP TRIGGER IF EXISTS `map_fdm`.`insert_calc_l_g_m`;
DELIMITER $$
CREATE TRIGGER `map_fdm`.`insert_calc_l_g_m` AFTER INSERT ON `map_fdm`.`geo_streets`
FOR EACH ROW
BEGIN
CALL `map_fdm`.`calc_l_g_m`(new.`pk_id`);
END $$
DELIMITER ;

DROP TRIGGER IF EXISTS `map_fdm`.`update_calc_l_g_m`;
DELIMITER $$
CREATE TRIGGER `map_fdm`.`update_calc_l_g_m` AFTER UPDATE ON `map_fdm`.`geo_streets`
FOR EACH ROW
BEGIN
CALL `map_fdm`.`calc_l_g_m`(new.`pk_id`);
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
  ,nodes_from.decimal_latitude
  ,nodes_from.decimal_longitude
  ,nodes_to.decimal_latitude
  ,nodes_to.decimal_longitude
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
  ,nodes_from.decimal_latitude
  ,nodes_from.decimal_longitude
  ,nodes_to.decimal_latitude
  ,nodes_to.decimal_longitude
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

