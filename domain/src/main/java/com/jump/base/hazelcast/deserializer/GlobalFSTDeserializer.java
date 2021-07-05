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
package com.jump.base.hazelcast.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.nustaq.serialization.FSTConfiguration;



@Slf4j
public final class GlobalFSTDeserializer<T> implements Deserializer<T> {

  private static ThreadLocal<FSTConfiguration> conf = ThreadLocal.withInitial(FSTConfiguration::createUnsafeBinaryConfiguration);

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(String s, byte[] bytes) {
    return null == bytes ? null : (T)conf.get().asObject(bytes);
  }
}
