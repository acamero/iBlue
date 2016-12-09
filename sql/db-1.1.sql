CREATE DATABASE `iblue_fdm` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `iblue_tmp` /*!40100 DEFAULT CHARACTER SET latin1 */;

--------------------------------------------------------------------

CREATE USER 'iblueuser'@'%' IDENTIFIED BY 'zaq1xsw2';
GRANT USAGE ON *.* TO 'iblueuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `iblue_fdm`.* TO 'iblueuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `iblue_tmp`.* TO 'iblueuser'@'%';

--------------------------------------------------------------------

-- store parking spots reported by users
-- DROP TABLE `iblue_fdm`.`spots`;
CREATE TABLE `iblue_fdm`.`spots` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `float_latitude` float(10,6) NOT NULL,
  `float_longitude` float(10,6) NOT NULL,
  `float_northing` float(16,6) NOT NULL,
  `float_easting` float(16,6) NOT NULL,
  `int_longitude_zone` tinyint,
  `char_latitude_zone` char(1),
  `str_mac` varchar(45) DEFAULT NULL,
  `fk_street_id` INT(11),
  `int_status` tinyint(4) DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

CREATE INDEX status ON `iblue_fdm`.`spots` (int_status);
CREATE INDEX ts_create ON `iblue_fdm`.`spots` (ts_create);
CREATE INDEX fk_id ON `iblue_fdm`.`spots` (fk_street_id);

-- store the map of streets
-- drop TABLE `iblue_fdm`.`streets`;
CREATE TABLE `iblue_fdm`.`streets` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  
  `float_latitude_1` float(10,6) NOT NULL,
  `float_longitude_1` float(10,6) NOT NULL,  
  `float_northing_1` float(16,6) NOT NULL,
  `float_easting_1` float(16,6) NOT NULL,
  `int_longitude_zone_1` tinyint,
  `char_latitude_zone_1` char(1),

  `float_latitude_2` float(10,6) NOT NULL,
  `float_longitude_2` float(10,6) NOT NULL, 
  `float_northing_2` float(16,6) NOT NULL,
  `float_easting_2` float(16,6) NOT NULL,
  `int_longitude_zone_2` tinyint,
  `char_latitude_zone_2` char(1),
  
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
  
  `int_capacity` smallint,
  `int_type` smallint,
  
  `int_status` tinyint(4) DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

CREATE INDEX street_lat_long_1 ON `iblue_fdm`.`streets`(float_latitude_1, float_longitude_1);
CREATE INDEX status ON `iblue_fdm`.`streets` (int_status);


-- ------------------------------------------------------------------


-- DROP TRIGGER `iblue_fdm`.calc_l_g_m;
CREATE TRIGGER `iblue_fdm`.calc_l_g_m BEFORE INSERT ON `iblue_fdm`.`streets`
FOR EACH ROW
SET 
	new.float_least_easting = least(new.float_easting_1, new.float_easting_2),
    new.float_greatest_easting = greatest(new.float_easting_1, new.float_easting_2),
    new.float_mid_easting = (new.float_easting_1+new.float_easting_2)/2,
    new.float_least_northing = least(new.float_northing_1, new.float_northing_2),
    new.float_greatest_northing = greatest(new.float_northing_1, new.float_northing_2),
    new.float_mid_northing = (new.float_northing_1+new.float_northing_2)/2;

-- ------------------------------------------------------------------

-- delete from `iblue_fdm`.spots where pk_id>1;
-- delete from `iblue_fdm`.streets where pk_id>1;

-- DROP VIEW `iblue_fdm`.`vw_in_use_spots`;
CREATE VIEW `iblue_fdm`.`vw_in_use_spots` AS
SELECT sp.fk_street_id, count(*) in_use_spots
FROM `iblue_fdm`.spots sp
WHERE
	sp.int_status = 1
GROUP BY sp.fk_street_id;


-- ------------------------------------------------------------------

-- 0.0015 ~ 150 m a la redonda
-- DROP PROCEDURE `iblue_fdm`.`nearest_street` ;
DELIMITER $$
CREATE PROCEDURE `iblue_fdm`.`nearest_street` 
(IN lat FLOAT(10,6), IN lon FLOAT(10,6), IN northing FLOAT(16,6), IN easting FLOAT(16,6))
BEGIN
SELECT 
	st.pk_id, 
	ABS(st.float_line_coeff_a*easting + st.float_line_coeff_b*northing+st.float_line_coeff_c) / st.float_line_sqrt_a2_b2 
    +
    CASE
		WHEN easting BETWEEN st.float_least_easting AND st.float_greatest_easting
            OR northing BETWEEN st.float_least_northing AND st.float_greatest_northing THEN 0
		ELSE ABS( easting-st.float_mid_easting) + ABS(northing-st.float_mid_northing)
	END  
    as dist
FROM `iblue_fdm`.streets st
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.0015 AND lat + 0.0015
    AND st.float_longitude_1 BETWEEN lon - 0.0015 AND lon + 0.0015
    AND st.int_status = 1
ORDER BY dist ASC
LIMIT 1;
END $$
DELIMITER ;

-- 0.05 ~ 5.5km a la redonda
-- DROP PROCEDURE `iblue_fdm`.`area_map`;
DELIMITER $$
CREATE PROCEDURE `iblue_fdm`.`area_map`
(IN lat FLOAT(10,6), IN lon FLOAT(10,6))
BEGIN 
SELECT
	st.pk_id
	,st.float_latitude_1
    ,st.float_longitude_1
	,st.float_latitude_2
    ,st.float_longitude_2
	,coalesce(usp.in_use_spots,0) as in_use_spots
    ,st.int_capacity
    ,st.int_type
FROM `iblue_fdm`.streets st
	LEFT JOIN `iblue_fdm`.vw_in_use_spots usp
		ON st.pk_id = usp.fk_street_id
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.05 AND lat + 0.05
    AND st.float_longitude_1 BETWEEN lon - 0.05 AND lon + 0.05
    AND st.int_status = 1;
END $$
DELIMITER ;

-- 0.015 ~ 1.5km a la redonda
-- DROP PROCEDURE `iblue_fdm`.`closest_parking` ;
DELIMITER $$
CREATE PROCEDURE `iblue_fdm`.`closest_parking`
(IN lat FLOAT(10,6), IN lon FLOAT(10,6))
BEGIN 
SELECT
	st.pk_id
	,st.float_latitude_1
    ,st.float_longitude_1
	,st.float_latitude_2
    ,st.float_longitude_2
	,coalesce(usp.in_use_spots,0) as in_use_spots
    ,st.int_capacity
    ,st.int_type
    ,st.int_capacity - coalesce(usp.in_use_spots,0) as availability
    ,ABS(st.float_latitude_1 - lat) + ABS(st.float_longitude_1 - lon) as dist_app
FROM `iblue_fdm`.streets st
	LEFT JOIN `iblue_fdm`.vw_in_use_spots usp
		ON st.pk_id = usp.fk_street_id
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.015 AND lat + 0.015
    AND st.float_longitude_1 BETWEEN lon - 0.015 AND lon + 0.015
    AND st.int_status = 1
	AND st.int_capacity > 0
HAVING availability >0 
ORDER BY dist_app ASC
LIMIT 1 ;
END $$
DELIMITER ;

