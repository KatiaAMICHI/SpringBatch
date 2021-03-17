package com.jump.objects.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AssetKVRepository extends JpaRepository<Asset, Integer> {
    Asset getByLabel(final String label);
}
