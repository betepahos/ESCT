package com.hmsonline.cassandra.triggers.index;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.jackson.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;


public class Elasticsearchindexer implements Indexer {


    public Elasticsearchindexer() {

    }



    public void index(String columnFamily, String rowKey, String json) {
    JSONObject row = (JSONObject) JSONValue.parse(json);
    index(columnFamily, rowKey, row);
  }

  @SuppressWarnings("unchecked")
  public void index(String columnFamily, String rowKey, JSONObject row) {

      String rowkeyz=rowKey;
      String idDoc=this.getDocumentId(columnFamily,rowKey);
      String ColumnFamilyz=columnFamily;


      JSONObject columz =  new JSONObject();{
          for (Object column : row.keySet())
              columz.put(column.toString().toLowerCase(), row.get(column));
      }



      /*
      JSONObject document = new JSONObject();

    document.put("id", this.getDocumentId(columnFamily, rowKey));
    document.put("rowKey" , rowKey);
    document.put("columnFamily" , columnFamily);
    for (Object column : row.keySet()) {
      document.put(column.toString().toLowerCase(), row.get(column));
    }
      */

    // Index using Elasticsearch
    try {
        TransportClient client = new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build());
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        IndexResponse response = client.prepareIndex("twitter", "tweet", "23")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("CF", ColumnFamilyz)
                        .field("id", idDoc)
                        .field("rowKey", rowkeyz)
                        .field("column", columz)
                        .endObject()
                )
                .execute()
                .actionGet();
        System.out.println("Indexed.Check Elasticsearch");
    }
    catch (Exception ex) {
      throw new RuntimeException("Could not send index update request", ex);
    }

  }

  public void delete(String columnFamily, String rowKey) {

    String query = "id:" + this.getDocumentId(columnFamily, rowKey);
  }

  private String getDocumentId(String columnFamily, String rowKey) {
    return columnFamily + "." + rowKey;
  }

}