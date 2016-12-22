package com.iblue.model.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.internal.util.SerializationHelper;

import com.iblue.model.Tile;
import com.iblue.model.TileContainerInterface;
import com.iblue.model.db.service.DbSchema;
import com.iblue.utils.Pair;

@Entity
@Table(name = "tile_container", schema = DbSchema.DB_SCHEMA)
public class TileContainer implements Serializable, TileContainerInterface {

	private static final long serialVersionUID = -1863411281979689540L;
	@Id
	@Column(name = "pk_latitude_id")
	private long idLatitude;
	@Id
	@Column(name = "pk_longitude_id")
	private long idLongitude;
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;

	@Column(name = "byte_tile")
	@Lob
	private byte[] byteTile;
	@Transient
	private Tile tile;

	@Transient
	public void setTileId(Pair<Long, Long> tileId) {
		idLatitude = tileId.getFirst();
		idLongitude = tileId.getSecond();
	}

	@Transient
	public Pair<Long, Long> getTileId() {
		return new Pair<Long, Long>(idLatitude, idLongitude);
	}

	public long getIdLatitude() {
		return idLatitude;
	}

	public void setIdLatitude(long idLatitude) {
		this.idLatitude = idLatitude;
	}

	public long getIdLongitude() {
		return idLongitude;
	}

	public void setIdLongitude(long idLongitude) {
		this.idLongitude = idLongitude;
	}

	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
	}

	public void setTile(Tile tile) {
		this.byteTile = SerializationHelper.serialize(tile);
		this.tile = tile;
	}

	public void setByteTile(byte[] byteObject) {
		this.byteTile = byteObject;
		this.tile = (Tile) SerializationHelper.deserialize(byteTile);
	}

	public Tile getTile() {
		if(tile==null && byteTile!=null) {
			this.tile = (Tile) SerializationHelper.deserialize(byteTile);
		}
		return tile;
	}
	
	public byte[] getByteTile() {
		return byteTile;
	}

}
