Computer Monitor Server
=======================
All information is available via a REST API at port 28622. Here are usage examples:

 * GET /about: Returns a JSON containing the server version.
 * GET /: Returns a simple web interface containing all info.
 * GET /data?type=TYPE: Returns JSON data about hardware "TYPE".
 
Possible hardware types are:

 * CPU
 * GPU
 * system
 * disks
 * processes
 
Additionally, type 'processes' has an optional parameter called 'sort' with two possible values:

 * name
 * usage