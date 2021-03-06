package gov.usgs.volcanoes.swarm.data;

import gov.usgs.volcanoes.core.data.HelicorderData;
import gov.usgs.volcanoes.core.data.Scnl;
import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.util.UtilException;
import gov.usgs.volcanoes.winston.Channel;
import gov.usgs.volcanoes.winston.db.Channels;
import gov.usgs.volcanoes.winston.db.Data;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of <code>SeismicDataSource</code> that communicates
 * directly with a Winston database. Essentially identical to
 * DirectWinstonSource.
 *
 *
 * @author Dan Cervelli
 */
public class DirectWwsSource extends SeismicDataSource {
  private String dbDriver;
  private String dbUrl;
  private String dbPrefix;

  private WinstonDatabase winston;
  private Data data;
  private Channels stations;

  // explicit default constructor required for reflection
  public DirectWwsSource() {}

  /**
   * Parse data source parameters.
   * @see gov.usgs.volcanoes.swarm.data.SeismicDataSource#parse(java.lang.String)
   */
  public void parse(String params) {
    String[] ss = params.split("\\|");
    dbDriver = ss[0];
    dbUrl = ss[1];
    dbPrefix = ss[2];

    winston = new WinstonDatabase(dbDriver, dbUrl, dbPrefix);
    stations = new Channels(winston);
    data = new Data(winston);
  }

  public void close() {
    winston.close();
  }

  /**
   * Get wave.
   * @see gov.usgs.volcanoes.swarm.data.SeismicDataSource#getWave(java.lang.String, double, double)
   */
  public synchronized Wave getWave(String station, double t1, double t2) {
    CachedDataSource cache = CachedDataSource.getInstance();

    Wave sw = cache.getWave(station, t1, t2);
    if (sw == null) {
      try {
        sw = data.getWave(station, t1, t2, 0);
      } catch (UtilException e) {
        //
      }
      if (sw != null && !sw.isData()) {
        sw = null;
      }
      if (sw != null && sw.buffer != null && sw.buffer.length > 0) {
        cache.putWave(station, sw);
      }
    }
    return sw;
  }

  /**
   * Get channels.
   * @see gov.usgs.volcanoes.swarm.data.SeismicDataSource#getChannels()
   */
  public synchronized List<String> getChannels() {
    List<Channel> chs = stations.getChannels();
    List<String> result = new ArrayList<String>();
    for (Channel ch : chs) {
      result.add(ch.toString());
    }
    return result;
  }

  /**
   * Get helicorder.
   * @see gov.usgs.volcanoes.swarm.data.SeismicDataSource#getHelicorder
   * (java.lang.String, double, double, gov.usgs.volcanoes.swarm.data.GulperListener)
   */
  public synchronized HelicorderData getHelicorder(String station, double t1, double t2,
      GulperListener gl) {
    CachedDataSource cache = CachedDataSource.getInstance();
    HelicorderData hd = cache.getHelicorder(station, t1, t2, this);
    if (hd == null) {
      try {
        hd = data.getHelicorderData(Scnl.parse(station), t1, t2, 0);
      } catch (UtilException e) {
        //
      }
      if (hd != null && hd.rows() != 0) {
        cache.putHelicorder(station, hd);
      } else {
        hd = null;
      }
    }
    return hd;
  }

  public String toConfigString() {
    String typeString = DataSourceType.getShortName(this.getClass());
    return String.format("%s;%s:%s|%s|%s", name, typeString, dbDriver, dbUrl, dbPrefix);
  }
}
