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

import co.cask.tephra.TxConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.filter.Filter;

/**
 * Supports use of empty values (empty {@code byte[]}) to identify delete markers.
 */
public class EmptyValueDeleteStrategy implements DeleteStrategy {
  private final boolean clearDeletes;
  private final boolean allowEmptyValues;

  public EmptyValueDeleteStrategy(boolean clearDeletes, Configuration conf) {
    this.clearDeletes = clearDeletes;
    this.allowEmptyValues = conf.getBoolean(TxConstants.ALLOW_EMPTY_VALUES_KEY,
                                            TxConstants.ALLOW_EMPTY_VALUES_DEFAULT);
  }

  @Override
  public boolean isDelete(KeyValue keyValue) {
    return keyValue.getValueLength() == 0 && !allowEmptyValues;
  }

  @Override
  public Filter.ReturnCode getReturnCode() {
    if (clearDeletes) {
      // skip "deleted" cell
      return Filter.ReturnCode.NEXT_COL;
    } else {
      // keep the marker but skip any remaining versions
      return Filter.ReturnCode.INCLUDE_AND_NEXT_COL;
    }
  }

  @Override
  public void reset() {
    // no-op
  }
}
