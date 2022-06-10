/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockByKey utility allows to synchronize access to code based on arbitrary String keys.
 *
 * @see <a href="https://www.baeldung.com/java-acquire-lock-by-key">java-acquire-lock-by-key</a>
 */
public class LockByKeyUtil {
  private static ConcurrentHashMap<String, LockWrapper> locks = new ConcurrentHashMap<>();

  public void lock(String key) {
    final LockWrapper lockWrapper =
        locks.compute(key, (k, v) -> v == null ? new LockWrapper() : v.addThreadInQueue());
    lockWrapper.lock.lock();
  }

  public void unlock(String key) {
    LockWrapper lockWrapper = locks.get(key);
    lockWrapper.lock.unlock();
    if (lockWrapper.removeThreadFromQueue() == 0) {
      // Remove specific value, as they may be already a new lock
      locks.remove(key, lockWrapper);
    }
  }

  /** Wraps ReentrantLock and thread queue counter. */
  private static class LockWrapper {
    private final Lock lock = new ReentrantLock();
    private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(1);

    private LockWrapper addThreadInQueue() {
      numberOfThreadsInQueue.incrementAndGet();
      return this;
    }

    private int removeThreadFromQueue() {
      return numberOfThreadsInQueue.decrementAndGet();
    }
  }
}
