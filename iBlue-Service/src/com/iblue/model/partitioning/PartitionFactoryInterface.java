package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.List;

public interface PartitionFactoryInterface {

	public PartitionTileInterface loadFromDb();

	public PartitionTileInterface loadFromConfiguration(List<BigDecimal> latRanges, List<BigDecimal> lonRanges);
}
