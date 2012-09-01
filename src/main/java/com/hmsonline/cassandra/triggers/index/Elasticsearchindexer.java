package com.hmsonline.cassandra.triggers.index;

import org.json.simple.JSONValue;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.HashMap;


public class Elasticsearchindexer implements Indexer {


    public Elasticsearchindexer() {

    }


    public void index(String columnFamily, String rowKey, String json) {
    HashMap row = (HashMap) JSONValue.parse(json);
    index(columnFamily, rowKey, row);
  }

  @SuppressWarnings("unchecked")
  public void index(String columnFamily, String rowKey, HashMap row) {

      String rowkeyz=rowKey;
      String idDoc=this.getDocumentId(columnFamily,rowKey);
      String ColumnFamilyz=columnFamily;


      HashMap columz =  new HashMap();{
          for (Object column : row.keySet())
              columz.put(column.toString().toLowerCase(), row.get(column));
      }




      // Index using Elasticsearch
    try {
          Sixnineidmath sixnineidmath = new Sixnineidmath(1,1);{

          sixnineidmath.nextId();
        }

        TransportClient client = new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build());
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        IndexResponse response = client.prepareIndex("twitter", "tweet", JSONValue.toJSONString(sixnineidmath.nextId()) )
                .setSource(jsonBuilder()
                        .startObject()
                        .field("Columnfamily: ", ColumnFamilyz)
                        .field("idDoc: ", idDoc)
                        .field("rowKey: ", rowkeyz)
                        .field("column: ", columz)
                        .endObject()
                )
                .execute()
                .actionGet();
        System.out.println("Indexed.Check Elasticsearch ");
      client.close();
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