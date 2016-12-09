CREATE DATABASE `iblue` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE `iblue_tmp` /*!40100 DEFAULT CHARACTER SET latin1 */;

--------------------------------------------------------------------

CREATE USER 'iblueuser'@'%' IDENTIFIED BY 'zaq1xsw2';
GRANT USAGE ON *.* TO 'iblueuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `iblue`.* TO 'iblueuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON `iblue_tmp`.* TO 'iblueuser'@'%';

--------------------------------------------------------------------

-- store parking spots reported by users
-- DROP TABLE `iblue`.`spots`;
CREATE TABLE `iblue`.`spots` (
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

CREATE INDEX status ON `iblue`.`spots` (int_status);
CREATE INDEX ts_create ON `iblue`.`spots` (ts_create);
CREATE INDEX fk_id ON `iblue`.`spots` (fk_street_id);

-- store the map of streets
-- drop TABLE `iblue`.`streets`;
CREATE TABLE `iblue`.`streets` (
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
  
  `int_status` tinyint(4) DEFAULT NULL,
  `ts_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

CREATE INDEX street_lat_long_1 ON `iblue`.`streets`(float_latitude_1, float_longitude_1);
CREATE INDEX status ON `iblue`.`streets` (int_status);


--------------------------------------------------------------------


-- DROP TRIGGER iblue.calc_l_g_m;
CREATE TRIGGER iblue.calc_l_g_m BEFORE INSERT ON `iblue`.`streets`
FOR EACH ROW
SET 
	new.float_least_easting = least(new.float_easting_1, new.float_easting_2),
    new.float_greatest_easting = greatest(new.float_easting_1, new.float_easting_2),
    new.float_mid_easting = (new.float_easting_1+new.float_easting_2)/2,
    new.float_least_northing = least(new.float_northing_1, new.float_northing_2),
    new.float_greatest_northing = greatest(new.float_northing_1, new.float_northing_2),
    new.float_mid_northing = (new.float_northing_1+new.float_northing_2)/2;

--------------------------------------------------------------------

-- delete from iblue.spots where pk_id>1;
-- delete from iblue.streets where pk_id>1;

-- DROP VIEW `iblue`.`vw_in_use_spots`;
CREATE VIEW `iblue`.`vw_in_use_spots` AS
SELECT sp.fk_street_id, count(*) in_use_spots
FROM iblue.spots sp
WHERE
	sp.int_status = 1
GROUP BY sp.fk_street_id;


--------------------------------------------------------------------


-- DROP PROCEDURE `iblue`.`nearest_street` ;
DELIMITER $$
CREATE PROCEDURE `iblue`.`nearest_street` 
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
FROM iblue.streets st
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.01 AND lat + 0.01
    AND st.float_longitude_1 BETWEEN lon - 0.01 AND lon + 0.01
    AND st.int_status = 1
ORDER BY dist ASC
LIMIT 1;
END $$
DELIMITER ;
/*
DELIMITER $$
CREATE PROCEDURE `iblue`.`nearest_street` 
(IN lat FLOAT(10,6), IN lon FLOAT(10,6), IN northing FLOAT(16,6), IN easting FLOAT(16,6))
BEGIN
SELECT 
	st.pk_id, 
	ABS(st.float_line_coeff_a*easting + st.float_line_coeff_b*northing+st.float_line_coeff_c) / st.float_line_sqrt_a2_b2 
    +
    CASE
		WHEN easting BETWEEN least(st.float_easting_1, st.float_easting_2) 
			AND greatest(st.float_easting_1, st.float_easting_2) THEN 0
		ELSE 2
	END 
    +
	CASE
		WHEN northing BETWEEN least(st.float_northing_1, st.float_northing_2) 
			AND greatest(st.float_northing_1, st.float_northing_2) THEN 0
		ELSE 2
	END 
    as dist
FROM iblue.streets st
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.01 AND lat + 0.01
    AND st.float_longitude_1 BETWEEN lon - 0.01 AND lon + 0.01
    AND st.int_status = 1
ORDER BY dist ASC
LIMIT 1;
END $$
DELIMITER ;
*/

-- DROP PROCEDURE `iblue`.`area_map`;
DELIMITER $$
CREATE PROCEDURE `iblue`.`area_map`
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
FROM iblue.streets st
	LEFT JOIN iblue.vw_in_use_spots usp
		ON st.pk_id = usp.fk_street_id
WHERE 
	st.float_latitude_1 BETWEEN lat - 0.1 AND lat + 0.1
    AND st.float_longitude_1 BETWEEN lon - 0.1 AND lon + 0.1
    AND st.int_status = 1;
END $$
DELIMITER ;



--------------------------------------------------------------------

--
-- Bulk load of streets
--

-- drop table iblue_tmp.nodes ;
create table iblue_tmp.nodes 
(
id_node bigint,
lat float(10,7),
lon float(10,7),
primary key(id_node)
) ;

-- drop table iblue_tmp.ways ;
create table iblue_tmp.ways
(
id_from bigint,
id_to bigint,
-- id int auto_increment,
-- primary key (id)
primary key(id_from, id_to)
) ;

create view iblue_tmp.street_load as
select 
    n_f.lat as lat_from
    ,n_f.lon as lon_from
    ,n_t.lat as lat_to
    ,n_t.lon as lon_to
from iblue_tmp.ways w
	join iblue_tmp.nodes n_f
		on w.id_from = n_f.id_node
	join iblue_tmp.nodes n_t
		on w.id_to = n_t.id_node ;

drop table iblue_tmp.dedup_ways;

set @cnt=0; 

create table iblue_tmp.dedup_ways as
select w.id_from, w.id_to, w.id, @cnt:=@cnt+1 as line
from iblue_tmp.ways w
	join(
	select id_from, id_to, count(*) cnt
	from iblue_tmp.ways
	group by 1,2
	having cnt>1
	) d
		on w.id_from = d.id_from
		and w.id_to = d.id_to
order by 1,2,3
;

SET SQL_SAFE_UPDATES = 0;

delete from iblue_tmp.ways 
where 
id in (select id
	from iblue_tmp.dedup_ways
	where line%2=0);



--
-- Update streets capacity based on distance
--
-- drop table iblue_tmp.streets_cap;
create table iblue_tmp.streets_cap as
select 
	pk_id
	,case 
		when len<20 then 0 
        else cast( floor((len-20) / 6) as signed)
	end as cap
from
	(
	select 
		pk_id
		,sqrt(POW(float_easting_1 - float_easting_2, 2) + POW(float_northing_1 - float_northing_2,2)) as len
	from iblue.streets
	) a ;

SET SQL_SAFE_UPDATES = 0;

update iblue.streets s, iblue_tmp.streets_cap c
set 
	s.int_capacity = c.cap
where 
	s.pk_id = c.pk_id ;
