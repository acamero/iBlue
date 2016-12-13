INSERT INTO `map_fdm`.`geo_street_weights` (fk_weight_type_id, fk_geo_street_id, float_weight)
SELECT 
  8 as fk_weight_type_id,
  st.pk_id as fk_geo_street_id,  
  sqrt( pow(fri.float_easting - toi.float_easting,2) + pow(fri.float_northing-toi.float_northing, 2) ) as dist
FROM `map_fdm`.`geo_streets` st
  JOIN `map_fdm`.`intersections` fri
    ON st.fk_intersection_from_id = fri.pk_id
  JOIN `map_fdm`.`intersections` toi
    ON st.fk_intersection_to_id = toi.pk_id
;
