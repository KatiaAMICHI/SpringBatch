package com.jump.objects.asset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetKVRepository extends JpaRepository<Asset, Integer> {
    Asset getByLabel(final String label);
}
