package com.jump.batch.obj;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public interface AssetKVRepository extends JpaRepository<Asset, Integer> {
    Asset getByLabel(final String label);
}
