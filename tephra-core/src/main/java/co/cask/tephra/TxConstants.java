/*
 * Copyright © 2012-2014 Cask Data, Inc.
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

package co.cask.tephra;

import co.cask.tephra.snapshot.DefaultSnapshotCodec;
import co.cask.tephra.snapshot.SnapshotCodecV2;

import java.util.concurrent.TimeUnit;

/**
 * Transaction system constants
 */
public class TxConstants {
  /**
   * Defines what level of conflict detection should be used for transactions.  {@code ROW} means that only the
   * table name and row key for each change will be used to determine if the transaction change sets conflict.
   * {@code COLUMN} means that the table name, row key, column family, and column qualifier will all be used to
   * identify write conflicts.  The default value used by {@code TransactionAwareHTable} implementations is
   * {@code COLUMN}.
   *
   * <p>
   * <strong>Note: for a given table, all clients must use the same conflict detection setting!</strong>
   * Otherwise conflicts will not be flagged correctly.
   * </p>
   */
  public enum ConflictDetection {
    ROW,
    COLUMN
  }

  /**
   * property set for {@code org.apache.hadoop.hbase.HColumnDescriptor} to configure time-to-live on data within
   * the column family.  The value given is in milliseconds.  Once a cell's data has surpassed the given value in age,
   * the cell's data will no longer be visible and may be garbage collected.
   */
  public static final String PROPERTY_TTL = "dataset.table.ttl";

  /**
   * This is how many tx we allow per millisecond, if you care about the system for 100 years:
   * Long.MAX_VALUE / (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365 * 100)) =
   * (as of Feb 20, 2014) 2,028,653. It is safe and convenient to use 1,000,000 as multiplier:
   * <ul>
   *   <li>
   *     we hardly can do more than 1 billion txs per second
   *   </li>
   *   <li>
   *     long value will not overflow for 200 years
   *   </li>
   *   <li>
   *     makes reading & debugging easier if multiplier is 10^n
   *   </li>
   * </ul>
   */
  public static final long MAX_TX_PER_MS = 1000000;

  /**
   * Since HBase {@code Delete} operations cannot be undone at the same timestamp, "deleted" data is instead
   * overwritten with an empty {@code byte[]} to flag it as removed.  Cells with empty values will be filtered out
   * of the results for read operations.  If cells with empty values should be included in results (meaning data
   * cannot be transactionally deleted), then set this configuration property to true.
   */
  public static final String ALLOW_EMPTY_VALUES_KEY = "data.tx.allow.empty.values";
  public static final boolean ALLOW_EMPTY_VALUES_DEFAULT = false;

  /**
   * Key used to set the serialized transaction as an attribute on Get and Scan operations.
   */
  public static final String TX_OPERATION_ATTRIBUTE_KEY = "cask.tx";

  // Constants for monitoring status
  public static final String STATUS_OK = "OK";
  public static final String STATUS_NOTOK = "NOTOK";

  /**
   * TransactionManager configuration.
   */
  public static final class Manager {
    // TransactionManager configuration
    public static final String CFG_DO_PERSIST = "tx.persist";
    /** Directory in HDFS used for transaction snapshot and log storage. */
    public static final String CFG_TX_SNAPSHOT_DIR = "data.tx.snapshot.dir";
    /** Directory on the local filesystem used for transaction snapshot and log storage. */
    public static final String CFG_TX_SNAPSHOT_LOCAL_DIR = "data.tx.snapshot.local.dir";
    /** How often to clean up timed out transactions, in seconds, or 0 for no cleanup. */
    public static final String CFG_TX_CLEANUP_INTERVAL = "data.tx.cleanup.interval";
    /** The user id to access HDFS if not running in secure HDFS. */
    public static final String CFG_TX_HDFS_USER = "data.tx.hdfs.user";
    /** Default value for how often to check in-progress transactions for expiration, in seconds. */
    public static final int DEFAULT_TX_CLEANUP_INTERVAL = 10;
    /**
     * The timeout for a transaction, in seconds. If the transaction is not finished in that time,
     * it is marked invalid.
     */
    public static final String CFG_TX_TIMEOUT = "data.tx.timeout";
    /** Default value for transaction timeout, in seconds. */
    public static final int DEFAULT_TX_TIMEOUT = 30;
    /**
     * The timeout for a long running transaction, in seconds. If the transaction is not finished in that time,
     * it is marked invalid.
     */
    public static final String CFG_TX_LONG_TIMEOUT = "data.tx.long.timeout";
    /** Default value for long running transaction timeout, in seconds. */
    public static final int DEFAULT_TX_LONG_TIMEOUT = (int) TimeUnit.DAYS.toSeconds(1);
    /** The frequency (in seconds) to perform periodic snapshots, or 0 for no periodic snapshots. */
    public static final String CFG_TX_SNAPSHOT_INTERVAL = "data.tx.snapshot.interval";
    /** Default value for frequency of periodic snapshots of transaction state. */
    public static final long DEFAULT_TX_SNAPSHOT_INTERVAL = 300;
    /** Number of most recent transaction snapshots to retain. */
    public static final String CFG_TX_SNAPSHOT_RETAIN = "data.tx.snapshot.retain";
    /** Default value for number of most recent snapshots to retain. */
    public static final int DEFAULT_TX_SNAPSHOT_RETAIN = 10;
  }

  /**
   * TransactionService configuration.
   */
  public static final class Service {

    /** for the zookeeper quorum string for leader election for tx server. */
    public static final String CFG_DATA_TX_ZOOKEEPER_QUORUM
      = "data.tx.zookeeper.quorum";

    /** for the name used to announce service availability to discovery service */
    public static final String CFG_DATA_TX_DISCOVERY_SERVICE_NAME
      = "data.tx.discovery.service.name";

    /** for the port of the tx server. */
    public static final String CFG_DATA_TX_BIND_PORT
      = "data.tx.bind.port";

    /** for the address (hostname) of the tx server. */
    public static final String CFG_DATA_TX_BIND_ADDRESS
      = "data.tx.bind.address";

    /** the number of IO threads in the tx service. */
    public static final String CFG_DATA_TX_SERVER_IO_THREADS
      = "data.tx.server.io.threads";

    /** the number of handler threads in the tx service. */
    public static final String CFG_DATA_TX_SERVER_THREADS
      = "data.tx.server.threads";

    public static final String CFG_DATA_TX_THRIFT_MAX_READ_BUFFER
      = "data.tx.thrift.max.read.buffer";

    public static final String DEFAULT_DATA_TX_DISCOVERY_SERVICE_NAME
      = "transaction";

    /** default tx service port. */
    public static final int DEFAULT_DATA_TX_BIND_PORT
      = 15165;

    /** default tx service address. */
    public static final String DEFAULT_DATA_TX_BIND_ADDRESS
      = "0.0.0.0";

    /** default number of handler IO threads in tx service. */
    public static final int DEFAULT_DATA_TX_SERVER_IO_THREADS
      = 2;

    /** default number of handler threads in tx service. */
    public static final int DEFAULT_DATA_TX_SERVER_THREADS
      = 20;

    /** default thrift max read buffer size */
    public static final int DEFAULT_DATA_TX_THRIFT_MAX_READ_BUFFER
      = 16 * 1024 * 1024;

    // Configuration key names and defaults used by tx client.

    /** to specify the tx client socket timeout in ms. */
    public static final String CFG_DATA_TX_CLIENT_TIMEOUT
      = "data.tx.client.timeout";

    /** to specify the tx client provider strategy. */
    public static final String CFG_DATA_TX_CLIENT_PROVIDER
      = "data.tx.client.provider";

    /** to specify the number of threads for client provider "pool". */
    public static final String CFG_DATA_TX_CLIENT_COUNT
      = "data.tx.client.count";

    /** to specify the retry strategy for a failed thrift call. */
    public static final String CFG_DATA_TX_CLIENT_RETRY_STRATEGY
      = "data.tx.client.retry.strategy";

    /** to specify the number of times to retry a failed thrift call. */
    public static final String CFG_DATA_TX_CLIENT_ATTEMPTS
      = "data.tx.client.retry.attempts";

    /** to specify the initial sleep time for retry strategy backoff. */
    public static final String CFG_DATA_TX_CLIENT_BACKOFF_INIITIAL
      = "data.tx.client.retry.backoff.initial";

    /** to specify the backoff factor for retry strategy backoff. */
    public static final String CFG_DATA_TX_CLIENT_BACKOFF_FACTOR
      = "data.tx.client.retry.backoff.factor";

    /** to specify the sleep time limit for retry strategy backoff. */
    public static final String CFG_DATA_TX_CLIENT_BACKOFF_LIMIT
      = "data.tx.client.retry.backoff.limit";

    /** the default tx client socket timeout in milli seconds. */
    public static final int DEFAULT_DATA_TX_CLIENT_TIMEOUT
      = 30 * 1000;

    /** default number of pooled tx clients. */
    public static final int DEFAULT_DATA_TX_CLIENT_COUNT
      = 5;

    /** default tx client provider strategy. */
    public static final String DEFAULT_DATA_TX_CLIENT_PROVIDER
      = "pool";

    /** retry strategy for thrift clients, e.g. backoff, or n-times. */
    public static final String DEFAULT_DATA_TX_CLIENT_RETRY_STRATEGY
      = "backoff";

    /** default number of attempts for strategy n-times. */
    public static final int DEFAULT_DATA_TX_CLIENT_ATTEMPTS
      = 2;

    /** default initial sleep is 100ms. */
    public static final int DEFAULT_DATA_TX_CLIENT_BACKOFF_INIITIAL
      = 100;

    /** default backoff factor is 4. */
    public static final int DEFAULT_DATA_TX_CLIENT_BACKOFF_FACTOR
      = 4;

    /** default sleep limit is 30 sec. */
    public static final int DEFAULT_DATA_TX_CLIENT_BACKOFF_LIMIT
      = 30 * 1000;
  }

  /**
   * Configuration properties for metrics reporting
   */
  public static final class Metrics {
    /**
     * Frequency at which metrics should be reported, in seconds.
     */
    public static final String REPORT_PERIOD_KEY = "data.tx.metrics.period";
    /**
     * Default report period for metrics, in seconds.
     */
    public static final int REPORT_PERIOD_DEFAULT = 60;
  }

  /**
   * Configuration properties used by HBase
   */
  public static final class HBase {
    public static final String ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    public static final String ZK_SESSION_TIMEOUT = "zookeeper.session.timeout";
    public static final int DEFAULT_ZK_SESSION_TIMEOUT = 180 * 1000;
  }

  /**
   * Configuration for the TransactionDataJanitor coprocessor.
   */
  public static final class DataJanitor {
    /**
     * Whether or not the TransactionDataJanitor coprocessor should be enabled on tables.
     * Disable for testing.
     */
    public static final String CFG_TX_JANITOR_ENABLE = "data.tx.janitor.enable";
    public static final boolean DEFAULT_TX_JANITOR_ENABLE = true;
  }

  /**
   * Configuration for the transaction snapshot persistence.
   */
  public static final class Persist {
    /**
     * The class names of all known transaction snapshot codecs.
     */
    public static final String CFG_TX_SNAPHOT_CODEC_CLASSES = "data.tx.snapshot.codecs";
    public static final Class[] DEFAULT_TX_SNAPHOT_CODEC_CLASSES = 
      { DefaultSnapshotCodec.class, SnapshotCodecV2.class };
  }

}
