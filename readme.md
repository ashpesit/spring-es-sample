## A Search Application which uses Elasticsearch ##
***
#### Link to Swagger, to visually understand all the Controllers, their APIs and their corresponding details - [Swagger UI](http://15.206.168.189:8090/poc/swagger-ui.html#/) ###
***


### What does this application do? ###
I started this project with these initial requirements in mind.  
Given the data about Food Trucks in San Francisco : here (https://data.sfgov.org/Economy-and-Community/Mobile-Food-Facility-Permit/rqzj-sfat)  
I want to build below functionality

* Search by name of applicant
* Search by expiration date, to find whose permits have expired
* Search by street name
* Add new food truck entry to the dataset
* Given a delivery location, find out the closest truck possible.


### HLD ###
From the get go, I was in inclined towards using elasticsearch as primary database. Elasticsearch is an excellent choose when it comes to:-
* multiple attribute document search
* full-text / wildcard / fuzzy search

Other contender for database where combination of MongoDb and redis. Redis also has a support for geohash/proximity calculation.
MongoDb can store a ton of data and is highly scalable. But problem with these two is that doing full-text searching is very inefficient in either of these two database
Hence, Elasticsearch became the natural choice, as it can easily suffice all the search requirement of this project.

### LLD ###

#### Packages information ####

Type of Class               | Package                                | Usage 
--------------------------  |----------------------------------------| -------------  
Controllers                 | com.saisonomni.poc.controller          | Api controller classes
Configurations              | com.saisonomni.poc.config              | Swagger, Elasticsearch configes
Services                    | com.saisonomni.poc.manager             | Crud and search manager
Request                     | com.saisonomni.poc.request     |         request objects
Responses                   | com.saisonomni.poc.responses   |         resposne objects
Database Entities           | com.saisonomni.poc.elastic.entity  |     database entities
Repositories                | com.saisonomni.poc.elastic.repositories |database repositories
Utilities                   | in.mygate.inventory.utils              | Uitility classes

First we have our basic curd operation to create, read, update, delete documents from elasticsearch.
I also imported all the data to my free-tier aws-elasticsearch 7.10 using elasticdump (https://github.com/elasticsearch-dump/elasticsearch-dump)
For search, I made a generic search api which can be used to query any field in my elasticsearch index and is very flexible in terms of all logical and conditional operators.
```json
{
  "queries": [{         // list of boolean query to be applied
    "key":"locationid", // Field name of which query condition is applied
    "operator":"EQ",    // Operation type eg EQ, GTE, LTE, LIKE
    "value":["1660620"] // Value to be applied on the condition
  }],
  "limit": 1,           // no of document to be fetched
  "page": 1,            // page no of the request
  "fields":["locationid","Applicant","FacilityType","cnn","LocationDescription"], // list of fields that is to be fetched
  "sorts": [            // List of sorting to be applied on the result of the query
    {
      "key": "Latitude", // Field name on which sorting is applied
      "values": [37,-122],// Value to be user only in case of GEO_DISTANCE_ASC & GEO_DISTANCE_DESC sorting
      "sortType": "GEO_DISTANCE_ASC" // sorting type
    }
  ]
}
```
All the other search apis are using generic search api for fetching relevant result from the database.

POSTMAN collect for testing the api (https://api.postman.com/collections/1107465-e00f5de8-2794-4206-981f-fd365adf0ca7?access_key=PMAT-01GKCE020DYCMJWP4823RNWX0E)