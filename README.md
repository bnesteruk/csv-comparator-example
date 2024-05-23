# S3 - csv-comparator

## Description

Project classes: 

* [CSVService.java](src/main/java/com/bnesteruk/csv/CSVService.java) - responsible for CSV diff search
* [S3Service.java](src/main/java/com/bnesteruk/csv/S3Service.java) - responsible for S3 connection handling and objects fetching
* [Main.java](src/main/java/com/bnesteruk/csv/Main.java) - entry point 

2 csv files have been uploaded to the test S3 bucket. 

File keys: 
- `com.bnesteruk.csv/people_data_A.csv`
- `com.bnesteruk.csv/people_data_B.csv`

The files are also available as examples in the [src/main/resources](src/main/resources) folder

### The logic:

The content of files is slightly different and  some additional rows exist in one of files for the demonstration purposes.
The project connects to the S3 bucket via the IAM user's secret key and:

1. Fetches both csv files specified
2. Converts both csv files into lists of objects
3. Iterates the lists
4. If the lists have different length:
   1. Each extra object is added to the diff with the `<+>` marks
5. If a difference is found in any of object's fields:
   1. the missing value is marked with `<->`
   2. the new value is marked with `<+>`
  
### Comparing example: 

```json
        {                                       
                  "First Name": "Olivia",       
                  "Last Name": "Lopez",         
                - "Age": "28",                  
                + "Age": "21"                   
                  "Occupation": "Dancer",       
                  "Company": "PerformArts",     
        },                                      
        {                                       
                  "First Name": "Isabella",     
                  "Last Name": "Young",         
                  "Age": "25",                  
                - "Occupation": "Editor",       
                + "Occupation": "null"          
                  "Company": "NewAge Media",    
        },                                      
        {                                       
                  "First Name": "Emily",        
                  "Last Name": "Evans",         
                  "Age": "32",                  
                  "Occupation": "Social Worker",
                - "Company": "HelpHands",       
                + "Company": "Film Studios"
        },
        {
                  + "First Name": "Victoria",
                  + "Last Name": "Reed",
                  + "Age": "44",
                  + "Occupation": "Doctor",
                  + "Company": "Wellness Clinic",
        },

```







## Installation

* JDK 17 is required
* Make sure the actual IAM user and bucket details are specified in the  [resources/config.properties](src/main/resources/config.properties)




## Usage

### To run via CLI: 

* Navigate to the [demo](demo) folder 
* `java -jar csv-comparator-1.0-SNAPSHOT.jar fileAKey=com.bnesteruk.csv/people_data_A.csv fileBKey=com.bnesteruk.csv/people_data_B.csv`

### To run just the main method 

Make sure you uncomment the `fileAKey`, `fileBKey` at `com/bnesteruk/csv/Main.java:5`

## Expected output: 



## Contact

- [Bohdan Nesteruk](mailto:bohdan.nesteruk@gmail.com)