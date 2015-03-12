/*
 * Copyright © 2015 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.tephra.hbase94.coprocessor;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.filter.Filter;

/**
 * Interface to make delete marker handling pluggable.
 */
public interface DeleteStrategy {
  /**
   * Returns true is the current cell should be interpreted as a delete marker.  If this returns true,
   * {@link #getReturnCode()} will be called to determine what the wrapping filter should return.
   * @param keyValue the KeyValue to check
   * @return true if the KeyValue represents a delete marker
   */
  boolean isDelete(KeyValue keyValue);

  /**
   * Returns the filter return code which should be emitted.
   */
  Filter.ReturnCode getReturnCode();

  /**
   * Called between rows to reset internal state.  This method should be called when the current row changes
   * in a scanner context, or when {@code Filter.reset()} is called.
   */
  void reset();
}
