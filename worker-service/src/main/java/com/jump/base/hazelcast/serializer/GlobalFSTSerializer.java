/*
 *      _   _   _       ___  ___   _____            ___       ___  ___   _____
 *     | | | | | |     /   |/   | |  _  \          /   |     /   |/   | /  ___/
 *     | | | | | |    / /|   /| | | |_| |         / /| |    / /|   /| | | |___
 *  _  | | | | | |   / / |__/ | | |  ___/        / / | |   / / |__/ | | \___  \
 * | |_| | | |_| |  / /       | | | |           / /  | |  / /       | |  ___| |
 * \_____/ \_____/ /_/        |_| |_|          /_/   |_| /_/        |_| /_____/
 *
 *
 * Jump Asset Management Solution Jump Informatique. Tous droits réservés.
 * Ce programme est protégé par la loi relative au droit d'auteur et par les conventions internationales.
 * Toute reproduction ou distribution partielle ou totale du logiciel, par quelque moyen que ce soit, est
 * strictement interdite. Toute personne ne respectant pas ces dispositions se rendra coupable du délit de
 * contrefaçon et sera passible des sanctions pénales prévues par la loi.
 * daté du 13/05/2020.
 */
package com.jump.base.hazelcast.serializer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;


@Slf4j
public final class GlobalFSTSerializer<T> implements Serializer<T> {

  private static ThreadLocal<FSTConfiguration> conf = ThreadLocal.withInitial(FSTConfiguration::createUnsafeBinaryConfiguration);

  @SneakyThrows
  @Override
  public byte[] serialize(String s, Object object) {
    try {
      return conf.get().asByteArray(object);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IOException(e);
    }

  }
}
