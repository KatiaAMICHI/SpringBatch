package com.jump.batch.obj;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User:  t.riquet<thibaut.riquet@jump-informatique.com>
 * Date: 12/06/2019
 */
@ResponseBody
@RequestMapping
public interface AssetKVRepository extends JpaRepository<Asset, Integer> {
}
