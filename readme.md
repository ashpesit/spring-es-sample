## A Search Application which uses Elasticsearch ##
***
#### Link to Swagger, to visually understand all the Controllers, their APIs and their corresponding details - [Swagger UI](http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/swagger-ui.html#/) ###
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

##### Generic Search API request object #####
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
Request Curls
```text
Search By applicant name
curl --location --request GET 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/search/applicant/Eva%27s%20Catering'

Search By GeoLocation
curl --location --request GET 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/search/geo-distance?lat=37.71644300214742&lon=-122.38993787932108'

Search By StreetName
curl --location --request GET 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/search/street?name=JAMESTOWN'

Search By Expiry| Is expired : v=true; Not expired: v=false
curl --location --request GET 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/search/expired?v=false'

Generic Search Api
curl --location --request POST 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/search' \
--header 'Content-Type: application/json' \
--data-raw '{
    "queries": [{
        "key":"locationid",
        "operator":"EQ",
        "value":["1660620"]
    }],
    "limit": 1,
    "fields":["locationid","Applicant","FacilityType","cnn","LocationDescription"]
}'

Create New Entry
curl --location --request POST 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "lot": "4",
    "permit": "22MFF-00073",
    "dayshours": "",
    "locationid": 1660620,
    "Applicant": "Eva'\''s Catering",
    "FacilityType": "Truck",
    "cnn": 3081000,
    "LocationDescription": "Some day road",
    "Address": "630 MAMAM ST",
    "blocklot": "3778004",
    "block": "3778",
    "Status": "APPROVED",
    "FoodItems": "Cold Truck: Burrito: Corn Dog: Salads: Sandwiches: Quesadilla: Tacos: Fried Rice: Cow Mein: Chinese Rice: Noodle Plates: Soup: Bacon: Eggs: Ham: Avacado: Sausages: Beverages",
    "X": 6012488.815,
    "Y": 2110632.864,
    "Latitude": 37.77609875,
    "Longitude": -122.4002872,
    "Schedule": "http://bsm.sfdpw.org/PermitsTracker/reports/report.aspx?title=schedule&report=rptSchedule&params=permit=22MFF-00076&ExportPDF=1&Filename=22MFF-00076_schedule.pdf",
    "NOISent": "",
    "Approved": "11/21/2022 00:00:00",
    "Received": 20221121,
    "PriorPermit": 1,
    "ExpirationDate": "11/15/2023 0:00:00",
    "Location": "37.77609875315735, -122.40028723986815",
    "FirePreventionDistricts": 14,
    "PoliceDistricts": 2,
    "SupervisorDistricts": 9,
    "ZipCodes": 28856,
    "Neighborhoods": 34
}'

Update Existing entry
curl --location --request PATCH 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "7L7d2IQBYFpnUHSZkhYm",
    "lot": "4",
    "permit": "22MFF-00073",
    "dayshours": "",
    "locationid": 1660620,
    "Applicant": "Eva'\''s Catering"
}'

Read Doc By Id
curl --location --request GET 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/Lb7X14QBYFpnUHSZohZ5'

Delete Doc By Id
curl --location --request DELETE 'http://springeb-env.eba-6psgyhye.ap-south-1.elasticbeanstalk.com/poc/mobile-food-facility/v1/Lb7X14QBYFpnUHSZohZ5'


```

### How will this scale ###
As any scalable system should be, this project is completely stateless.  
Main challenge would be to scale the database. Scaling elasticsearch is mainly twofold.
* Scaling the throughput
* Scaling the data volume

For scaling the data ingestion (write throughput), I would need keep an optimal number shards in my index.  
It will also help if I can figure out how to divide the data into multiple index. For that I would have to decide the index pattern. Best approach for this is to look at the query pattern and decide the pattern in such a way that result of a single query can be fetched from the least no of index.  
Since we have query patterns which uses different fields of the document, it is not feasible to use any one field value in the index pattern.  
For scaling the read throughput, we can keep an optimal no of replica shards. Replica shards can't be used for writing the data but reading if possible in both primary and replica shards.   

For scaling the data volume, again the solution is to divide the data into multiple index by using index pattern. 

