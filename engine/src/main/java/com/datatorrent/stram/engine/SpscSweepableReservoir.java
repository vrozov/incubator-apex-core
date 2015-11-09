/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.stram.engine;

import org.jctools.queues.SpscArrayQueue;

import com.datatorrent.stram.tuple.Tuple;

import static java.lang.Thread.sleep;

public class SpscSweepableReservoir extends AbstractReservoir
{
  private final SpscArrayQueue<Object> queue;

  SpscSweepableReservoir(final String id, final int capacity)
  {
    super(id);
    queue = new SpscArrayQueue<>(capacity);
  }

  @Override
  public Tuple sweep()
  {
    Object o;
    while ((o = queue.peek()) != null) {
      if (o instanceof Tuple) {
        return (Tuple)o;
      }
      count++;
      sink.put(queue.poll());
    }
    return null;
  }

  @Override
  public int size()
  {
    return queue.size();
  }

  @Override
  public boolean isEmpty()
  {
    return queue.peek() == null;
  }

  @Override
  public Object remove()
  {
    return queue.poll();
  }

  @Override
  public boolean add(Object o)
  {
    return queue.add(o);
  }

  @Override
  public void put(final Object o) throws InterruptedException
  {
    while (!queue.offer(o))
    {
      sleep(10);
    }
  }

}
